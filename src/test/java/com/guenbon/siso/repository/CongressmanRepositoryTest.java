package com.guenbon.siso.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.guenbon.siso.config.QuerydslConfig;
import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import com.guenbon.siso.support.fixture.CongressmanFixture;
import com.guenbon.siso.support.fixture.MemberFixture;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
class CongressmanRepositoryTest {

    @Autowired
    CongressmanRepository congressmanRepository;

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    MemberRepository memberRepository;

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

        final Congressman 서재민 = saveCongressman(CongressmanFixture.builder().setName("서재민").build());
        final Congressman 김선균 = saveCongressman(CongressmanFixture.builder().setName("김선균").build());
        final Congressman 정승수 = saveCongressman(CongressmanFixture.builder().setName("정승수").build());
        final Congressman 송효근 = saveCongressman(CongressmanFixture.builder().setName("송효근").build());
        final Congressman 장지담 = saveCongressman(CongressmanFixture.builder().setName("장지담").build());

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

        PageRequest pageRequest1 = PageRequest.of(0, 2, Sort.by("rating").descending());
        PageRequest pageRequest2 = PageRequest.of(1, 2, Sort.by("rating").descending());
        PageRequest pageRequest3 = PageRequest.of(2, 2, Sort.by("rating").descending());

        // when
        List<CongressmanGetListDTO> list1 = congressmanRepository.getList(pageRequest1, Long.MAX_VALUE, null, null);
        List<CongressmanGetListDTO> list2 = congressmanRepository.getList(pageRequest2, 송효근.getId(), 3.5, null);
        List<CongressmanGetListDTO> list3 = congressmanRepository.getList(pageRequest3, 정승수.getId(), 3.0, null);

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

        final Congressman 서재민 = saveCongressman(CongressmanFixture.builder().setName("서재민").build());
        final Congressman 김선균 = saveCongressman(CongressmanFixture.builder().setName("김선균").build());
        final Congressman 정승수 = saveCongressman(CongressmanFixture.builder().setName("정승수").build());
        final Congressman 송효근 = saveCongressman(CongressmanFixture.builder().setName("송효근").build());
        final Congressman 장지담 = saveCongressman(CongressmanFixture.builder().setName("장지담").build());

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

        PageRequest pageRequest1 = PageRequest.of(0, 2, Sort.by("rating").ascending());
        PageRequest pageRequest2 = PageRequest.of(1, 2, Sort.by("rating").ascending());
        PageRequest pageRequest3 = PageRequest.of(2, 2, Sort.by("rating").ascending());

        // when
        List<CongressmanGetListDTO> list1 = congressmanRepository.getList(pageRequest1, Long.MAX_VALUE, null, null);
        List<CongressmanGetListDTO> list2 = congressmanRepository.getList(pageRequest2, 장지담.getId(), 3.5, null);
        List<CongressmanGetListDTO> list3 = congressmanRepository.getList(pageRequest3, 서재민.getId(), 5.0, null);

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

        final Congressman 김땅콩 = saveCongressman(CongressmanFixture.builder().setName("김땅콩").setParty("더불어민주당").build());
        final Congressman 김유신 = saveCongressman(CongressmanFixture.builder().setName("김유신").setParty("한나라당").build());
        final Congressman 레오나르도김 = saveCongressman(CongressmanFixture.builder().setName("레오나르도 김").setParty("국민의힘").build());
        final Congressman 장몽이 = saveCongressman(CongressmanFixture.builder().setName("장몽이").setParty("국민의힘").build());
        final Congressman 장지담 = saveCongressman(CongressmanFixture.builder().setName("장지담").setParty("국민의힘").build());

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

        PageRequest pageRequest1 = PageRequest.of(0, 2, Sort.by("rating").ascending());
        PageRequest pageRequest2 = PageRequest.of(0, 2, Sort.by("rating").ascending());
        PageRequest pageRequest3 = PageRequest.of(0, 2, Sort.by("rating").ascending());

        // when
        List<CongressmanGetListDTO> list1 = congressmanRepository.getList(pageRequest1, Long.MAX_VALUE, null, null, "김");
        List<CongressmanGetListDTO> list2 = congressmanRepository.getList(pageRequest2, Long.MAX_VALUE, null, null, "장");
        List<CongressmanGetListDTO> list3 = congressmanRepository.getList(pageRequest3, Long.MAX_VALUE, null, null, "장지담");

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

        final Congressman 서재민 = saveCongressman(CongressmanFixture.builder().setName("서재민").setParty("더불어민주당").build());
        final Congressman 김선균 = saveCongressman(CongressmanFixture.builder().setName("김선균").setParty("한나라당").build());
        final Congressman 정승수 = saveCongressman(CongressmanFixture.builder().setName("정승수").setParty("국민의힘").build());
        final Congressman 송효근 = saveCongressman(CongressmanFixture.builder().setName("송효근").setParty("국민의힘").build());
        final Congressman 장지담 = saveCongressman(CongressmanFixture.builder().setName("장지담").setParty("국민의힘").build());

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

        PageRequest pageRequest1 = PageRequest.of(0, 2, Sort.by("rating").ascending());
        PageRequest pageRequest2 = PageRequest.of(0, 2, Sort.by("rating").ascending());
        PageRequest pageRequest3 = PageRequest.of(0, 2, Sort.by("rating").ascending());

        // when
        List<CongressmanGetListDTO> list1 = congressmanRepository.getList(pageRequest1, Long.MAX_VALUE, null, "더불어민주당");
        List<CongressmanGetListDTO> list2 = congressmanRepository.getList(pageRequest2, Long.MAX_VALUE, null, "한나라당");
        List<CongressmanGetListDTO> list3 = congressmanRepository.getList(pageRequest3, Long.MAX_VALUE, null, "국민의힘");

        // then
        assertAll(
                () -> assertThat(list1).usingRecursiveComparison().isEqualTo(List.of(서재민_DTO)),
                () -> assertThat(list2).usingRecursiveComparison().isEqualTo(List.of(김선균_DTO)),
                () -> assertThat(list3).usingRecursiveComparison().isEqualTo(List.of(정승수_DTO, 송효근_DTO, 장지담_DTO))
        );
    }


    private CongressmanGetListDTO toDTO(Congressman congressman, double rate) {
        return CongressmanGetListDTO.builder()
                .id(congressman.getId())
                .name(congressman.getName())
                .rate(rate)
                .build();
    }

    private void saveRating(Member member, Congressman congressman, double rate) {
        ratingRepository.save(Rating.builder().member(member).congressman(congressman).rate(rate).build());
    }

    private Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    private Congressman saveCongressman(Congressman congressman) {
        return congressmanRepository.save(congressman);
    }
}