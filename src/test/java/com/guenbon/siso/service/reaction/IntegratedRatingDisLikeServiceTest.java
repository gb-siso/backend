package com.guenbon.siso.service.reaction;

import com.guenbon.siso.dto.reaction.response.RatingReactionDTO;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.entity.congressman.Congressman;
import com.guenbon.siso.entity.like.RatingLike;
import com.guenbon.siso.repository.MemberRepository;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import com.guenbon.siso.repository.dislike.RatingDislikeRepository;
import com.guenbon.siso.repository.like.RatingLikeRepository;
import com.guenbon.siso.repository.rating.RatingRepository;
import com.guenbon.siso.support.fixture.congressman.CongressmanFixture;
import com.guenbon.siso.support.fixture.member.MemberFixture;
import com.guenbon.siso.support.fixture.rating.RatingFixture;
import com.guenbon.siso.util.AESUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class IntegratedRatingDisLikeServiceTest {

    @Autowired
    RatingLikeRepository ratingLikeRepository;
    @Autowired
    RatingDislikeRepository ratingDislikeRepository;
    @Autowired
    RatingDisLikeService ratingDisLikeService;
    @Autowired
    AESUtil aesUtil;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private CongressmanRepository congressmanRepository;
    @Autowired
    private RatingLikeService ratingLikeService;

    @Test
    @DisplayName("좋아요 누른 평가에 대해 create 호출할 경우 좋아요를 삭제하고 싫어요를 생성한다.")
    void pushedLike_create_deleteLikeAndCreateDisLike() {
        // given
        Congressman congressman = congressmanRepository.save(CongressmanFixture.builder().build());
        Member member = memberRepository.save(MemberFixture.builder().build());
        Rating rating = ratingRepository.save(RatingFixture.builder().setMember(member).setCongressman(congressman).build());
        RatingLike like = ratingLikeRepository.save(RatingLike.builder().rating(rating).member(member).build());
        System.out.println(like);

        // when
        Long ratingId = rating.getId();
        Long memberId = member.getId();
        ratingDisLikeService.create(aesUtil.encrypt(ratingId), memberId);

        // then
        boolean likeExists = ratingLikeRepository.findByRatingIdAndMemberId(ratingId, memberId).isPresent();
        boolean dislikeExists = ratingDislikeRepository.findByRatingIdAndMemberId(ratingId, memberId).isPresent();
        assertThat(likeExists).isFalse();
        assertThat(dislikeExists).isTrue();
    }
}