package com.guenbon.siso.service.reaction;

import com.guenbon.siso.dto.reaction.response.RatingReactionDTO;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.entity.dislike.RatingDislike;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.reaction.RatingDisLikeErrorCode;
import com.guenbon.siso.repository.MemberRepository;
import com.guenbon.siso.repository.dislike.RatingDislikeRepository;
import com.guenbon.siso.repository.like.RatingLikeRepository;
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
public class RatingDisLikeService {
    private final RatingLikeRepository ratingLikeRepository;
    private final RatingDislikeRepository ratingDislikeRepository;
    private final AESUtil aesUtil;
    private final RatingService ratingService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public RatingReactionDTO create(String encryptedRatingId, Long memberId) {
        final Long ratingId = aesUtil.decrypt(encryptedRatingId);
        final Member member = memberService.findById(memberId);
        final Rating rating = ratingService.findById(ratingId);

        // 중복 싫어요 예외처리
        if (ratingDislikeRepository.existsByRatingIdAndMemberId(ratingId, memberId)) {
            throw new CustomException(RatingDisLikeErrorCode.DUPLICATED);
        }

        // 싫어요 생성
        RatingDislike ratingDislike = ratingDislikeRepository.save(RatingDislike.builder().rating(rating).member(member).build());

        // 좋아요 있으면 삭제
        return ratingLikeRepository.findByRatingIdAndMemberId(ratingId, memberId)
                .map(ratingLike -> {
                    rating.removeLike(ratingLike);
                    ratingLikeRepository.delete(ratingLike);
                    return RatingReactionDTO.of(aesUtil.encrypt(ratingId),
                            RatingReactionDTO.Reaction.of(aesUtil.encrypt(ratingLike.getId()), ReactionStatus.DELETED),
                              RatingReactionDTO.Reaction.of(aesUtil.encrypt(ratingDislike.getId()), ReactionStatus.CREATED)
                    );
                })
                .orElseGet(() -> RatingReactionDTO.of(aesUtil.encrypt(ratingId),
                        RatingReactionDTO.Reaction.none(),
                        RatingReactionDTO.Reaction.of(aesUtil.encrypt(ratingDislike.getId()), ReactionStatus.CREATED)));
    }

    public RatingReactionDTO delete(String encryptedRatingId, Long memberId) {
        final Long ratingId = aesUtil.decrypt(encryptedRatingId);
        // 좋아요 누르지 않았는데 좋아요 해제 요청 시 예외처리
        final RatingDislike ratingDislike = ratingDislikeRepository.findByRatingIdAndMemberId(ratingId, memberId).orElseThrow(() -> new CustomException(RatingDisLikeErrorCode.NOT_DISLIKED));
        ratingDislikeRepository.delete(ratingDislike);
        return RatingReactionDTO.of(
                aesUtil.encrypt(ratingId),
                RatingReactionDTO.Reaction.none(),
                RatingReactionDTO.Reaction.of(aesUtil.encrypt(ratingDislike.getId()), ReactionStatus.DELETED)
        );
    }
}
