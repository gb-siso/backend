package com.guenbon.siso.service;

import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.RatingErrorCode;
import com.guenbon.siso.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final MemberService memberService;
    private final CongressmanService congressmanService;

    @Transactional(readOnly = false)
    public void create(Long memberId, Long congressmanId) {
        final Member member = memberService.findById(memberId);
        final Congressman congressman = congressmanService.findById(congressmanId);
        if (ratingRepository.existsByMemberAndCongressman(member, congressman)) {
            throw new BadRequestException(RatingErrorCode.DUPLICATED);
        }
        ratingRepository.save(Rating.builder()
                .member(member)
                .congressman(congressman)
                .build());
    }
}
