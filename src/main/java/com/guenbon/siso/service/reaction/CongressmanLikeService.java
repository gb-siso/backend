package com.guenbon.siso.service.reaction;

import com.guenbon.siso.dto.reaction.response.ReactionDTO;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.congressman.Congressman;
import com.guenbon.siso.entity.like.CongressmanLike;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.reaction.CongressmanLikeErrorCode;
import com.guenbon.siso.repository.dislike.congressman.CongressmanDislikeRepository;
import com.guenbon.siso.repository.like.congressman.CongressmanLikeRepository;
import com.guenbon.siso.service.congressman.CongressmanService;
import com.guenbon.siso.service.member.MemberService;
import com.guenbon.siso.support.constants.ReactionStatus;
import com.guenbon.siso.util.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class CongressmanLikeService {

    private final CongressmanLikeRepository congressmanLikeRepository;
    private final CongressmanDislikeRepository congressmanDislikeRepository;
    private final AESUtil aesUtil;
    private final CongressmanService congressmanService;
    private final MemberService memberService;

    public ReactionDTO create(String encryptedRatingId, Long memberId) {
        final Long congressmanId = aesUtil.decrypt(encryptedRatingId);
        final Member member = memberService.findById(memberId);
        final Congressman congressman = congressmanService.findById(congressmanId);

        // 중복 좋아요 예외처리
        if (congressmanLikeRepository.existsByCongressmanIdAndMemberId(congressmanId, memberId)) {
            throw new CustomException(CongressmanLikeErrorCode.DUPLICATED);
        }

        // 좋아요 생성
        CongressmanLike congressmanLike = congressmanLikeRepository.save(CongressmanLike.builder().congressman(congressman).member(member).build());

        // 싫어요 있으면 삭제
        return congressmanDislikeRepository.findByCongressmanIdAndMemberId(congressmanId, memberId)
                .map(ratingDislike -> {
                    congressmanDislikeRepository.delete(ratingDislike);
                    return ReactionDTO.of(aesUtil.encrypt(congressmanId),
                            ReactionDTO.Reaction.of(aesUtil.encrypt(congressmanLike.getId()), ReactionStatus.CREATED),
                            ReactionDTO.Reaction.of(aesUtil.encrypt(ratingDislike.getId()), ReactionStatus.DELETED)
                    );
                })
                .orElseGet(() -> ReactionDTO.of(aesUtil.encrypt(congressmanId),
                        ReactionDTO.Reaction.of(aesUtil.encrypt(congressmanLike.getId()), ReactionStatus.CREATED),
                        ReactionDTO.Reaction.none()));
    }

    public ReactionDTO delete(String encryptedCongressmanId, Long memberId) {
        final Long congressmanId = aesUtil.decrypt(encryptedCongressmanId);
        // 좋아요 누르지 않았는데 좋아요 해제 요청 시 예외처리
        final CongressmanLike congressmanLike = congressmanLikeRepository.findByCongressmanIdAndMemberId(congressmanId, memberId).orElseThrow(() -> new CustomException(CongressmanLikeErrorCode.NOT_LIKED));
        congressmanLikeRepository.delete(congressmanLike);
        return ReactionDTO.of(
                aesUtil.encrypt(congressmanId),
                ReactionDTO.Reaction.of(aesUtil.encrypt(congressmanLike.getId()), ReactionStatus.DELETED),
                ReactionDTO.Reaction.none()
        );
    }
}
