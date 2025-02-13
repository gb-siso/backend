package com.guenbon.siso.service.auth;

import com.guenbon.siso.dto.auth.IssueTokenResult;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.AuthErrorCode;
import com.guenbon.siso.service.member.MemberService;
import com.guenbon.siso.support.fixture.member.MemberFixture;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.ResponseCookie;

import java.util.stream.Stream;

import static com.guenbon.siso.exception.errorCode.AuthErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Slf4j
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

    @DisplayName("reissueWithKakao 파라미터로 유효하지 않은 refreshToken 전달 시 알맞은 에러코드와 CustomException을 던진다.")
    @ParameterizedTest
    @MethodSource("provideInvalidRefreshTokenErrorCode")
    void reissueWithKakao_invalidRefreshToken_CustomException(AuthErrorCode authErrorCode) {
        // given
        final String invalidRefreshToken = "invalidRefreshToken";
        when(jwtTokenProvider.verifySignature(invalidRefreshToken)).thenThrow(new CustomException(authErrorCode));
        // when, then
        assertThrows(CustomException.class, () -> authService.reissueWithKakao(invalidRefreshToken), authErrorCode.getMessage());
    }

    private static Stream<AuthErrorCode> provideInvalidRefreshTokenErrorCode() {
        return Stream.of(EXPIRED, UNSUPPORTED, MALFORMED, SIGNATURE, NULL_OR_BLANK_TOKEN);
    }

    @DisplayName("reissueWithKakao 파라미터로 데이터베이스에 존재하지 않는 refreshToken 전달 시 NOT_EXISTS_IN_DATABASE 에러코드 CustomException을 던진다.")
    @Test
    void reissueWithKakao_refreshTokenNotExistsInDatabase_CustomException() {
        // given
        final String notInDatabaseRefreshToken = "notInDatabaseRefreshToken";
        when(memberService.findByRefreshToken(notInDatabaseRefreshToken)).thenThrow(new CustomException(NOT_EXISTS_IN_DATABASE));
        // when, then
        assertThrows(CustomException.class, () -> authService.reissueWithKakao(notInDatabaseRefreshToken), NOT_EXISTS_IN_DATABASE.getMessage());
    }

    @DisplayName("reissueWithKakao 파라미터로 데이터베이스에 존재하는 유효한 refreshToken 전달 시 IssueTokenResult를 응답한다.")
    @Test
    void reissueWithKakao_success_IssueTokenResult() {
        // given
        final String validRefreshToken = "notInDatabaseRefreshToken";
        final Member member = MemberFixture.builder().setId(1L).build();
        final String reissueAccessToken = "reissueAccessToken";
        final String reissueRefreshToken = "reissueRefreshToken";
        final String expectedRefreshTokenCookie = ResponseCookie.from("refreshToken", reissueRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .build()
                .toString();
        when(memberService.findByRefreshToken(validRefreshToken)).thenReturn(member);
        when(jwtTokenProvider.createRefreshToken()).thenReturn(reissueRefreshToken);
        when(jwtTokenProvider.createAccessToken(member.getId())).thenReturn(reissueAccessToken);
        // when
        IssueTokenResult issueTokenResult = authService.reissueWithKakao(validRefreshToken);
        // then
        assertAll(
                () -> assertThat(issueTokenResult.getAccessToken()).isEqualTo(reissueAccessToken),
                () -> assertThat(issueTokenResult.getImage()).isEqualTo(member.getImageUrl()),
                () -> assertThat(issueTokenResult.getNickname()).isEqualTo(member.getNickname()),
                () -> assertThat(issueTokenResult.getRefreshTokenCookie()).isEqualTo(expectedRefreshTokenCookie)
        );
    }
}