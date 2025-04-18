package com.guenbon.siso.service;

import com.guenbon.siso.entity.Member;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.AuthErrorCode;
import com.guenbon.siso.exception.errorCode.MemberErrorCode;
import com.guenbon.siso.repository.MemberRepository;
import com.guenbon.siso.service.auth.JwtTokenProvider;
import com.guenbon.siso.service.member.MemberService;
import com.guenbon.siso.support.fixture.member.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberService memberService;
    @Mock
    MemberRepository memberRepository;
    @Mock
    JwtTokenProvider jwtTokenProvider;

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
        assertThatThrownBy(() -> memberService.findById(존재하지_않는_ID))
                .isInstanceOf(CustomException.class)
                .hasMessage(MemberErrorCode.NOT_EXISTS.getMessage());
    }

    // 실패 테스트
    @Test
    @DisplayName("findByKakaoIdOrCreateMember에서 회원 생성 시 랜덤 닉네임 생성이 계속 중복되면 예외를 던진다")
    void findByKakaoIdOrCreateMember_duplicatedNickname_InternalServerException() {
        // given
        final Long kakaoId = 1L;
        when(memberRepository.findByKakaoId(kakaoId)).thenReturn(Optional.empty());
        when(memberRepository.existsByNickname(any(String.class))).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> memberService.findByKakaoIdOrCreateMember(kakaoId))
                .isInstanceOf(CustomException.class)
                .hasMessage(MemberErrorCode.RANDOM_NICKNAME_GENERATE_FAILED.getMessage());
        ;
    }

    @Test
    @DisplayName("findByKakaoIdOrCreateMember에서 이미 가입한 회원일 경우 해당 회원을 반환한다.")
    void findByKakaoIdOrCreateMember_alreadyRegistered_registeredMember() {
        // given
        final Long kakaoId = 1L;
        final Member expected = MemberFixture.builder().setKakaoId(kakaoId).build();
        when(memberRepository.findByKakaoId(kakaoId)).thenReturn(Optional.of(expected));
        // when, then
        assertThat(memberService.findByKakaoIdOrCreateMember(kakaoId)).isEqualTo(expected);
    }

    @Test
    @DisplayName("findByKakaoIdOrCreateMember에서 랜덤 닉네임 생성 성공 시 회원을 반환한다.")
    void findByKakaoIdOrCreateMember_success_Member() {
        // given
        final Long kakaoId = 1L;
        final Member expected = MemberFixture.builder().setKakaoId(kakaoId).build();
        when(memberRepository.findByKakaoId(kakaoId)).thenReturn(Optional.empty());
        when(memberRepository.existsByNickname(any(String.class))).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(expected);

        // when, then
        assertThat(memberService.findByKakaoIdOrCreateMember(kakaoId)).isEqualTo(expected);
    }

    @Test
    @DisplayName("findByNaverIdOrCreateMember에서 회원 생성 시 랜덤 닉네임 생성이 계속 중복되면 예외를 던진다")
    void findByNaverIdOrCreateMember_duplicatedNickname_InternalServerException() {
        // given
        final String naverId = "naverId";
        when(memberRepository.findByNaverId(naverId)).thenReturn(Optional.empty());
        when(memberRepository.existsByNickname(any(String.class))).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> memberService.findByNaverIdOrCreateMember(naverId))
                .isInstanceOf(CustomException.class)
                .hasMessage(MemberErrorCode.RANDOM_NICKNAME_GENERATE_FAILED.getMessage());
        ;
    }

    @Test
    @DisplayName("findByNaverIdOrCreateMember에서 이미 가입한 회원일 경우 해당 회원을 반환한다.")
    void findByNaverIdOrCreateMember_alreadyRegistered_registeredMember() {
        // given
        final String naverId = "naverId";
        final Member expected = MemberFixture.builder().setNaverId(naverId).build();
        when(memberRepository.findByNaverId(naverId)).thenReturn(Optional.of(expected));
        // when, then
        assertThat(memberService.findByNaverIdOrCreateMember(naverId)).isEqualTo(expected);
    }

    @Test
    @DisplayName("findByNaverIdOrCreateMember에서 랜덤 닉네임 생성 성공 시 회원을 반환한다.")
    void findByNaverIdOrCreateMember_success_Member() {
        // given
        final String naverId = "naverId";
        final Member expected = MemberFixture.builder().setNaverId(naverId).build();
        when(memberRepository.findByNaverId(naverId)).thenReturn(Optional.empty());
        when(memberRepository.existsByNickname(any(String.class))).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(expected);

        // when, then
        assertThat(memberService.findByNaverIdOrCreateMember(naverId)).isEqualTo(expected);
    }

    @Test
    @DisplayName("findByRefreshToken가 db에서 refreshToken을 못찾으면 NOT_EXISTS_IN_DATABASE 에러코드인 CustomException을 던진다.")
    void findByRefreshToken_notExists_CustomException() {
        // given
        final String refreshToken = "refreshToken";
        when(memberRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.empty());
        // when, then
        assertThatThrownBy(() -> memberService.findByRefreshToken(refreshToken))
                .isInstanceOf(CustomException.class)
                .hasMessage(AuthErrorCode.NOT_EXISTS_IN_DATABASE.getMessage());
        ;
    }

    @Test
    @DisplayName("findByRefreshToken가 db에서 refreshToken을 못찾으면 Member를 반환한다.")
    void findByRefreshToken_success_Member() {
        // given
        final String refreshToken = "refreshToken";
        final Member expected = MemberFixture.builder().setRefreshToken(refreshToken).build();
        when(memberRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.of(expected));
        // when
        Member actual = memberService.findByRefreshToken(refreshToken);
        // then
        assertThat(actual).isEqualTo(expected);
    }
}