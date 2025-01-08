package com.guenbon.siso.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.guenbon.siso.dto.cursor.count.CountCursor;
import com.guenbon.siso.dto.cursor.count.DecryptedCountCursor;
import com.guenbon.siso.dto.rating.response.RatingListDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import com.guenbon.siso.exception.errorCode.RatingErrorCode;
import com.guenbon.siso.repository.rating.RatingRepository;
import com.guenbon.siso.support.fixture.congressman.CongressmanFixture;
import com.guenbon.siso.support.fixture.dislike.RatingDislikeFixture;
import com.guenbon.siso.support.fixture.like.RatingLikeFixture;
import com.guenbon.siso.support.fixture.member.MemberFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @InjectMocks
    RatingService ratingService;
    @Mock
    RatingRepository ratingRepository;
    @Mock
    MemberService memberService;
    @Mock
    CongressmanService congressmanService;
    @Mock
    AESUtil aesUtil;

    @Test
    void ratingService_null_아님() {
        assertThat(ratingService).isNotNull();
    }

    @Test
    @DisplayName("중복되는 memberId와 congressmanId에 대해 create 메서드 호출 시 BadRequestException을 던진다")
    void create_duplicate_DuplicateRatingException() {
        // given
        final Member 장몽이 = MemberFixture.builder()
                .setId(1L)
                .setNickname("장몽이")
                .build();
        final Congressman 이준석 = CongressmanFixture.builder()
                .setId(1L)
                .setName("이준석")
                .build();
        when(ratingRepository.existsByMemberAndCongressman(장몽이, 이준석)).thenReturn(true);
        when(memberService.findById(장몽이.getId())).thenReturn(장몽이);
        when(congressmanService.findById(이준석.getId())).thenReturn(이준석);

        // when, then
        assertThrows(BadRequestException.class, () -> ratingService.create(장몽이.getId(), 이준석.getId()),
                RatingErrorCode.DUPLICATED.getMessage());
    }

    @Test
    @DisplayName("중복되지 않는 memberId와 congressmanId에 대해 create 메서드 호출 시 Rating 생성에 성공한다")
    void create_Rating_success() {
        final Member 장몽이 = MemberFixture.builder()
                .setId(1L)
                .setNickname("장몽이")
                .build();
        final Congressman 이준석 = CongressmanFixture.builder()
                .setId(1L)
                .setName("이준석")
                .build();
        when(ratingRepository.existsByMemberAndCongressman(장몽이, 이준석)).thenReturn(false);
        when(memberService.findById(장몽이.getId())).thenReturn(장몽이);
        when(congressmanService.findById(이준석.getId())).thenReturn(이준석);

        assertDoesNotThrow(() -> ratingService.create(장몽이.getId(), 이준석.getId()));

        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @DisplayName("validateAndGetRecentRatings에 null congressmanId를 넣으면 BadRequestException을 던지고 에러코드는 NULL_VALUE_NOT_ALLOWED이다")
    @Test
    void getRecentRatingByCongressmanId_nullCongressmanId_BadRequestException() {
        // given
        final PageRequest pageable = createPageRequest("topicality", 0);
        when(aesUtil.decrypt(null)).thenThrow(new BadRequestException(CommonErrorCode.NULL_VALUE_NOT_ALLOWED));

        // when, then
        assertThrows(BadRequestException.class,
                () -> ratingService.validateAndGetRecentRatings(null, pageable, null),
                CommonErrorCode.NULL_VALUE_NOT_ALLOWED.getMessage());
    }

    @DisplayName("validateAndGetRecentRatings에 null pageable을 넣으면 BadRequestException을 던지고 에러코드는 NULL_VALUE_NOT_ALLOWED이다")
    @Test
    void getRecentRatingByCongressmanId_nullPageRequest_BadRequestException() {
        // given
        final String encryptedCongressmanId = "adksl123897adjadsjfkl";
        final Long congressmanId = 3L;
        when(aesUtil.decrypt(encryptedCongressmanId)).thenReturn(congressmanId);
        // when, then
        assertThrows(BadRequestException.class,
                () -> ratingService.validateAndGetRecentRatings(encryptedCongressmanId, null, null),
                CommonErrorCode.NULL_VALUE_NOT_ALLOWED.getMessage());
    }

    @DisplayName("validateAndGetRecentRatings에 유효한 congressmanId와 pageable을 넣으면 RatingListDTO를 반환한다")
    @Test
    void getRecentRatingByCongressmanId_validInput_RatingListDTO() {
        // given
        final String encryptedCongressmanId = "encryptedCongressmanId";
        final PageRequest page1 = createPageRequest("topicality", 0);
        final PageRequest page2 = createPageRequest("topicality", 1);
        final Long congressmanId = 3L;

        mockBehavior(encryptedCongressmanId, congressmanId, page1, page2);

        // when
        // page1 : 다음 페이지 존재 -> 커서값 존재
        final RatingListDTO resultPage1 = ratingService.validateAndGetRecentRatings(encryptedCongressmanId, page1,
                null);
        // page2 : 다음 페이지 없음 -> 커서값 null
        final RatingListDTO resultPage2 = ratingService.validateAndGetRecentRatings(encryptedCongressmanId, page2,
                CountCursor.of("4L", 4));

        // then
        assertAll(
                () -> assertThat(
                        resultPage1.getRatingList().stream().map(rating -> rating.getId()).toList())
                        .containsExactly("3L", "2L", "1L", "4L"),
                () -> assertThat(
                        resultPage1.getRatingList().stream().map(rating -> rating.getMember().getId()).toList())
                        .containsExactly("1L", "2L", "3L", "4L"),
                () -> assertThat(resultPage1.getCountCursor()).usingRecursiveComparison()
                        .isEqualTo(CountCursor.of("4L", 4)),
                () -> assertThat(
                        resultPage2.getRatingList().stream().map(rating -> rating.getId()).toList())
                        .containsExactly("3L", "2L", "1L"),
                () -> assertThat(
                        resultPage2.getRatingList().stream().map(rating -> rating.getMember().getId()).toList())
                        .containsExactly("1L", "2L", "3L"),
                () -> assertThat(
                        resultPage2.getCountCursor())
                        .isNull()
        );
    }

    private void mockBehavior(String encryptedCongressmanId, Long congressmanId, PageRequest page1, PageRequest page2) {

        // 공통 mock stubbing
        when(aesUtil.decrypt(encryptedCongressmanId)).thenReturn(congressmanId);
        when(aesUtil.encrypt(1L)).thenReturn("1L");
        when(aesUtil.encrypt(2L)).thenReturn("2L");
        when(aesUtil.encrypt(3L)).thenReturn("3L");
        when(aesUtil.encrypt(4L)).thenReturn("4L");

        // page 1 에 대해 mock stubbing
        when(ratingRepository.getSortedRatingsByCongressmanId(congressmanId, page1, null)).thenReturn(
                List.of(
                        Rating.builder().member(MemberFixture.fromId(1L)).id(3L).build(),
                        Rating.builder().member(MemberFixture.fromId(2L)).id(2L).build(),
                        Rating.builder().member(MemberFixture.fromId(3L)).id(1L).build(),
                        Rating.builder().member(MemberFixture.fromId(4L)).id(4L).ratingLikeList(
                                        List.of(RatingLikeFixture.builder().build(), RatingLikeFixture.builder().build()))
                                .ratingDislikeList(
                                        List.of(RatingDislikeFixture.builder().build(),
                                                RatingDislikeFixture.builder().build())).build()));

        // page 2 에 대해 mock stubbing
        when(aesUtil.decrypt("4L")).thenReturn(4L);
        when(ratingRepository.getSortedRatingsByCongressmanId(eq(congressmanId), eq(page2),
                any(DecryptedCountCursor.class)))
                .thenReturn(
                        List.of(
                                Rating.builder().member(MemberFixture.fromId(1L)).id(3L).build(),
                                Rating.builder().member(MemberFixture.fromId(2L)).id(2L).build(),
                                Rating.builder().member(MemberFixture.fromId(3L)).id(1L).build()));
    }

    private static PageRequest createPageRequest(final String sort, final Integer page) {
        return PageRequest.of(page, 3, Sort.by(sort).descending());
    }
}