package com.guenbon.siso.repository;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.guenbon.siso.config.QuerydslConfig;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.entity.like.RatingLike;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import com.guenbon.siso.repository.like.RatingLikeRepository;
import com.guenbon.siso.repository.rating.RatingRepository;
import com.guenbon.siso.support.fixture.congressman.CongressmanFixture;
import com.guenbon.siso.support.fixture.member.MemberFixture;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QuerydslConfig.class) // QueryDslConfig 추가
@Slf4j
@EnableJpaAuditing
public class RatingLikeRepositoryTest {
    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RatingLikeRepository ratingLikeRepository;
    @Autowired
    CongressmanRepository congressmanRepository;


    @Test
    @DisplayName("RatingLike save 시 중복되는 member - rating에 대해 예외를 던진다")
    void save_duplicated_Exception() {
        // given
        final Member 장몽이 = memberRepository.save(MemberFixture.builder().setNickname("장몽이").build());
        final Congressman 이준석 = congressmanRepository.save(CongressmanFixture.builder().setName("이준석").build());
        final Rating rating = ratingRepository.save(Rating.builder().member(장몽이).congressman(이준석).build());
        likeRateAndSave(rating, RatingLike.builder().member(장몽이).build());

        // when, then
        assertThrows(DataIntegrityViolationException.class,
                () -> likeRateAndSave(rating, RatingLike.builder().member(장몽이).build()));
    }

    private void likeRateAndSave(Rating rating, RatingLike like) {
        rating.addLike(like);
        ratingLikeRepository.save(like);
    }
}
