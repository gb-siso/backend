package com.guenbon.siso.repository;

import com.guenbon.siso.config.QuerydslConfig;
import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QuerydslConfig.class) // QueryDslConfig 추가
@Slf4j
@EnableJpaAuditing
class CongressmanRepositoryTest {

    @Autowired
    CongressmanRepository congressmanRepository;

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    MemberRepository memberRepository;

    @MockitoSpyBean
    AuditingHandler auditingHandler;

    @MockitoBean
    DateTimeProvider dateTimeProvider;

    @BeforeEach
    void setUp() {
        auditingHandler.setDateTimeProvider(dateTimeProvider);
    }

    @Test
    @DisplayName("findById 가 존재하는 국회의원에 대해 정상 반환한다")
    void findById_exists_congressman() {
        // given
        final Congressman 이준석 = CongressmanFixture.builder().setName("이준석").build();
        final Congressman expected = congressmanRepository.save(이준석);

        // when
        Congressman actual = congressmanRepository.findById(expected.getId()).get();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("getList가 pageable 파라미터에 rating 높은 순으로 대해 알맞은 리스트를 응답한다")
    void getList_pageableRatingDesc_list() {
        // given
        final Member jangmong99 = saveMember(MemberFixture.builder().setNickname("jangmong99").build());
        final Member chungmung99 = saveMember(MemberFixture.builder().setNickname("chungmung99").build());

        final Congressman 서재민 = saveCongressman(CongressmanFixture.builder().setCode("a").setName("서재민").build());
        final Congressman 김선균 = saveCongressman(CongressmanFixture.builder().setCode("b").setName("김선균").build());
        final Congressman 정승수 = saveCongressman(CongressmanFixture.builder().setCode("c").setName("정승수").build());
        final Congressman 송효근 = saveCongressman(CongressmanFixture.builder().setCode("d").setName("송효근").build());
        final Congressman 장지담 = saveCongressman(CongressmanFixture.builder().setCode("e").setName("장지담").build());

        saveRating(jangmong99, 서재민, 5.0);
        saveRating(jangmong99, 김선균, 4.0);
        saveRating(jangmong99, 정승수, 3.0);
        saveRating(jangmong99, 송효근, 2.0);
        saveRating(chungmung99, 송효근, 5.0);
        saveRating(chungmung99, 장지담, 3.5);

        CongressmanGetListDTO 서재민_DTO = toDTO(서재민, 5.0);
        CongressmanGetListDTO 김선균_DTO = toDTO(김선균, 4.0);
        CongressmanGetListDTO 정승수_DTO = toDTO(정승수, 3.0);
        CongressmanGetListDTO 송효근_DTO = toDTO(송효근, 3.5);
        CongressmanGetListDTO 장지담_DTO = toDTO(장지담, 3.5);

        PageRequest pageRequest1 = PageRequest.of(0, 2, Sort.by("rate").descending());
        PageRequest pageRequest2 = PageRequest.of(1, 2, Sort.by("rate").descending());
        PageRequest pageRequest3 = PageRequest.of(2, 2, Sort.by("rate").descending());

        // when
        List<CongressmanGetListDTO> list1 = congressmanRepository.getList(pageRequest1, Long.MAX_VALUE, null, null,
                null);
        List<CongressmanGetListDTO> list2 = congressmanRepository.getList(pageRequest2, 송효근.getId(), 3.5, null, null);
        List<CongressmanGetListDTO> list3 = congressmanRepository.getList(pageRequest3, 정승수.getId(), 3.0, null, null);

        // then
        assertAll(
                () -> assertThat(list1).usingRecursiveComparison().isEqualTo(List.of(서재민_DTO, 김선균_DTO, 송효근_DTO)),
                () -> assertThat(list2).usingRecursiveComparison().isEqualTo(List.of(송효근_DTO, 장지담_DTO, 정승수_DTO)),
                () -> assertThat(list3).usingRecursiveComparison().isEqualTo(List.of(정승수_DTO))
        );
    }

    @Test
    @DisplayName("getList가 pageable 파라미터에 rating 낮은 순으로 대해 알맞은 리스트를 응답한다")
    void getList_paryFiltering_list() {
        // given
        final Member jangmong99 = saveMember(MemberFixture.builder().setNickname("jangmong99").build());
        final Member chungmung99 = saveMember(MemberFixture.builder().setNickname("chungmung99").build());

        final Congressman 서재민 = saveCongressman(CongressmanFixture.builder().setCode("a").setName("서재민").build());
        final Congressman 김선균 = saveCongressman(CongressmanFixture.builder().setCode("b").setName("김선균").build());
        final Congressman 정승수 = saveCongressman(CongressmanFixture.builder().setCode("c").setName("정승수").build());
        final Congressman 송효근 = saveCongressman(CongressmanFixture.builder().setCode("d").setName("송효근").build());
        final Congressman 장지담 = saveCongressman(CongressmanFixture.builder().setCode("e").setName("장지담").build());

        saveRating(jangmong99, 서재민, 5.0);
        saveRating(jangmong99, 김선균, 4.0);
        saveRating(jangmong99, 정승수, 3.0);
        saveRating(jangmong99, 송효근, 2.0);
        saveRating(chungmung99, 송효근, 5.0);
        saveRating(chungmung99, 장지담, 3.5);

        CongressmanGetListDTO 서재민_DTO = toDTO(서재민, 5.0);
        CongressmanGetListDTO 김선균_DTO = toDTO(김선균, 4.0);
        CongressmanGetListDTO 정승수_DTO = toDTO(정승수, 3.0);
        CongressmanGetListDTO 송효근_DTO = toDTO(송효근, 3.5);
        CongressmanGetListDTO 장지담_DTO = toDTO(장지담, 3.5);

        PageRequest pageRequest1 = PageRequest.of(0, 2, Sort.by("rate").ascending());
        PageRequest pageRequest2 = PageRequest.of(1, 2, Sort.by("rate").ascending());
        PageRequest pageRequest3 = PageRequest.of(2, 2, Sort.by("rate").ascending());

        // when
        List<CongressmanGetListDTO> list1 = congressmanRepository.getList(pageRequest1, Long.MAX_VALUE, null, null,
                null);
        List<CongressmanGetListDTO> list2 = congressmanRepository.getList(pageRequest2, 장지담.getId(), 3.5, null, null);
        List<CongressmanGetListDTO> list3 = congressmanRepository.getList(pageRequest3, 서재민.getId(), 5.0, null, null);

        // then
        assertAll(
                () -> assertThat(list1).usingRecursiveComparison().isEqualTo(List.of(정승수_DTO, 송효근_DTO, 장지담_DTO)),
                () -> assertThat(list2).usingRecursiveComparison().isEqualTo(List.of(장지담_DTO, 김선균_DTO, 서재민_DTO)),
                () -> assertThat(list3).usingRecursiveComparison().isEqualTo(List.of(서재민_DTO))
        );
    }

    @Test
    @DisplayName("getList가 search 파라미터에 따라 필터링해 알맞은 리스트를 응답한다")
    void getList_searchFiltering_list() {
        final Member jangmong99 = saveMember(MemberFixture.builder().setNickname("jangmong99").build());
        final Member chungmung99 = saveMember(MemberFixture.builder().setNickname("chungmung99").build());

        final Congressman 김땅콩 = saveCongressman(CongressmanFixture.builder().setCode("abc123").setName("김땅콩").setParty("더불어민주당").build());
        final Congressman 김유신 = saveCongressman(CongressmanFixture.builder().setCode("def456").setName("김유신").setParty("한나라당").build());
        final Congressman 레오나르도김 = saveCongressman(
                CongressmanFixture.builder().setCode("jdk123").setName("레오나르도 김").setParty("국민의힘").build());
        final Congressman 장몽이 = saveCongressman(CongressmanFixture.builder().setCode("qwe123").setName("장몽이").setParty("국민의힘").build());
        final Congressman 장지담 = saveCongressman(CongressmanFixture.builder().setCode("rty456").setName("장지담").setParty("국민의힘").build());

        saveRating(jangmong99, 김땅콩, 5.0);
        saveRating(jangmong99, 김유신, 4.0);
        saveRating(jangmong99, 레오나르도김, 3.0);
        saveRating(jangmong99, 장몽이, 2.0);
        saveRating(chungmung99, 장몽이, 5.0);
        saveRating(chungmung99, 장지담, 3.5);

        CongressmanGetListDTO 김땅콩_DTO = toDTO(김땅콩, 5.0);
        CongressmanGetListDTO 김유신_DTO = toDTO(김유신, 4.0);
        CongressmanGetListDTO 레오나르도김_DTO = toDTO(레오나르도김, 3.0);
        CongressmanGetListDTO 장몽이_DTO = toDTO(장몽이, 3.5);
        CongressmanGetListDTO 장지담_DTO = toDTO(장지담, 3.5);

        PageRequest pageRequest1 = PageRequest.of(0, 2, Sort.by("rate").ascending());
        PageRequest pageRequest2 = PageRequest.of(0, 2, Sort.by("rate").ascending());
        PageRequest pageRequest3 = PageRequest.of(0, 2, Sort.by("rate").ascending());

        // when
        List<CongressmanGetListDTO> list1 = congressmanRepository.getList(pageRequest1, Long.MAX_VALUE, null, null,
                "김");
        List<CongressmanGetListDTO> list2 = congressmanRepository.getList(pageRequest2, Long.MAX_VALUE, null, null,
                "장");
        List<CongressmanGetListDTO> list3 = congressmanRepository.getList(pageRequest3, Long.MAX_VALUE, null, null,
                "장지담");

        // then
        assertAll(
                () -> assertThat(list1).usingRecursiveComparison().isEqualTo(List.of(레오나르도김_DTO, 김유신_DTO, 김땅콩_DTO)),
                () -> assertThat(list2).usingRecursiveComparison().isEqualTo(List.of(장몽이_DTO, 장지담_DTO)),
                () -> assertThat(list3).usingRecursiveComparison().isEqualTo(List.of(장지담_DTO))
        );
    }

    @Test
    @DisplayName("getList가 party 파라미터에 따라 필터링해 알맞은 리스트를 응답한다")
    void getList_pageableParty_list() {
        // given
        final Member jangmong99 = saveMember(MemberFixture.builder().setNickname("jangmong99").build());
        final Member chungmung99 = saveMember(MemberFixture.builder().setNickname("chungmung99").build());

        final Congressman 서재민 = saveCongressman(CongressmanFixture.builder().setCode("a").setName("서재민").setParty("더불어민주당").build());
        final Congressman 김선균 = saveCongressman(CongressmanFixture.builder().setCode("b").setName("김선균").setParty("한나라당").build());
        final Congressman 정승수 = saveCongressman(CongressmanFixture.builder().setCode("c").setName("정승수").setParty("국민의힘").build());
        final Congressman 송효근 = saveCongressman(CongressmanFixture.builder().setCode("d").setName("송효근").setParty("국민의힘").build());
        final Congressman 장지담 = saveCongressman(CongressmanFixture.builder().setCode("e").setName("장지담").setParty("국민의힘").build());

        saveRating(jangmong99, 서재민, 5.0);
        saveRating(jangmong99, 김선균, 4.0);
        saveRating(jangmong99, 정승수, 3.0);
        saveRating(jangmong99, 송효근, 2.0);
        saveRating(chungmung99, 송효근, 5.0);
        saveRating(chungmung99, 장지담, 3.5);

        CongressmanGetListDTO 서재민_DTO = toDTO(서재민, 5.0);
        CongressmanGetListDTO 김선균_DTO = toDTO(김선균, 4.0);
        CongressmanGetListDTO 정승수_DTO = toDTO(정승수, 3.0);
        CongressmanGetListDTO 송효근_DTO = toDTO(송효근, 3.5);
        CongressmanGetListDTO 장지담_DTO = toDTO(장지담, 3.5);

        PageRequest pageRequest1 = PageRequest.of(0, 2, Sort.by("rate").ascending());
        PageRequest pageRequest2 = PageRequest.of(0, 2, Sort.by("rate").ascending());
        PageRequest pageRequest3 = PageRequest.of(0, 2, Sort.by("rate").ascending());

        // when
        List<CongressmanGetListDTO> list1 = congressmanRepository.getList(pageRequest1, Long.MAX_VALUE, null, "더불어민주당",
                null);
        List<CongressmanGetListDTO> list2 = congressmanRepository.getList(pageRequest2, Long.MAX_VALUE, null, "한나라당",
                null);
        List<CongressmanGetListDTO> list3 = congressmanRepository.getList(pageRequest3, Long.MAX_VALUE, null, "국민의힘",
                null);

        // then
        assertAll(
                () -> assertThat(list1).usingRecursiveComparison().isEqualTo(List.of(서재민_DTO)),
                () -> assertThat(list2).usingRecursiveComparison().isEqualTo(List.of(김선균_DTO)),
                () -> assertThat(list3).usingRecursiveComparison().isEqualTo(List.of(정승수_DTO, 송효근_DTO, 장지담_DTO))
        );
    }

    @Test
    @DisplayName("getRecentMemberImagesByCongressmanId이 정상 입력에 대해 회원들 이미지를 List<String> 형태로 반환한다")
    void getRecentMemberImagesByCongressmanId_validInput_StringList() {
        // given
        // member 5명  , rating
        final Member 장몽이 = saveMember(MemberFixture.builder().setNickname("jangmong99").setImageUrl("장몽image").build());
        final Member 멍청이 = saveMember(MemberFixture.builder().setNickname("chungmung99").setImageUrl("멍청image").build());
        final Member 다미 = saveMember(MemberFixture.builder().setNickname("dami").setImageUrl("다미image").build());
        final Member 레온이 = saveMember(MemberFixture.builder().setNickname("leon1234").setImageUrl("레온image").build());
        final Member 얼죽이 = saveMember(MemberFixture.builder().setNickname("ulljook").setImageUrl("얼죽Image").build());
        // congressman 2명
        final Congressman 이준석 = saveCongressman(CongressmanFixture.builder().setCode("abc123").setName("이준석").setParty("더불어민주당").build());
        final Congressman 윤석열 = saveCongressman(CongressmanFixture.builder().setCode("def456").setName("윤석열").setParty("한나라당").build());

        // rating (이준석)
        saveRating(다미, 이준석, 3.5, 1);
        saveRating(레온이, 이준석, 3.5, 2);
        saveRating(장몽이, 이준석, 3.5, 3);
        saveRating(멍청이, 이준석, 3.5, 4);
        // rating (윤석열)
        saveRating(얼죽이, 윤석열, 2.5, 1);
        saveRating(다미, 윤석열, 3.5, 2);

        // when
        final List<String> 이준석_평가작성_회원_이미지리스트 = congressmanRepository.getRecentMemberImagesByCongressmanId(이준석.getId());
        final List<String> 윤석열_평가작성_회원_이미지리스트 = congressmanRepository.getRecentMemberImagesByCongressmanId(윤석열.getId());
        // then
        assertAll(
                () -> assertThat(이준석_평가작성_회원_이미지리스트).usingRecursiveComparison()
                        .isEqualTo(List.of(멍청이.getImageUrl(), 장몽이.getImageUrl(), 레온이.getImageUrl(), 다미.getImageUrl())),
                () -> assertThat(윤석열_평가작성_회원_이미지리스트).usingRecursiveComparison()
                        .isEqualTo(List.of(다미.getImageUrl(), 얼죽이.getImageUrl()))
        );
    }

    private CongressmanGetListDTO toDTO(Congressman congressman, double rate) {
        return CongressmanGetListDTO.builder()
                .id(congressman.getId())
                .name(congressman.getName())
                .rate(rate)
                .timesElected(congressman.getTimesElected())
                .party(congressman.getParty())
                .build();
    }

    private Rating saveRating(Member member, Congressman congressman, double rate) {
        return ratingRepository.save(RatingFixture.builder().setMember(member).setCongressman(congressman).setRate(rate).build());
    }

    private Rating saveRating(Member member, Congressman congressman, double rate, int plusDays) {
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now().plusDays(plusDays)));
        return ratingRepository.save(RatingFixture.builder().setMember(member).setCongressman(congressman).setRate(rate).build());
    }

    private Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    private Congressman saveCongressman(Congressman congressman) {
        return congressmanRepository.save(congressman);
    }

    @Test
    @DisplayName("rating 이 작성되지 않은 congressman 도 getList 의 결과에 포함되어야 한다. 이 때 rating 이 없는 congressman 도 알맞게 정렬된다.")
    void noRating_getList_orderedList() {
        /**
         * 정렬 기준
         * desc : 10, 0, null
         * asc : null, 0, 10
         */
        // given
        final Congressman minRatingCongressman = saveCongressman(CongressmanFixture.builder().setName("평점이 0인 국회의원").setCode("cc").build());
        final Congressman noRatingCongressman = saveCongressman(CongressmanFixture.builder().setName("아무도 평가 작성 안한 국회의원").setCode("aa").build());
        final Congressman maxRatingCongressman = saveCongressman(CongressmanFixture.builder().setName("평점이 10인 국회의원").setCode("bb").build());


        final Member jangmong99 = saveMember(MemberFixture.builder().setNickname("jangmong99").build());

        saveRating(jangmong99, maxRatingCongressman, 10.0);
        saveRating(jangmong99, minRatingCongressman, 0.0);

        List<Congressman> ratingDescExpected = List.of(maxRatingCongressman, minRatingCongressman, noRatingCongressman);
        List<Congressman> ratingAscExpected = List.of(noRatingCongressman, minRatingCongressman, maxRatingCongressman);

        // 평점 내림차순
        PageRequest ratingDesc = PageRequest.of(0, 3, Sort.by("rate").descending());
        PageRequest ratingAsc = PageRequest.of(0, 3, Sort.by("rate").ascending());

        // when
        List<CongressmanGetListDTO> ratingDescActual = congressmanRepository.getList(ratingDesc, Long.
                MAX_VALUE, null, null, null);
        List<CongressmanGetListDTO> ratingAscActual = congressmanRepository.getList(ratingAsc, Long.
                MAX_VALUE, null, null, null);

        // then
        // todo code 로 변경 필요 (unique)
        assertThat(ratingDescActual.stream().map(CongressmanGetListDTO::getName).toList()).isEqualTo(ratingDescExpected.stream().map(Congressman::getName).toList());
        assertThat(ratingAscActual.stream().map(CongressmanGetListDTO::getName).toList()).isEqualTo(ratingAscExpected.stream().map(Congressman::getName).toList());
    }
}