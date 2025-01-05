package com.guenbon.siso.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.guenbon.siso.config.QuerydslConfig;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.entity.dislike.RatingDisLike;
import com.guenbon.siso.entity.like.RatingLike;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import com.guenbon.siso.support.fixture.congressman.CongressmanFixture;
import com.guenbon.siso.support.fixture.member.MemberFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QuerydslConfig.class) // QueryDslConfig 추가
public class RatingRepositoryTest {

    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CongressmanRepository congressmanRepository;
    @Autowired
    RatingLikeRepository ratingLikeRepository;
    @Autowired
    RatingDisLikeRepository ratingDisLikeRepository;

    @Test
    void ratingRepository_null_아님() {
        assertThat(ratingRepository).isNotNull();
    }

    @Test
    @DisplayName("회원 id와 국회의원 id로 존재하는 rating을 조회할 수 있다")
    void findByMemberIdAndCongressmanId_exist_rating() {
        // given
        final Member 장몽이 = MemberFixture.builder()
                .setNickname("장몽이")
                .build();
        final Congressman 이준석 = CongressmanFixture.builder()
                .setName("이준석")
                .build();
        final Rating given = Rating.builder()
                .member(장몽이)
                .congressman(이준석)
                .build();

        final Member savedMember = memberRepository.save(장몽이);
        final Congressman savedCongressman = congressmanRepository.save(이준석);
        ratingRepository.save(given);

        // when
        final Rating actual = ratingRepository.findByMemberIdAndCongressmanId(savedMember.getId(),
                savedCongressman.getId()).get();

        // then
        assertAll(
                () -> assertThat(actual.getMember().getId()).isEqualTo(savedMember.getId()),
                () -> assertThat(actual.getCongressman().getId()).isEqualTo(savedCongressman.getId())
        );
    }

    @Test
    @DisplayName("existsByMemberAndCongressman가 존재하는 rating에 대해 true를 반환한다")
    void existsByMemberAndCongressman_exists_true() {
        // given
        final Member 장몽이 = MemberFixture.builder()
                .setNickname("장몽이")
                .build();
        final Congressman 이준석 = CongressmanFixture.builder()
                .setName("이준석")
                .build();
        final Rating given = Rating.builder()
                .member(장몽이)
                .congressman(이준석)
                .build();

        memberRepository.save(장몽이);
        congressmanRepository.save(이준석);
        ratingRepository.save(given);

        // when
        boolean result = ratingRepository.existsByMemberAndCongressman(장몽이, 이준석);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("getRecentRatingByCongressmanIdSort가 유효한 파라미터에 대해 정렬된 Rating List를 반환한다")
    void getRecentRatingByCongressmanIdSort_validParamter_RatingList() {
        // given
        final Member 장몽원 = memberRepository.save(MemberFixture.builder().build());
        final Member 장몽투 = memberRepository.save(MemberFixture.builder().build());
        final Member 장몽삼 = memberRepository.save(MemberFixture.builder().build());
        final Member 장몽포 = memberRepository.save(MemberFixture.builder().build());
        final Congressman 이준석 = congressmanRepository.save(CongressmanFixture.builder().build());
        final Rating rate1 = ratingRepository.save(Rating.builder().member(장몽원).congressman(이준석).build());
        final Rating rate2 = ratingRepository.save(Rating.builder().member(장몽투).congressman(이준석).build());
        final Rating rate3 = ratingRepository.save(Rating.builder().member(장몽삼).congressman(이준석).build());
        final Rating rate4 = ratingRepository.save(Rating.builder().member(장몽포).congressman(이준석).build());
        final Rating rate5 = ratingRepository.save(Rating.builder().member(장몽포).congressman(이준석).build());

        ratingLikeRepository.save(RatingLike.builder().rating(rate1).member(장몽투).build());
        ratingDisLikeRepository.save(RatingDisLike.builder().rating(rate1).member(장몽삼).build());
        ratingLikeRepository.save(RatingLike.builder().rating(rate2).member(장몽원).build());
        ratingDisLikeRepository.save(RatingDisLike.builder().rating(rate5).member(장몽원).build());
        ratingDisLikeRepository.save(RatingDisLike.builder().rating(rate5).member(장몽투).build());

        PageRequest pageRequest1 = PageRequest.of(0, 3, Sort.by("topicality").descending());
        PageRequest pageRequest2 = PageRequest.of(0, 3, Sort.by("like").descending());
        PageRequest pageRequest3 = PageRequest.of(0, 3, Sort.by("dislike").descending());

        // when
        List<Rating> actual1 = ratingRepository.getRecentRatingByCongressmanIdSort(이준석.getId(), pageRequest1).get();
        List<Rating> actual2 = ratingRepository.getRecentRatingByCongressmanIdSort(이준석.getId(), pageRequest2).get();
        List<Rating> actual3 = ratingRepository.getRecentRatingByCongressmanIdSort(이준석.getId(), pageRequest3).get();

        // then
        assertAll(
                () -> assertThat(actual1).usingRecursiveComparison().isEqualTo(List.of(rate5, rate1, rate2, rate4)),
                () -> assertThat(actual2).usingRecursiveComparison().isEqualTo(List.of(rate2, rate1, rate4, rate3)),
                () -> assertThat(actual3).usingRecursiveComparison().isEqualTo(List.of(rate5, rate1, rate4, rate3))
        );
    }
}