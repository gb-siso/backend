package com.guenbon.siso.service.reaction;

import com.guenbon.siso.dto.reaction.response.ReactionDTO;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.entity.dislike.RatingDislike;
import com.guenbon.siso.entity.like.RatingLike;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.reaction.RatingDisLikeErrorCode;
import com.guenbon.siso.repository.dislike.rating.RatingDislikeRepository;
import com.guenbon.siso.repository.like.rating.RatingLikeRepository;
import com.guenbon.siso.service.member.MemberService;
import com.guenbon.siso.service.rating.RatingService;
import com.guenbon.siso.support.constants.ReactionStatus;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingDisLikeServiceTest {
    @InjectMocks
    RatingDisLikeService ratingDisLikeService;
    @Mock
    AESUtil aesUtil;
    @Mock
    RatingLikeRepository ratingLikeRepository;
    @Mock
    RatingDislikeRepository ratingDislikeRepository;
    @Mock
    MemberService memberService;
    @Mock
    RatingService ratingService;

    @Test
    @DisplayName("이미 싫어요 누른 평가에 대해 create 호출할 경우 DUPLICATED 예외를 던진다.")
    void duplicated_create_duplicatedErrorCode() {
        // given
        final String encryptedRatingId = "encryptedRatingId";
        final Long ratingId = 1L;
        final Long memberId = 2L;
        final Member member = MemberFixture.builder().setId(memberId).build();
        final Rating rating = RatingFixture.builder().setId(ratingId).setMember(member).build();

        when(aesUtil.decrypt(encryptedRatingId)).thenReturn(ratingId);
        when(memberService.findById(memberId)).thenReturn(member);
        when(ratingService.findById(ratingId)).thenReturn(rating);
        when(ratingDislikeRepository.existsByRatingIdAndMemberId(ratingId, memberId)).thenReturn(true);

        // when // then
        assertThatThrownBy(() -> ratingDisLikeService.create(encryptedRatingId, memberId))
                .isInstanceOf(CustomException.class)
                .hasMessage(RatingDisLikeErrorCode.DUPLICATED.getMessage());
    }

    @Test
    @DisplayName("좋아요 누른 평가에 대해 create 호출할 경우 좋아요를 삭제하고 싫어요를 생성한다.")
    void pushedLike_create_deleteLikeAndCreateDisLike() {
        // given
        final String encryptedRatingId = "encryptedRatingId";
        final Long ratingId = 1L;
        final Long memberId = 2L;
        final Member member = MemberFixture.builder().setId(memberId).build();
        final Rating rating = RatingFixture.builder().setId(ratingId).setMember(member).build();
        final RatingLike ratingLike = RatingLike.builder().rating(rating).member(member).build();
        final RatingDislike ratingDislike = RatingDislike.builder().rating(rating).member(member).build();

        when(aesUtil.decrypt(encryptedRatingId)).thenReturn(ratingId);
        when(memberService.findById(memberId)).thenReturn(member);
        when(ratingService.findById(ratingId)).thenReturn(rating);
        when(ratingDislikeRepository.existsByRatingIdAndMemberId(ratingId, memberId)).thenReturn(false);
        when(ratingDislikeRepository.save(any(RatingDislike.class))).thenReturn(ratingDislike);
        when(ratingLikeRepository.findByRatingIdAndMemberId(ratingId, memberId)).thenReturn(Optional.of(ratingLike));

        // when
        ReactionDTO actual = ratingDisLikeService.create(encryptedRatingId, memberId);

        // then
        assertAll(
                () -> assertThat(actual.getDislike().getStatus()).isEqualTo(ReactionStatus.CREATED),
                () -> assertThat(actual.getLike().getStatus()).isEqualTo(ReactionStatus.DELETED)
        );
    }

    @Test
    @DisplayName("아무것도 안 누른 상태로 create 호출할 시 싫어요를 생성한다.")
    void nothing_create_createDisLike() {
        // given
        final String encryptedRatingId = "encryptedRatingId";
        final Long ratingId = 1L;
        final Long memberId = 2L;
        final Member member = MemberFixture.builder().setId(memberId).build();
        final Rating rating = RatingFixture.builder().setId(ratingId).setMember(member).build();
        final RatingDislike ratingDislike = RatingDislike.builder().rating(rating).member(member).build();

        when(aesUtil.decrypt(encryptedRatingId)).thenReturn(ratingId);
        when(memberService.findById(memberId)).thenReturn(member);
        when(ratingService.findById(ratingId)).thenReturn(rating);
        when(ratingDislikeRepository.existsByRatingIdAndMemberId(ratingId, memberId)).thenReturn(false);
        when(ratingDislikeRepository.save(any(RatingDislike.class))).thenReturn(ratingDislike);
        when(ratingLikeRepository.findByRatingIdAndMemberId(ratingId, memberId)).thenReturn(Optional.empty());
        // when
        ReactionDTO actual = ratingDisLikeService.create(encryptedRatingId, memberId);
        // then
        assertAll(
                () -> assertThat(actual.getLike().getStatus()).isEqualTo(ReactionStatus.NONE),
                () -> assertThat(actual.getDislike().getStatus()).isEqualTo(ReactionStatus.CREATED)
        );
    }

    @Test
    @DisplayName("싫어요 안 누른 평가에 대해 싫어요 delete 호출할 경우 NOT_DISLIKED 예외를 던진다.")
    void notLiked_delete_notLikedErrorCode() {
        // given
        final String encryptedRatingId = "encryptedRatingId";
        final Long ratingId = 1L;
        final Long memberId = 2L;

        // mock stubbing 보류
        when(aesUtil.decrypt(encryptedRatingId)).thenReturn(ratingId);
        when(ratingDislikeRepository.findByRatingIdAndMemberId(ratingId, memberId)).thenReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> ratingDisLikeService.delete(encryptedRatingId, memberId))
                .isInstanceOf(CustomException.class)
                .hasMessage(RatingDisLikeErrorCode.NOT_DISLIKED.getMessage());
    }

    @Test
    @DisplayName("내가 싫어요 누른 평가에 대해 싫어요 delete 호출할 경우 성공한다. 응답 싫어요 상태는 DELETED다.")
    void myDisLike_delete_success() {
        // given
        final String encryptedRatingId = "encryptedRatingId";
        final Long ratingId = 1L;
        final Long memberId = 2L;
        final Member member = MemberFixture.builder().setId(memberId).build();
        final Rating rating = RatingFixture.builder().setId(ratingId).setMember(member).build();
        final RatingDislike ratingLike = RatingDislike.builder().rating(rating).member(member).build();

        // mock stubbing 보류
        when(aesUtil.decrypt(encryptedRatingId)).thenReturn(ratingId);
        when(ratingDislikeRepository.findByRatingIdAndMemberId(ratingId, memberId)).thenReturn(Optional.of(ratingLike));

        // when
        ReactionDTO actual = ratingDisLikeService.delete(encryptedRatingId, memberId);
        // then
        assertAll(
                () -> assertThat(actual.getLike().getStatus()).isEqualTo(ReactionStatus.NONE),
                () -> assertThat(actual.getDislike().getStatus()).isEqualTo(ReactionStatus.DELETED)
        );
    }
}