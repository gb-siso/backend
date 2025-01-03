package com.guenbon.siso.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.RatingErrorCode;
import com.guenbon.siso.repository.RatingRepository;
import com.guenbon.siso.support.fixture.congressman.CongressmanFixture;
import com.guenbon.siso.support.fixture.member.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

}