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
import com.guenbon.siso.repository.dislike.RatingDisLikeRepository;
import com.guenbon.siso.repository.like.RatingLikeRepository;
import com.guenbon.siso.repository.rating.RatingRepository;
import com.guenbon.siso.support.fixture.congressman.CongressmanFixture;
import com.guenbon.siso.support.fixture.member.MemberFixture;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QuerydslConfig.class) // QueryDslConfig 추가
@Slf4j
@EnableJpaAuditing
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
    @DisplayName("getSortedRatingsByCongressmanId가 유효한 파라미터로 정렬된 Rating 리스트를 반환한다")
    void getSortedRatingsByCongressmanId_validParameters_RatingList() {
        // given
        final Member 장몽원 = saveMember();
        final Member 장몽투 = saveMember();
        final Member 장몽삼 = saveMember();
        final Member 장몽포 = saveMember();
        final Member 장몽오 = saveMember();

        final Congressman 이준석 = congressmanRepository.save(CongressmanFixture.builder().build());

        final Rating rate1 = saveRating(장몽원, 이준석); // 좋아요 1 싫어요 1
        final Rating rate2 = saveRating(장몽투, 이준석); // 좋아요 1 싫어요 0
        final Rating rate3 = saveRating(장몽삼, 이준석); // 좋아요 0 싫어요 1
        final Rating rate4 = saveRating(장몽포, 이준석); // 좋아요 2 싫어요 0
        final Rating rate5 = saveRating(장몽오, 이준석);  // 좋아요 0 싫어요 2

        likeRateAndSave(rate1, 장몽투);
        disLikeRateAndSave(rate1, 장몽삼);
        likeRateAndSave(rate2, 장몽원);
        disLikeRateAndSave(rate3, 장몽원);
        likeRateAndSave(rate4, 장몽원);
        likeRateAndSave(rate4, 장몽투);
        disLikeRateAndSave(rate5, 장몽원);
        disLikeRateAndSave(rate5, 장몽투);

        // when
        final List<Rating> likeSortResultOPage0 = ratingRepository.getSortedRatingsByCongressmanId(이준석.getId(),
                createPageRequest("like"), null);
        final List<Rating> likeSortResultOPage1 = ratingRepository.getSortedRatingsByCongressmanId(이준석.getId(),
                createPageRequest("like"), DecryptedRatingCursor.of(rate2.getId(), rate2.getLikes()));
        final List<Rating> disLikeSortResultOPage0 = ratingRepository.getSortedRatingsByCongressmanId(이준석.getId(),
                createPageRequest("dislike"), null);
        final List<Rating> disLikeSortResultOPage1 = ratingRepository.getSortedRatingsByCongressmanId(이준석.getId(),
                createPageRequest("dislike"), DecryptedRatingCursor.of(rate3.getId(), rate3.getDislikes()));
        final List<Rating> topicalitySortResultPage0 = ratingRepository.getSortedRatingsByCongressmanId(이준석.getId(),
                createPageRequest("topicality"), null);
        final List<Rating> topicalitySortResultPage1 = ratingRepository.getSortedRatingsByCongressmanId(이준석.getId(),
                createPageRequest("topicality"), DecryptedRatingCursor.of(rate4.getId(),
                        rate4.getTopicality()));
        // then
        assertAll(() -> assertRatingOrder(likeSortResultOPage0, rate4, rate1, rate2),
                () -> assertRatingOrder(likeSortResultOPage1, rate2, rate3, rate5),
                () -> assertRatingOrder(disLikeSortResultOPage0, rate5, rate1, rate3),
                () -> assertRatingOrder(disLikeSortResultOPage1, rate3, rate2, rate4),
                () -> assertRatingOrder(topicalitySortResultPage0, rate1, rate5, rate4),
                () -> assertRatingOrder(topicalitySortResultPage1, rate4, rate2, rate3));
    }

    private static ListAssert<Long> assertRatingOrder(List<Rating> ratingList, Rating rate1, Rating rate2,
                                                      Rating rate3) {
        return assertThat(ratingList.stream().map(Rating::getId)).containsExactly(rate1.getId(), rate2.getId(),
                rate3.getId());
    }

    private static PageRequest createPageRequest(String sort) {
        return PageRequest.of(0, 2, Sort.by(sort).descending());
    }

    private Rating saveRating(Member 장몽원, Congressman 이준석) {
        return ratingRepository.save(Rating.builder().member(장몽원).congressman(이준석).build());
    }

    private Member saveMember() {
        return memberRepository.save(MemberFixture.builder().build());
    }

    private void likeRateAndSave(Rating rating, final Member member) {
        final RatingLike like = RatingLike.builder().member(member).build();
        rating.addLike(like);
        ratingLikeRepository.save(like);
    }

    private void disLikeRateAndSave(Rating rating, final Member member) {
        final RatingDisLike disLike = RatingDisLike.builder().member(member).build();
        rating.addDisLike(disLike);
        ratingDisLikeRepository.save(disLike);
    }
}