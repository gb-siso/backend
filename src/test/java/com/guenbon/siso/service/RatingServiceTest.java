package com.guenbon.siso.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.guenbon.siso.dto.rating.response.RatingListDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import com.guenbon.siso.exception.errorCode.RatingErrorCode;
import com.guenbon.siso.repository.rating.RatingRepository;
import com.guenbon.siso.support.fixture.congressman.CongressmanFixture;
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
        final PageRequest pageRequest = createPageRequest("topicality");
        when(aesUtil.decrypt(null)).thenThrow(new BadRequestException(CommonErrorCode.NULL_VALUE_NOT_ALLOWED));

        // when, then
        assertThrows(BadRequestException.class, () -> ratingService.validateAndGetRecentRatings(null, pageRequest),
                CommonErrorCode.NULL_VALUE_NOT_ALLOWED.getMessage());
    }

    @DisplayName("validateAndGetRecentRatings에 null pageable을 넣으면 BadRequestException을 던지고 에러코드는 NULL_VALUE_NOT_ALLOWED이다")
    @Test
    void getRecentRatingByCongressmanId_nullPageRequest_BadRequestException() {
        // given
        final String encryptedCongressmanId = "adksl123897adjadsjfkl";
        final PageRequest pageRequest = createPageRequest("topicality");
        final Long congressmanId = 3L;
        when(aesUtil.decrypt(encryptedCongressmanId)).thenReturn(congressmanId);
        // when, then
        assertThrows(BadRequestException.class,
                () -> ratingService.validateAndGetRecentRatings(encryptedCongressmanId, null),
                CommonErrorCode.NULL_VALUE_NOT_ALLOWED.getMessage());
    }

    @DisplayName("validateAndGetRecentRatings에 유효한 congressmanId와 pageable을 넣으면 RatingListDTO를 반환한다")
    @Test
    void getRecentRatingByCongressmanId_validInput_RatingListDTO() {
        // given
        final String encryptedCongressmanId = "adksl123897adjadsjfkl";
        final PageRequest pageRequest = createPageRequest("topicality");
        final Long congressmanId = 3L;

        when(aesUtil.decrypt(encryptedCongressmanId)).thenReturn(congressmanId);
        when(ratingRepository.getRecentRatingByCongressmanId(congressmanId, pageRequest)).thenReturn(
                List.of(
                        Rating.builder().member(MemberFixture.builder().setId(1L).build()).id(3L).build(),
                        Rating.builder().member(MemberFixture.builder().setId(2L).build()).id(2L).build(),
                        Rating.builder().member(MemberFixture.builder().setId(3L).build()).id(1L).build(),
                        Rating.builder().member(MemberFixture.builder().setId(4L).build()).id(4L).build()
                )
        );
        when(aesUtil.encrypt(1L)).thenReturn("1L");
        when(aesUtil.encrypt(2L)).thenReturn("2L");
        when(aesUtil.encrypt(3L)).thenReturn("3L");
        when(aesUtil.encrypt(4L)).thenReturn("4L");

        // when
        final RatingListDTO result = ratingService.validateAndGetRecentRatings(encryptedCongressmanId, pageRequest);
        // then
        assertAll(
                () -> assertThat(
                        result.getRatingList().stream().map(rating -> rating.getId()).toList()).containsExactly("3L",
                        "2L", "1L", "4L"),
                () -> assertThat(
                        result.getRatingList().stream().map(rating -> rating.getMember().getId())
                                .toList()).containsExactly("1L", "2L", "3L", "4L"),
                () -> assertThat(
                        result.getIdCursor()).isEqualTo("4L"));
    }

    private static PageRequest createPageRequest(String sort) {
        return PageRequest.of(0, 3, Sort.by(sort).descending());
    }

}