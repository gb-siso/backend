package com.guenbon.siso.service.rating;

import com.guenbon.siso.dto.cursor.count.CountCursor;
import com.guenbon.siso.dto.cursor.count.DecryptedCountCursor;
import com.guenbon.siso.dto.member.common.MemberDTO;
import com.guenbon.siso.dto.rating.request.RatingWriteDTO;
import com.guenbon.siso.dto.rating.response.RatingDetailDTO;
import com.guenbon.siso.dto.rating.response.RatingListDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.RatingErrorCode;
import com.guenbon.siso.repository.rating.RatingRepository;
import com.guenbon.siso.service.congressman.CongressmanService;
import com.guenbon.siso.service.member.MemberService;
import com.guenbon.siso.util.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class RatingService {

    public static final String SORT_LIKE = "like";
    public static final String SORT_DISLIKE = "dislike";
    public static final String SORT_TOPICALITY = "topicality";
    public static final String SORT_REG_DATE = "regDate";
    public static final int ZERO = 0;
    private final RatingRepository ratingRepository;
    private final MemberService memberService;
    private final CongressmanService congressmanService;
    private final AESUtil aesUtil;

    @Transactional(readOnly = false)
    public String create(final Long memberId, RatingWriteDTO ratingWriteDTO) {
        final Member member = memberService.findById(memberId);
        final Congressman congressman = congressmanService.findById(aesUtil.decrypt(ratingWriteDTO.getCongressmanId()));
        validateDuplicated(member, congressman);
        ratingRepository.save(Rating.builder()
                .member(member)
                .congressman(congressman)
                .content(ratingWriteDTO.getContent())
                .rate(ratingWriteDTO.getRating())
                .build());
        return ratingWriteDTO.getCongressmanId();
    }

    private void validateDuplicated(final Member member, final Congressman congressman) {
        if (ratingRepository.existsByMemberAndCongressman(member, congressman)) {
            throw new CustomException(RatingErrorCode.DUPLICATED);
        }
    }

    public RatingListDTO validateAndGetRecentRatings(final String encryptedCongressmanId, final Pageable pageable,
                                                     final CountCursor countCursor) {
        final Long congressmanId = aesUtil.decrypt(encryptedCongressmanId);
        final DecryptedCountCursor decryptedCountCursor = getDecryptedCountCursor(countCursor);

        final List<Rating> recentRatingByCongressmanId = ratingRepository.getSortedRatingsByCongressmanId(congressmanId,
                pageable, decryptedCountCursor);
        final List<RatingDetailDTO> ratingDetailDTOList = toRatingDetailDTOList(recentRatingByCongressmanId);
        return RatingListDTO.of(ratingDetailDTOList, getCountCursor(pageable, ratingDetailDTOList));
    }

    private DecryptedCountCursor getDecryptedCountCursor(CountCursor countCursor) {
        return countCursor == null || countCursor.isAllFieldInvalid() ? null
                : DecryptedCountCursor.of(aesUtil.decrypt(countCursor.getIdCursor()), countCursor.getCountCursor());
    }

    private List<RatingDetailDTO> toRatingDetailDTOList(final List<Rating> recentRatingByCongressmanId) {
        return recentRatingByCongressmanId.stream()
                .map(this::toRatingDetailDTO)
                .toList();
    }

    private RatingDetailDTO toRatingDetailDTO(final Rating rating) {
        return RatingDetailDTO.from(
                rating,
                MemberDTO.of(rating.getMember(), aesUtil.encrypt(rating.getMember().getId())),
                aesUtil.encrypt(rating.getId())
        );
    }

    private static CountCursor getCountCursor(final Pageable pageable,
                                              final List<RatingDetailDTO> ratingDetailDTOList) {
        final int pageSize = pageable.getPageSize();
        if (ratingDetailDTOList.size() > pageSize) {
            final RatingDetailDTO lastElement = ratingDetailDTOList.get(pageSize);
            Sort sort = pageable.getSort();
            return switch (sort.getOrderFor(SORT_LIKE) != null ? SORT_LIKE
                    : sort.getOrderFor(SORT_DISLIKE) != null ? SORT_DISLIKE
                    : sort.getOrderFor(SORT_REG_DATE) != null ? SORT_REG_DATE
                    : SORT_TOPICALITY) {
                case SORT_LIKE -> new CountCursor(lastElement.getId(), lastElement.getLikeCount());
                case SORT_DISLIKE -> new CountCursor(lastElement.getId(), lastElement.getDislikeCount());
                case SORT_REG_DATE -> new CountCursor(lastElement.getId(), ZERO);
                default -> new CountCursor(lastElement.getId(), lastElement.getTopicality());
            };
        }
        return null;
    }

    // todo 테스트 작성 필요
    public Rating findById(Long id) {
        return ratingRepository.findById(id).orElseThrow(() -> new CustomException(RatingErrorCode.NOT_EXISTS));
    }
}
