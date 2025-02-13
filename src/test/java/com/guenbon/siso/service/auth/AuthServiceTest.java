package com.guenbon.siso.service.auth;

import com.guenbon.siso.dto.auth.IssueTokenResult;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.service.member.MemberService;
import com.guenbon.siso.support.fixture.member.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.ResponseCookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthService authService;
    @Mock
    MemberService memberService;
    @Mock
    JwtTokenProvider jwtTokenProvider;

    // 성공 테스트
    @Test
    @DisplayName("issueTokenWithKakaoId에서 이미 존재하는 회원일 시 토큰 생성 결과를 반환한다")
    void issueTokenWithKakaoId_existMember_IssueTokenResult() {
        // given
        final Long kakaoId = 1L;
        final String refreshToken = "refreshToken";
        final ResponseCookie refreshTokenCookie = ResponseCookie.from(AuthService.REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path(AuthService.PATH)
                .sameSite(SameSite.NONE.attributeValue())
                .build();

        final String accessToken = "accessToken";
        Member member = MemberFixture.builder().setKakaoId(1L).setId(1L).build();
        when(memberService.findByKakaoIdOrCreateMember(kakaoId)).thenReturn(member);
        when(jwtTokenProvider.createRefreshToken()).thenReturn(refreshToken);
        when(jwtTokenProvider.createAccessToken(member.getId())).thenReturn(accessToken);

        // when
        IssueTokenResult issueTokenResult = authService.issueTokenWithKakaoId(kakaoId);

        // then
        assertAll(
                () -> assertThat(issueTokenResult.getAccessToken()).isEqualTo(accessToken),
                () -> assertThat(issueTokenResult.getRefreshTokenCookie()).isEqualTo(refreshTokenCookie.toString()),
                () -> assertThat(issueTokenResult.getNickname()).isEqualTo(member.getNickname()),
                () -> assertThat(issueTokenResult.getImage()).isEqualTo(member.getImageUrl())
        );
    }

    @Test
    @DisplayName("issueTokenWithKakaoId에서 랜덤 닉네임 생성 성공 시 토큰 생성 결과를 반환한다")
    void issueTokenWithKakaoId_successNicknameGenerate_IssueTokenResult() {
        // given
        final Long kakaoId = 1L;
        final String refreshToken = "refreshToken";
        final ResponseCookie refreshTokenCookie = ResponseCookie.from(AuthService.REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path(AuthService.PATH)
                .sameSite(SameSite.NONE.attributeValue())
                .build();

        final String accessToken = "accessToken";
        Member member = MemberFixture.builder().setKakaoId(1L).setId(1L).build();
        when(memberService.findByKakaoIdOrCreateMember(kakaoId)).thenReturn(member);
        when(jwtTokenProvider.createRefreshToken()).thenReturn(refreshToken);
        when(jwtTokenProvider.createAccessToken(member.getId())).thenReturn(accessToken);

        // when
        IssueTokenResult issueTokenResult = authService.issueTokenWithKakaoId(kakaoId);

        // then
        assertAll(
                () -> assertThat(issueTokenResult.getAccessToken()).isEqualTo(accessToken),
                () -> assertThat(issueTokenResult.getRefreshTokenCookie()).isEqualTo(refreshTokenCookie.toString()),
                () -> assertThat(issueTokenResult.getNickname()).isEqualTo(member.getNickname()),
                () -> assertThat(issueTokenResult.getImage()).isEqualTo(member.getImageUrl())
        );
    }

    @Test
    @DisplayName("issueTokenWithNaverId에서 이미 존재하는 회원일 시 토큰 생성 결과를 반환한다")
    void issueTokenWithNaverId_existMember_IssueTokenResult() {
        // given
        final String naverId = "naverId";
        final String refreshToken = "refreshToken";
        final ResponseCookie refreshTokenCookie = ResponseCookie.from(AuthService.REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path(AuthService.PATH)
                .sameSite(SameSite.NONE.attributeValue())
                .build();

        final String accessToken = "accessToken";
        Member member = MemberFixture.builder().setKakaoId(1L).setId(1L).build();
        when(memberService.findByNaverIdOrCreateMember(naverId)).thenReturn(member);
        when(jwtTokenProvider.createRefreshToken()).thenReturn(refreshToken);
        when(jwtTokenProvider.createAccessToken(member.getId())).thenReturn(accessToken);

        // when
        IssueTokenResult issueTokenResult = authService.issueTokenWithNaverId(naverId);

        // then
        assertAll(
                () -> assertThat(issueTokenResult.getAccessToken()).isEqualTo(accessToken),
                () -> assertThat(issueTokenResult.getRefreshTokenCookie()).isEqualTo(refreshTokenCookie.toString()),
                () -> assertThat(issueTokenResult.getNickname()).isEqualTo(member.getNickname()),
                () -> assertThat(issueTokenResult.getImage()).isEqualTo(member.getImageUrl())
        );
    }

    @Test
    @DisplayName("issueTokenWithNaverId에서 랜덤 닉네임 생성 성공 시 토큰 생성 결과를 반환한다")
    void issueTokenWithNaverId_successNicknameGenerate_IssueTokenResult() {
        // given
        final String naverId = "naverId";
        final String refreshToken = "refreshToken";
        final ResponseCookie refreshTokenCookie = ResponseCookie.from(AuthService.REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path(AuthService.PATH)
                .sameSite(SameSite.NONE.attributeValue())
                .build();

        final String accessToken = "accessToken";
        Member member = MemberFixture.builder().setKakaoId(1L).setId(1L).build();
        when(memberService.findByNaverIdOrCreateMember(naverId)).thenReturn(member);
        when(jwtTokenProvider.createRefreshToken()).thenReturn(refreshToken);
        when(jwtTokenProvider.createAccessToken(member.getId())).thenReturn(accessToken);

        // when
        IssueTokenResult issueTokenResult = authService.issueTokenWithNaverId(naverId);

        // then
        assertAll(
                () -> assertThat(issueTokenResult.getAccessToken()).isEqualTo(accessToken),
                () -> assertThat(issueTokenResult.getRefreshTokenCookie()).isEqualTo(refreshTokenCookie.toString()),
                () -> assertThat(issueTokenResult.getNickname()).isEqualTo(member.getNickname()),
                () -> assertThat(issueTokenResult.getImage()).isEqualTo(member.getImageUrl())
        );
    }
}