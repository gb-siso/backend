package com.guenbon.siso.service.reaction;

import com.guenbon.siso.dto.reaction.response.ReactionDTO;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.entity.like.RatingLike;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.reaction.RatingLikeErrorCode;
import com.guenbon.siso.repository.dislike.rating.RatingDislikeRepository;
import com.guenbon.siso.repository.like.rating.RatingLikeRepository;
import com.guenbon.siso.service.member.MemberService;
import com.guenbon.siso.service.rating.RatingService;
import com.guenbon.siso.support.constants.ReactionStatus;
import com.guenbon.siso.util.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class RatingLikeService {

    private final RatingLikeRepository ratingLikeRepository;
    private final RatingDislikeRepository ratingDislikeRepository;
    private final AESUtil aesUtil;
    private final RatingService ratingService;
    private final MemberService memberService;

    public ReactionDTO create(String encryptedRatingId, Long memberId) {
        final Long ratingId = aesUtil.decrypt(encryptedRatingId);
        final Member member = memberService.findById(memberId);
        final Rating rating = ratingService.findById(ratingId);

        // 중복 좋아요 예외처리
        if (ratingLikeRepository.existsByRatingIdAndMemberId(ratingId, memberId)) {
            throw new CustomException(RatingLikeErrorCode.DUPLICATED);
        }

        // 좋아요 생성
        RatingLike ratingLike = ratingLikeRepository.save(RatingLike.builder().rating(rating).member(member).build());

        // 싫어요 있으면 삭제
        return ratingDislikeRepository.findByRatingIdAndMemberId(ratingId, memberId)
                .map(ratingDislike -> {
                    rating.removeDislike(ratingDislike);
                    ratingDislikeRepository.delete(ratingDislike);
                    return ReactionDTO.of(aesUtil.encrypt(ratingId),
                            ReactionDTO.Reaction.of(aesUtil.encrypt(ratingLike.getId()), ReactionStatus.CREATED),
                            ReactionDTO.Reaction.of(aesUtil.encrypt(ratingDislike.getId()), ReactionStatus.DELETED)
                    );
                })
                .orElseGet(() -> ReactionDTO.of(aesUtil.encrypt(ratingId),
                        ReactionDTO.Reaction.of(aesUtil.encrypt(ratingLike.getId()), ReactionStatus.CREATED),
                        ReactionDTO.Reaction.none()));
    }

    public ReactionDTO delete(String encryptedRatingId, Long memberId) {
        final Long ratingId = aesUtil.decrypt(encryptedRatingId);
        // 좋아요 누르지 않았는데 좋아요 해제 요청 시 예외처리
        final RatingLike ratingLike = ratingLikeRepository.findByRatingIdAndMemberId(ratingId, memberId).orElseThrow(() -> new CustomException(RatingLikeErrorCode.NOT_LIKED));
        ratingLikeRepository.delete(ratingLike);
        return ReactionDTO.of(
                aesUtil.encrypt(ratingId),
                ReactionDTO.Reaction.of(aesUtil.encrypt(ratingLike.getId()), ReactionStatus.DELETED),
                ReactionDTO.Reaction.none()
        );
    }
}
