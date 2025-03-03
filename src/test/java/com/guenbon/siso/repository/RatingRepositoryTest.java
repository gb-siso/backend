package com.guenbon.siso.repository;

import com.guenbon.siso.config.QuerydslConfig;
import com.guenbon.siso.dto.cursor.count.DecryptedCountCursor;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.entity.dislike.RatingDislike;
import com.guenbon.siso.entity.like.RatingLike;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import com.guenbon.siso.repository.dislike.RatingDislikeRepository;
import com.guenbon.siso.repository.like.RatingLikeRepository;
import com.guenbon.siso.repository.rating.RatingRepository;
import com.guenbon.siso.support.fixture.congressman.CongressmanFixture;
import com.guenbon.siso.support.fixture.member.MemberFixture;
import com.guenbon.siso.support.fixture.rating.RatingFixture;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QuerydslConfig.class)
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
    RatingDislikeRepository ratingDislikeRepository;

    @MockitoSpyBean
    AuditingHandler auditingHandler;

    @MockitoBean
    DateTimeProvider dateTimeProvider;

    @BeforeEach
    void setUp() {
        auditingHandler.setDateTimeProvider(dateTimeProvider);
    }

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
        final Rating given = RatingFixture.builder().setMember(장몽이).setCongressman(이준석).build();

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
        final Rating given = RatingFixture.builder().setMember(장몽이).setCongressman(이준석).build();

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
        final Member[] members = createMembers(5);
        final Congressman congressman = saveCongressman();

        final Rating[] ratings = {
                saveRating(members[0], congressman, 0), // 좋아요 1 싫어요 1
                saveRating(members[1], congressman, 1), // 좋아요 1 싫어요 0
                saveRating(members[2], congressman, 2), // 좋아요 0 싫어요 1
                saveRating(members[3], congressman, 3), // 좋아요 2 싫어요 0
                saveRating(members[4], congressman, 4)  // 좋아요 0 싫어요 2
        };

        configureLikesAndDislikes(ratings, members);

        // when: 각 정렬 조건(like, dislike, topicality)에 따라 페이지 0, 1의 결과를 가져온다.
        final List<Rating> likeSortResultPage0 = fetchSortedRatings(ratings[0], "like", null);
        final Rating likeSortLastRatingPage0 = likeSortResultPage0.get(likeSortResultPage0.size() - 1);
        final List<Rating> likeSortResultPage1 = fetchSortedRatings(likeSortLastRatingPage0, "like",
                DecryptedCountCursor.of(likeSortLastRatingPage0.getId(), likeSortLastRatingPage0.getLikeCount()));

        final List<Rating> dislikeSortResultPage0 = fetchSortedRatings(ratings[0], "dislike", null);
        final Rating dislikeSortLastRatingPage0 = dislikeSortResultPage0.get(dislikeSortResultPage0.size() - 1);
        final List<Rating> dislikeSortResultPage1 = fetchSortedRatings(dislikeSortLastRatingPage0, "dislike",
                DecryptedCountCursor.of(dislikeSortLastRatingPage0.getId(),
                        dislikeSortLastRatingPage0.getDislikeCount()));

        final List<Rating> topicalitySortResultPage0 = fetchSortedRatings(ratings[0], "topicality", null);
        final Rating topicalitySortLastRatingPage0 = topicalitySortResultPage0.get(
                topicalitySortResultPage0.size() - 1);
        final List<Rating> topicalitySortResultPage1 = fetchSortedRatings(topicalitySortLastRatingPage0, "topicality",
                DecryptedCountCursor.of(topicalitySortLastRatingPage0.getId(),
                        topicalitySortLastRatingPage0.getTopicality()));

        final List<Rating> regDateSortResultPage0 = fetchSortedRatings(ratings[0], "regDate", null);
        final Rating regDateSortLastRatingPage0 = regDateSortResultPage0.get(
                regDateSortResultPage0.size() - 1);
        final List<Rating> regDateSortLastRatingPage1 = fetchSortedRatings(regDateSortLastRatingPage0, "regDate",
                DecryptedCountCursor.of(regDateSortLastRatingPage0.getId(), 0));

        log.info("list 확인");
        for (Rating rating : regDateSortResultPage0) {
            log.info(rating.toString());
        }
        for (Rating rating : regDateSortLastRatingPage1) {
            log.info(rating.toString());
        }

        // then
        assertAll(
                () -> assertRatingOrder(likeSortResultPage0, ratings[3], ratings[1], ratings[0]),
                () -> assertRatingOrder(likeSortResultPage1, ratings[0], ratings[4], ratings[2]),
                () -> assertRatingOrder(dislikeSortResultPage0, ratings[4], ratings[2], ratings[0]),
                () -> assertRatingOrder(dislikeSortResultPage1, ratings[0], ratings[3], ratings[1]),
                () -> assertRatingOrder(topicalitySortResultPage0, ratings[4], ratings[3], ratings[0]),
                () -> assertRatingOrder(topicalitySortResultPage1, ratings[0], ratings[2], ratings[1]),
                () -> assertRatingOrder(regDateSortResultPage0, ratings[4], ratings[3], ratings[2]),
                () -> assertRatingOrder(regDateSortLastRatingPage1, ratings[2], ratings[1], ratings[0])
        );
    }

    private Member[] createMembers(final int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> memberRepository.save(MemberFixture.builder().build()))
                .toArray(Member[]::new);
    }

    private Congressman saveCongressman() {
        return congressmanRepository.save(CongressmanFixture.builder().build());
    }

    private Rating saveRating(final Member member, final Congressman congressman, int days) {
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now().plusDays(days)));
        return ratingRepository.save(RatingFixture.builder().setMember(member).setCongressman(congressman).build());
    }

    private void configureLikesAndDislikes(final Rating[] ratings, final Member[] members) {
        likeRateAndSave(ratings[0], members[1]);
        disLikeRateAndSave(ratings[0], members[2]);
        likeRateAndSave(ratings[1], members[0]);
        disLikeRateAndSave(ratings[2], members[0]);
        likeRateAndSave(ratings[3], members[0]);
        likeRateAndSave(ratings[3], members[1]);
        disLikeRateAndSave(ratings[4], members[0]);
        disLikeRateAndSave(ratings[4], members[1]);
    }

    private static void assertRatingOrder(final List<Rating> ratingList, final Rating... expectedOrder) {
        assertThat(ratingList.stream().map(Rating::getId))
                .containsExactly(Arrays.stream(expectedOrder).map(Rating::getId).toArray(Long[]::new));
    }

    private void likeRateAndSave(final Rating rating, final Member member) {
        final RatingLike like = RatingLike.builder().member(member).build();
        rating.addLike(like);
        ratingLikeRepository.save(like);
    }

    private void disLikeRateAndSave(final Rating rating, final Member member) {
        final RatingDislike dislike = RatingDislike.builder().member(member).build();
        rating.addDislike(dislike);
        ratingDislikeRepository.save(dislike);
    }

    private List<Rating> fetchSortedRatings(
            final Rating baseRating,
            final String sortProperty,
            final DecryptedCountCursor countCursor
    ) {
        // congressman 고정 및 기본 페이지 정보 설정
        final Long congressmanId = baseRating.getCongressman().getId();
        final Pageable pageable = PageRequest.of(0, 2, Sort.by(sortProperty).descending());

        // 정렬 조건과 페이지 정보를 기반으로 congressman ID에 해당하는 Rating 리스트를 가져온다
        return ratingRepository.getSortedRatingsByCongressmanId(
                congressmanId,
                pageable,
                countCursor
        );
    }
}