package com.guenbon.siso.service.reaction;

import com.guenbon.siso.dto.reaction.response.RatingReactionDTO;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.entity.dislike.RatingDislike;
import com.guenbon.siso.entity.like.RatingLike;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.reaction.RatingDisLikeErrorCode;
import com.guenbon.siso.repository.dislike.RatingDislikeRepository;
import com.guenbon.siso.repository.like.RatingLikeRepository;
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
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class IntegratedRatingDisLikeServiceTest {

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

        // Member


        // when
        RatingReactionDTO actual = ratingDisLikeService.create(encryptedRatingId, memberId);

        // then
        assertAll(
                () -> assertThat(actual.getDislike().getStatus()).isEqualTo(ReactionStatus.CREATED),
                () -> assertThat(actual.getLike().getStatus()).isEqualTo(ReactionStatus.DELETED)
        );
    }

}