package com.guenbon.siso.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.guenbon.siso.entity.Rating;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class RatingRepositoryTest {

    @Autowired
    RatingRepository ratingRepository;

    @Test
    void ratingRepository_null_아님() {
        assertThat(ratingRepository).isNotNull();
    }

    @Test
    @DisplayName("회원 id와 국회의원 id로 존재하는 rating을 조회할 수 있다")
    void findByMemberIdAndCongressManId_exist_rating() {
        // given
        final Member 지담 = MemberFixture.builder().name("장지담").build();
        final CongressMan 이준석 = CongressManFixture.builder().name("이준석").build();
        final Rating given = Rating.builder().member().congressMan().build();
        ratingRepository.save(rating);

        // when
        Rating actual =  ratingRepository.findByMemberIdAndCongressManId(지담.getId(),이준석.getId()).get();

        // then
        assertAll(
                ()->assertThat(rating.getMember().getId()).isEqaulTo(지담.getId()),
                ()->assertThat(rating.getCongressMan.getId()).isEqualTo(이준석.getId());
        );
    }

}