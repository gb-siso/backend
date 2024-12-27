package com.guenbon.siso.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.support.fixture.CongressmanFixture;
import com.guenbon.siso.support.fixture.MemberFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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

        ratingRepository.save(Rating.builder()
                .member(jangmong99)
                .congressman(서재민)
                .rating(5.0F)
                .build());
        ratingRepository.save(Rating.builder()
                .member(jangmong99)
                .congressman(김선균)
                .rating(4.0F)
                .build());
        ratingRepository.save(Rating.builder()
                .member(jangmong99)
                .congressman(정승수)
                .rating(3.0F)
                .build());
        ratingRepository.save(Rating.builder()
                .member(jangmong99)
                .congressman(송효근)
                .rating(2.0F)
                .build());
        ratingRepository.save(Rating.builder()
                .member(chungmung99)
                .congressman(송효근)
                .rating(5.0F)
                .build());

        PageRequest pageRequest1 = PageRequest.of(0, 2, Sort.by("rating").descending());
        List<Congressman> list1 = congressmanRepository.getList(pageRequest1, Long.MAX_VALUE);

        PageRequest pageRequest2 = PageRequest.of(1, 2, Sort.by("rating").descending());
        List<Congressman> list2 = congressmanRepository.getList(pageRequest2, 김선균.getId());

        assertAll(
                () -> assertThat(list1).containsExactly(서재민, 김선균),
                () -> assertThat(list2).containsExactly(송효근, 정승수));
    }
}