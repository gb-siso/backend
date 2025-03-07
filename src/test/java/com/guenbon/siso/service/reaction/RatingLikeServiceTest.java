package com.guenbon.siso.service.reaction;

import com.guenbon.siso.dto.reaction.response.RatingReactionDTO;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.entity.dislike.RatingDislike;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.reaction.RatingLikeErrorCode;
import com.guenbon.siso.repository.dislike.RatingDislikeRepository;
import com.guenbon.siso.repository.like.RatingLikeRepository;
import com.guenbon.siso.support.constants.ReactionStatus;
import com.guenbon.siso.support.fixture.dislike.RatingDislikeFixture;
import com.guenbon.siso.support.fixture.like.RatingLikeFixture;
import com.guenbon.siso.support.fixture.member.MemberFixture;
import com.guenbon.siso.support.fixture.rating.RatingFixture;
import com.guenbon.siso.util.AESUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingLikeServiceTest {
    @InjectMocks
    RatingLikeService ratingLikeService;
    @Mock
    AESUtil aesUtil;
    @Mock
    RatingLikeRepository ratingLikeRepository;
    @Mock
    RatingDislikeRepository ratingDislikeRepository;

    @Test
    @DisplayName("이미 좋아요 누른 평가에 대해 create 호출할 경우 DUPLICATED 예외를 던진다.")
    void duplicated_create_duplicatedErrorCode() {
        // given
        final String encryptedRatingId = "encryptedRatingId";
        final Long ratingId = 1L;
        final Long memberId = 2L;

        when(aesUtil.decrypt(encryptedRatingId)).thenReturn(ratingId);
        when(ratingLikeRepository.findByRatingIdAndMemberId(ratingId, memberId)).thenReturn(Optional.of(RatingLikeFixture.builder().build()));

        // when // then
        assertThrows(CustomException.class, () -> ratingLikeService.create(encryptedRatingId, memberId), RatingLikeErrorCode.DUPLICATED.getMessage());
    }

    @Test
    @DisplayName("싫어요 누른 평가에 대해 create 호출할 경우 싫어요를 삭제하고 좋아요를 생성한다.")
    void pushedDislike_create_deleteDislikeAndCreateLike() {
        // given
        final String encryptedRatingId = "encryptedRatingId";
        final Long ratingId = 1L;
        final Long memberId = 2L;
        final Member member = MemberFixture.builder().setId(memberId).build();
        final Rating rating = RatingFixture.builder().setId(ratingId).setMember(member).build();

        final RatingDislike ratingDislike = RatingDislikeFixture.builder().setRating(rating).setMember(member).build();

        when(aesUtil.decrypt(encryptedRatingId)).thenReturn(ratingId);
        when(ratingLikeRepository.findByRatingIdAndMemberId(ratingId, memberId)).thenReturn(Optional.empty());
        when(ratingDislikeRepository.findByRatingIdAndMemberId(ratingId, memberId)).thenReturn(Optional.of(ratingDislike));

        // when // then
        assertThrows(CustomException.class, () -> ratingLikeService.create(encryptedRatingId, memberId), RatingLikeErrorCode.DUPLICATED.getMessage());
    }

    @Test
    @DisplayName("아무것도 안 누른 상태로 create 호출할 시 좋아요를 생성한다.")
    void nothing_create_createLike() {
        // given
        final String encryptedRatingId = "encryptedRatingId";
        final Long ratingId = 1L;
        final Long memberId = 2L;
        final Member member = MemberFixture.builder().setId(memberId).build();
        final Rating rating = RatingFixture.builder().setId(ratingId).setMember(member).build();
        // when
        RatingReactionDTO actual = ratingLikeService.create(encryptedRatingId, memberId);
        // then
        assertAll(
                () -> assertThat(actual.getLike().getStatus()).isEqualTo(ReactionStatus.CREATED),
                () -> assertThat(actual.getDislike().getStatus()).isEqualTo(ReactionStatus.NONE)
        );
    }
}