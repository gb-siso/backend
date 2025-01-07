package com.guenbon.siso.service;

import com.guenbon.siso.dto.member.common.MemberDTO;
import com.guenbon.siso.dto.rating.response.RatingDetailDTO;
import com.guenbon.siso.dto.rating.response.RatingListDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import com.guenbon.siso.exception.errorCode.RatingErrorCode;
import com.guenbon.siso.repository.rating.RatingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final MemberService memberService;
    private final CongressmanService congressmanService;
    private final AESUtil aesUtil;

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


    public RatingListDTO validateAndGetRecentRatings(String encryptedCongressmanId, PageRequest pageRequest) {
        final Long congressmanId = aesUtil.decrypt(encryptedCongressmanId);
        if (pageRequest == null) {
            throw new BadRequestException(CommonErrorCode.NULL_VALUE_NOT_ALLOWED);
        }
        List<Rating> recentRatingByCongressmanId = ratingRepository.getRecentRatingByCongressmanId(congressmanId,
                pageRequest);
        List<RatingDetailDTO> ratingDetailDTOList = recentRatingByCongressmanId.stream()
                .map(rating ->
                        RatingDetailDTO.from(
                                rating,
                                MemberDTO.of(rating.getMember(), aesUtil.encrypt(rating.getMember().getId())),
                                aesUtil.encrypt(rating.getId())
                        )).toList();
        return RatingListDTO.of(ratingDetailDTOList, getIdCursor(pageRequest, ratingDetailDTOList));
    }

    private static String getIdCursor(PageRequest pageRequest, List<RatingDetailDTO> ratingDetailDTOList) {
        String idCursor = null;
        if (ratingDetailDTOList.size() > pageRequest.getPageSize()) {
            idCursor = ratingDetailDTOList.get(pageRequest.getPageSize()).getId();
        }
        return idCursor;
    }
}
