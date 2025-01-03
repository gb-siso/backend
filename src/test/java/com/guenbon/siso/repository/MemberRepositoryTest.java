package com.guenbon.siso.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.guenbon.siso.config.QuerydslConfig;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.support.fixture.member.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QuerydslConfig.class) // QueryDslConfig 추가\
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("findById 가 존재하는 회원에 대해 정상 반환한다")
    void findById_exists_congressman() {
        // given
        final Member 장몽이 = MemberFixture.builder().setNickname("장몽이").build();
        final Member expected = memberRepository.save(장몽이);

        // when
        Member actual = memberRepository.findById(expected.getId()).get();

        // then
        assertThat(actual).isEqualTo(expected);
    }

}