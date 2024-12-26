package com.guenbon.siso.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.guenbon.siso.entity.Member;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.MemberErrorCode;
import com.guenbon.siso.repository.MemberRepository;
import com.guenbon.siso.support.fixture.MemberFixture;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberService memberService;
    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("findById가 존재하는 국회의원을 반환한다")
    void findById_exist_Congressman() {
        // given
        final Member 장몽이 = MemberFixture.builder()
                .setId(1L)
                .setNickname("장몽이")
                .build();
        when(memberRepository.findById(장몽이.getId())).thenReturn(Optional.of(장몽이));

        // when
        final Member actual = memberService.findById(장몽이.getId());

        // then
        assertThat(actual).isEqualTo(장몽이);
    }

    @Test
    @DisplayName("findById가 존재하지 않는 회원에 대해 BadRequestExceptiond을 던진다")
    void findById_notExist_NotExistException() {
        // given
        final Long 존재하지_않는_ID = 1L;
        when(memberRepository.findById(존재하지_않는_ID)).thenReturn(Optional.empty());
        // when then
        assertThrows(BadRequestException.class, () -> memberService.findById(존재하지_않는_ID),
                MemberErrorCode.NOT_EXISTS.getMessage());
    }
}