package com.guenbon.siso.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.guenbon.siso.repository.RatingRepository;
import java.util.Optional;
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

    @Test
    void ratingService_null_아님() {
        assertThat(ratingService).isNotNull();
    }

    @Test
    @DisplayName("중복되는 memberId와 congressmanId에 대해 create 메서드 호출 시 DuplicateRatingException을 던진다")
    void create_duplicate_DuplicateRatingException() {
        // given
        final Long memberId = 1L;
        final Long congressmanId = 1L;

        when(ratingRepository.findByMemberIdAndCongressmanId(memberId,congressmanId)).thenReturn(Optional.empty());

        // when, then
        assertThrows(DuplicateRatingException.class, ratingService.create(memberId, congressmanId));
    }
}