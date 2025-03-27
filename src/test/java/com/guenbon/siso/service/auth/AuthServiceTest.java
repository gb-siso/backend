package com.guenbon.siso.service.auth;

import com.guenbon.siso.dto.auth.IssueTokenResult;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.AuthErrorCode;
import com.guenbon.siso.exception.errorCode.MemberErrorCode;
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
import org.springframework.http.ResponseCookie;

import java.util.stream.Stream;

import static com.guenbon.siso.exception.errorCode.AuthErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    // dummy value
    private final String accessToken = "accessToken";
    private final String refreshToken = "refreshToken";

    // 성공 테스트
    @Test
    @DisplayName("issueTokenWithKakaoId에서 이미 존재하는 회원일 시 토큰 생성 결과를 반환한다")
    void issueTokenWithKakaoId_existMember_IssueTokenResult() {
        // given
        final Long kakaoId = 1L;
        final ResponseCookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);
        Member member = MemberFixture.builder().setKakaoId(1L).setId(1L).build();
        when(memberService.findByKakaoIdOrCreateMember(kakaoId)).thenReturn(member);
        when(jwtTokenProvider.createRefreshToken()).thenReturn(refreshToken);
        when(jwtTokenProvider.createAccessToken(member)).thenReturn(accessToken);
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
        final ResponseCookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);
        Member member = MemberFixture.builder().setKakaoId(1L).setId(1L).build();
        when(memberService.findByKakaoIdOrCreateMember(kakaoId)).thenReturn(member);
        when(jwtTokenProvider.createRefreshToken()).thenReturn(refreshToken);
        when(jwtTokenProvider.createAccessToken(member)).thenReturn(accessToken);

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
        final ResponseCookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);
        Member member = MemberFixture.builder().setKakaoId(1L).setId(1L).build();
        when(memberService.findByNaverIdOrCreateMember(naverId)).thenReturn(member);
        when(jwtTokenProvider.createRefreshToken()).thenReturn(refreshToken);
        when(jwtTokenProvider.createAccessToken(member)).thenReturn(accessToken);
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
        final ResponseCookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);
        Member member = MemberFixture.builder().setKakaoId(1L).setId(1L).build();
        when(memberService.findByNaverIdOrCreateMember(naverId)).thenReturn(member);
        when(jwtTokenProvider.createRefreshToken()).thenReturn(refreshToken);
        when(jwtTokenProvider.createAccessToken(member)).thenReturn(accessToken);
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

    @DisplayName("reissue 파라미터로 유효하지 않은 refreshToken 전달 시 알맞은 에러코드와 CustomException을 던진다.")
    @ParameterizedTest
    @MethodSource("provideInvalidRefreshTokenErrorCode")
    void reissue_invalidRefreshToken_CustomException(AuthErrorCode authErrorCode) {
        // given
        when(jwtTokenProvider.verifySignature(refreshToken)).thenThrow(new CustomException(authErrorCode));
        // when, then
        assertThatThrownBy(() -> authService.reissue(refreshToken))
                .isInstanceOf(CustomException.class)
                .hasMessage(authErrorCode.getMessage());
        ;
    }

    private static Stream<AuthErrorCode> provideInvalidRefreshTokenErrorCode() {
        return Stream.of(EXPIRED, UNSUPPORTED, MALFORMED, SIGNATURE, NULL_OR_BLANK_TOKEN);
    }

    @DisplayName("reissue 파라미터로 데이터베이스에 존재하지 않는 refreshToken 전달 시 NOT_EXISTS_IN_DATABASE 에러코드 CustomException을 던진다.")
    @Test
    void reissue_refreshTokenNotExistsInDatabase_CustomException() {
        // given
        when(memberService.findByRefreshToken(refreshToken)).thenThrow(new CustomException(NOT_EXISTS_IN_DATABASE));
        // when, then
        assertThatThrownBy(() -> authService.reissue(refreshToken))
                .isInstanceOf(CustomException.class)
                .hasMessage(NOT_EXISTS_IN_DATABASE.getMessage());
        ;
    }

    @DisplayName("reissue 파라미터로 데이터베이스에 존재하는 유효한 refreshToken 전달 시 IssueTokenResult를 응답한다.")
    @Test
    void reissue_success_IssueTokenResult() {
        // given
        final Member member = MemberFixture.builder().setId(1L).build();
        final String reissueAccessToken = "reissueAccessToken";
        final String reissueRefreshToken = "reissueRefreshToken";
        final String expectedRefreshTokenCookie = createRefreshTokenCookie(reissueRefreshToken).toString();
        when(memberService.findByRefreshToken(refreshToken)).thenReturn(member);
        when(jwtTokenProvider.createRefreshToken()).thenReturn(reissueRefreshToken);
        when(jwtTokenProvider.createAccessToken(member)).thenReturn(reissueAccessToken);
        // when
        IssueTokenResult issueTokenResult = authService.reissue(refreshToken);
        // then
        assertAll(
                () -> assertThat(issueTokenResult.getAccessToken()).isEqualTo(reissueAccessToken),
                () -> assertThat(issueTokenResult.getImage()).isEqualTo(member.getImageUrl()),
                () -> assertThat(issueTokenResult.getNickname()).isEqualTo(member.getNickname()),
                () -> assertThat(issueTokenResult.getRefreshTokenCookie()).isEqualTo(expectedRefreshTokenCookie)
        );
    }

    private static ResponseCookie createRefreshTokenCookie(String value) {
        return ResponseCookie.from("refreshToken", value)
                .httpOnly(false)
                .secure(true)
                .path("/")
                .sameSite("None")
                .build();
    }

    @Test
    @DisplayName("존재하지 않는 memberId로 logout 호출 시 member 존재하지 않음 예외를 던진다")
    void notExistsMember_logout_Exception() {
        // given
        final Long memberId = 1L;
        when(memberService.findById(memberId)).thenThrow(new CustomException(MemberErrorCode.NOT_EXISTS));

        // when, then
        assertThatThrownBy(() -> authService.logout(memberId),
                MemberErrorCode.NOT_EXISTS.getMessage(),
                CustomException.class);
    }

    @Test
    @DisplayName("유효한 memberId로 logout 호출 시 성공한다.")
    void validMemberId_logout_success() {
        // given
        final Long memberId = 1L;
        Member member = MemberFixture.builder().setId(memberId).build();
        when(memberService.findById(memberId)).thenReturn(member);

        // when
        authService.logout(memberId);

        // then
        assertNull(member.getRefreshToken());
    }
}