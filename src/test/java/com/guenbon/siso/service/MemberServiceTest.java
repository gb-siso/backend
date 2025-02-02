package com.guenbon.siso.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.guenbon.siso.dto.auth.IssueTokenResult;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.MemberErrorCode;
import com.guenbon.siso.repository.MemberRepository;
import com.guenbon.siso.support.fixture.member.MemberFixture;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.ResponseCookie;

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
        assertThrows(CustomException.class, () -> memberService.findById(존재하지_않는_ID),
                MemberErrorCode.NOT_EXISTS.getMessage());
    }

    // 실패 테스트
    @Test
    @DisplayName("issueToken에서 회원 생성 시 랜덤 닉네임 생성이 계속 중복되면 예외를 던진다")
    void issueToken_duplicatedNickname_InternalServerException() {
        // given
        final Long kakaoId = 1L;
        when(memberRepository.findByKakaoId(kakaoId)).thenReturn(Optional.empty());
        when(memberRepository.existsByNickname(any(String.class))).thenReturn(true);

        // when, then
        assertThrows(CustomException.class, () -> memberService.issueToken(kakaoId),
                MemberErrorCode.RANDOM_NICKNAME_GENERATE_FAILED.getMessage());
    }

    // 성공 테스트
    @Test
    @DisplayName("issueToken에서 이미 존재하는 회원일 시 토큰 생성 결과를 반환한다")
    void issueToken_existMember_IssueTokenResult() {
        // given
        final Long kakaoId = 1L;
        final String refreshToken = "refreshToken";
        final ResponseCookie refreshTokenCookie = ResponseCookie.from(MemberService.REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path(MemberService.PATH)
                .sameSite(SameSite.NONE.attributeValue())
                .build();

        final String accessToken = "accessToken";
        Member member = MemberFixture.builder().setKakaoId(1L).setId(1L).build();
        when(memberRepository.findByKakaoId(kakaoId)).thenReturn(Optional.of(member));
        when(jwtTokenProvider.createRefreshToken(member.getId())).thenReturn(refreshToken);
        when(jwtTokenProvider.createAccessToken(member.getId())).thenReturn(accessToken);

        // when
        IssueTokenResult issueTokenResult = memberService.issueToken(kakaoId);

        // then
        assertAll(
                () -> assertThat(issueTokenResult.getAccessToken()).isEqualTo(accessToken),
                () -> assertThat(issueTokenResult.getRefreshTokenCookie()).isEqualTo(refreshTokenCookie.toString()),
                () -> assertThat(issueTokenResult.getNickname()).isEqualTo(member.getNickname()),
                () -> assertThat(issueTokenResult.getImage()).isEqualTo(member.getImageUrl())
        );
    }

    @Test
    @DisplayName("issueToken에서 랜덤 닉네임 생성 성공 시 토큰 생성 결과를 반환한다")
    void issueToken_successNicknameGenerate_IssueTokenResult() {
        // given
        final Long kakaoId = 1L;
        final String refreshToken = "refreshToken";
        final ResponseCookie refreshTokenCookie = ResponseCookie.from(MemberService.REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path(MemberService.PATH)
                .sameSite(SameSite.NONE.attributeValue())
                .build();

        final String accessToken = "accessToken";
        Member member = MemberFixture.builder().setKakaoId(1L).setId(1L).build();
        when(memberRepository.findByKakaoId(kakaoId)).thenReturn(Optional.empty());
        when(memberRepository.existsByNickname(any(String.class))).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(jwtTokenProvider.createRefreshToken(member.getId())).thenReturn(refreshToken);
        when(jwtTokenProvider.createAccessToken(member.getId())).thenReturn(accessToken);

        // when
        IssueTokenResult issueTokenResult = memberService.issueToken(kakaoId);

        // then
        assertAll(
                () -> assertThat(issueTokenResult.getAccessToken()).isEqualTo(accessToken),
                () -> assertThat(issueTokenResult.getRefreshTokenCookie()).isEqualTo(refreshTokenCookie.toString()),
                () -> assertThat(issueTokenResult.getNickname()).isEqualTo(member.getNickname()),
                () -> assertThat(issueTokenResult.getImage()).isEqualTo(member.getImageUrl())
        );
    }
}