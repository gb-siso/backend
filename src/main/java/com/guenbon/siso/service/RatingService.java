package com.guenbon.siso.service;

import com.guenbon.siso.dto.cursor.count.CountCursor;
import com.guenbon.siso.dto.cursor.count.DecryptedCountCursor;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public void create(final Long memberId, final Long congressmanId) {
        final Member member = memberService.findById(memberId);
        final Congressman congressman = congressmanService.findById(congressmanId);
        validateDuplicated(member, congressman);
        ratingRepository.save(Rating.builder()
                .member(member)
                .congressman(congressman)
                .build());
    }

    private void validateDuplicated(final Member member, final Congressman congressman) {
        if (ratingRepository.existsByMemberAndCongressman(member, congressman)) {
            throw new BadRequestException(RatingErrorCode.DUPLICATED);
        }
    }


    public RatingListDTO validateAndGetRecentRatings(final String encryptedCongressmanId, final Pageable pageable,
                                                     final CountCursor countCursor) {
        final Long congressmanId = aesUtil.decrypt(encryptedCongressmanId);
        final DecryptedCountCursor decryptedCountCursor = countCursor == null ? null : DecryptedCountCursor.of(
                aesUtil.decrypt(countCursor.getIdCursor()),
                countCursor.getCountCursor());
        validatePageRequest(pageable);

        final List<Rating> recentRatingByCongressmanId = ratingRepository.getSortedRatingsByCongressmanId(congressmanId,
                pageable, decryptedCountCursor);
        final List<RatingDetailDTO> ratingDetailDTOList = toRatingDetailDTOList(recentRatingByCongressmanId);
        return RatingListDTO.of(ratingDetailDTOList, getCountCursor(pageable, ratingDetailDTOList));
    }

    private static void validatePageRequest(final Pageable pageRequest) {
        if (pageRequest == null) {
            throw new BadRequestException(CommonErrorCode.NULL_VALUE_NOT_ALLOWED);
        }
    }

    private List<RatingDetailDTO> toRatingDetailDTOList(final List<Rating> recentRatingByCongressmanId) {
        return recentRatingByCongressmanId.stream()
                .map(rating ->
                        RatingDetailDTO.from(
                                rating,
                                MemberDTO.of(rating.getMember(), aesUtil.encrypt(rating.getMember().getId())),
                                aesUtil.encrypt(rating.getId())
                        )).toList();
    }

    private static CountCursor getCountCursor(final Pageable pageable,
                                              final List<RatingDetailDTO> ratingDetailDTOList) {
        final int pageSize = pageable.getPageSize();
        if (ratingDetailDTOList.size() > pageSize) {
            final RatingDetailDTO lastElement = ratingDetailDTOList.get(pageSize);
            Sort sort = pageable.getSort();
            if (sort.getOrderFor("like") != null) {
                return CountCursor.of(lastElement.getId(), lastElement.getLikeCount());
            }
            if (sort.getOrderFor("dislike") != null) {
                return CountCursor.of(lastElement.getId(), lastElement.getDislikeCount());
            }
            return CountCursor.of(lastElement.getId(), lastElement.getTopicality());
        }
        return null;
    }
}