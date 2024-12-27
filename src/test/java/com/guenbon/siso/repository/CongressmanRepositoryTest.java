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
    @DisplayName("getList가 pageable 파라미터에 대해 알맞은 리스트를 응답한다")
    void getList_pageable_list() {
        // given
        final Member jangmong99 = memberRepository.save(MemberFixture.builder()
                .setNickname("jangmong99").build());
        final Member chungmung99 = memberRepository.save(MemberFixture.builder()
                .setNickname("chungmung99").build());

        final Congressman 서재민 = congressmanRepository.save(CongressmanFixture.builder()
                .setName("서재민").build());
        final Congressman 김선균 = congressmanRepository.save(CongressmanFixture.builder()
                .setName("김선균").build());
        final Congressman 정승수 = congressmanRepository.save(CongressmanFixture.builder()
                .setName("정승수").build());
        final Congressman 송효근 = congressmanRepository.save(CongressmanFixture.builder()
                .setName("송효근").build());
        final Congressman 장지담 = congressmanRepository.save(CongressmanFixture.builder()
                .setName("장지담").build());

        ratingRepository.save(Rating.builder()
                .member(jangmong99)
                .congressman(서재민)
                .rate(5.0)
                .build());
        ratingRepository.save(Rating.builder()
                .member(jangmong99)
                .congressman(김선균)
                .rate(4.0)
                .build());
        ratingRepository.save(Rating.builder()
                .member(jangmong99)
                .congressman(정승수)
                .rate(3.0)
                .build());
        ratingRepository.save(Rating.builder()
                .member(jangmong99)
                .congressman(송효근)
                .rate(2.0)
                .build());
        ratingRepository.save(Rating.builder()
                .member(chungmung99)
                .congressman(송효근)
                .rate(5.0)
                .build());
        ratingRepository.save(Rating.builder()
                .member(chungmung99)
                .congressman(장지담)
                .rate(3.5)
                .build());

        CongressmanGetListDTO 서재민_DTO = CongressmanGetListDTO.builder()
                .id(서재민.getId())
                .name(서재민.getName())
                .rate(5.0).build();

        CongressmanGetListDTO 김선균_DTO = CongressmanGetListDTO.builder()
                .id(김선균.getId())
                .name(김선균.getName())
                .rate(4.0).build();

        CongressmanGetListDTO 송효근_DTO = CongressmanGetListDTO.builder()
                .id(송효근.getId())
                .name(송효근.getName())
                .rate(3.5).build();

        CongressmanGetListDTO 장지담_DTO = CongressmanGetListDTO.builder()
                .id(장지담.getId())
                .name(장지담.getName())
                .rate(3.5).build();

        CongressmanGetListDTO 정승수_DTO = CongressmanGetListDTO.builder()
                .id(정승수.getId())
                .name(정승수.getName())
                .rate(3.0).build();

        PageRequest pageRequest1 = PageRequest.of(0, 2, Sort.by("rating").descending());
        List<CongressmanGetListDTO> list1 = congressmanRepository.getList(pageRequest1, Long.MAX_VALUE, null);

        PageRequest pageRequest2 = PageRequest.of(1, 2, Sort.by("rating").descending());
        List<CongressmanGetListDTO> list2 = congressmanRepository.getList(pageRequest2, 송효근.getId(), 3.5);

        PageRequest pageRequest3 = PageRequest.of(1, 2, Sort.by("rating").descending());
        List<CongressmanGetListDTO> list3 = congressmanRepository.getList(pageRequest2, 정승수.getId(), 3.0);

        assertAll(
                () -> assertThat(list1).usingRecursiveComparison().isEqualTo(List.of(서재민_DTO, 김선균_DTO, 송효근_DTO)),
                () -> assertThat(list2).usingRecursiveComparison().isEqualTo(List.of(송효근_DTO, 장지담_DTO, 정승수_DTO)),
                () -> assertThat(list3).usingRecursiveComparison().isEqualTo(List.of(정승수_DTO))
        );
    }
}