package com.guenbon.siso.service.reaction;

import com.guenbon.siso.dto.reaction.response.ReactionDTO;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.congressman.Congressman;
import com.guenbon.siso.entity.dislike.CongressmanDisLike;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.reaction.CongressmanDisLikeErrorCode;
import com.guenbon.siso.exception.errorCode.reaction.RatingDisLikeErrorCode;
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
public class CongressmanDisLikeService {
    private final CongressmanLikeRepository congressmanLikeRepository;
    private final CongressmanDislikeRepository congressmanDislikeRepository;
    private final AESUtil aesUtil;
    private final CongressmanService congressmanService;
    private final MemberService memberService;

    public ReactionDTO create(String encryptedCongressmanId, Long memberId) {
        final Long congressmanId = aesUtil.decrypt(encryptedCongressmanId);
        final Member member = memberService.findById(memberId);
        final Congressman congressman = congressmanService.findById(congressmanId);

        // 중복 싫어요 예외처리
        if (congressmanDislikeRepository.existsByCongressmanIdAndMemberId(congressmanId, memberId)) {
            throw new CustomException(CongressmanDisLikeErrorCode.DUPLICATED);
        }

        // 싫어요 생성
        CongressmanDisLike congressmanDisLike = congressmanDislikeRepository.save(CongressmanDisLike.builder().congressman(congressman).member(member).build());

        // 좋아요 있으면 삭제
        return congressmanLikeRepository.findByCongressmanIdAndMemberId(congressmanId, memberId)
                .map(congressmanLike -> {
                    congressmanLikeRepository.delete(congressmanLike);
                    return ReactionDTO.of(aesUtil.encrypt(congressmanId),
                            ReactionDTO.Reaction.of(aesUtil.encrypt(congressmanLike.getId()), ReactionStatus.DELETED),
                            ReactionDTO.Reaction.of(aesUtil.encrypt(congressmanLike.getId()), ReactionStatus.CREATED)
                    );
                })
                .orElseGet(() -> ReactionDTO.of(aesUtil.encrypt(congressmanId),
                        ReactionDTO.Reaction.none(),
                        ReactionDTO.Reaction.of(aesUtil.encrypt(congressmanDisLike.getId()), ReactionStatus.CREATED)));
    }

    public ReactionDTO delete(String encryptedCongressmanId, Long memberId) {
        final Long congressmanId = aesUtil.decrypt(encryptedCongressmanId);
        // 좋아요 누르지 않았는데 좋아요 해제 요청 시 예외처리
        final CongressmanDisLike congressmanDisLike = congressmanDislikeRepository.findByCongressmanIdAndMemberId(congressmanId, memberId).orElseThrow(() -> new CustomException(RatingDisLikeErrorCode.NOT_DISLIKED));
        congressmanDislikeRepository.delete(congressmanDisLike);
        return ReactionDTO.of(
                aesUtil.encrypt(congressmanId),
                ReactionDTO.Reaction.none(),
                ReactionDTO.Reaction.of(aesUtil.encrypt(congressmanDisLike.getId()), ReactionStatus.DELETED)
        );
    }
}
