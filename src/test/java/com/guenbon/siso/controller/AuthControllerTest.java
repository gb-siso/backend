package com.guenbon.siso.controller;

import com.guenbon.siso.dto.auth.IssueTokenResult;
import com.guenbon.siso.dto.error.ApiErrorResponse;
import com.guenbon.siso.exception.ApiException;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.AuthErrorCode;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import com.guenbon.siso.exception.errorCode.KakaoApiErrorCode;
import com.guenbon.siso.exception.errorCode.NaverApiErrorCode;
import com.guenbon.siso.service.auth.AuthApiService;
import com.guenbon.siso.service.auth.AuthService;
import com.guenbon.siso.service.auth.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {AuthController.class})
@Slf4j
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    AuthApiService authApiService;
    @MockitoBean
    AuthService authService;
    @MockitoBean
    JwtTokenProvider jwtTokenProvider;

    public static final String BASE_URL = "/api/v1/auth";
    public static final String CODE = "code";
    public static final String ERROR = "error";
    public static final String LOGIN_KAKAO_PATH = "/login/kakao";
    public static final String LOGIN_NAVER_PATH = "/login/naver";

    @DisplayName("카카오 로그인 인가코드 받기 리다이렉트 시 쿼리 파라미터에 error 포함될 시 ApiErrorResponse로 응답한다")
    @ParameterizedTest
    @EnumSource(KakaoApiErrorCode.class)
    void kakaoLogin_redirectedWithErrorParameter_ApiErrorResponse(KakaoApiErrorCode kakaoApiErrorCode) throws Exception {
        // given
        final ApiErrorResponse expected = ApiErrorResponse.from(kakaoApiErrorCode);

        // when then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + LOGIN_KAKAO_PATH)
                        .param(ERROR, kakaoApiErrorCode.getCode()) // errorCode 대신 getCode() 사용
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(kakaoApiErrorCode.getHttpStatus().value())) // HTTP 상태 코드 비교
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(expected.getMessage()))
                .andExpect(jsonPath("$.code").value(expected.getCode()));
    }

    @DisplayName("카카오 로그인 시 authenticateWithKakao 메서드에서 ApiException이 발생할 시 ApiErrorResponse로 응답한다")
    @ParameterizedTest
    @EnumSource(KakaoApiErrorCode.class)
    void kakaoLogin_ApiExceptionAtAuthenticateWithKakao_ApiErrorResponse(KakaoApiErrorCode kakaoApiErrorCode) throws Exception {
        // given
        final ApiErrorResponse expected = ApiErrorResponse.from(kakaoApiErrorCode);
        final String invalidKakaoAuthCode = "invalidKakaoAuthCode";
        when(authApiService.authenticateWithKakao(invalidKakaoAuthCode)).thenThrow(new ApiException(kakaoApiErrorCode));

        // when then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + LOGIN_KAKAO_PATH)
                        .param(CODE, invalidKakaoAuthCode) // errorCode 대신 getCode() 사용
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(kakaoApiErrorCode.getHttpStatus().value())) // HTTP 상태 코드 비교
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(expected.getMessage()))
                .andExpect(jsonPath("$.code").value(expected.getCode()));
    }

    @DisplayName("카카오 로그인에 성공하면 LoginDTO를 응답한다")
    @Test
    void kakaoLogin_success_LoginDTO() throws Exception {
        // given
        final String validKakaoAuthCode = "validKakaoAuthCode";
        final String dummyRefreshToken = "dummyRefreshToken";
        final String refreshTokenCookie = createRefreshTokenCookie(dummyRefreshToken);
        final IssueTokenResult expected = buildDummyIssueTokenResult(refreshTokenCookie);

        when(authApiService.authenticateWithKakao(validKakaoAuthCode)).thenReturn(expected);

        // when then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + LOGIN_KAKAO_PATH)
                        .param(CODE, validKakaoAuthCode) // errorCode 대신 getCode() 사용
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful()) // HTTP 상태 코드 비교
                .andExpect(content().contentType("application/json"))
                .andExpect(cookie().value("refreshToken", dummyRefreshToken))
                .andExpect(jsonPath("$.nickname").value(expected.getNickname()))
                .andExpect(jsonPath("$.imageUrl").value(expected.getImage()))
                .andExpect(jsonPath("$.accessToken").value(expected.getAccessToken()));
    }

    @DisplayName("네이버 로그인 인가코드 받기 리다이렉트 시 쿼리 파라미터에 error 포함될 시 ApiErrorResponse로 응답한다")
    @ParameterizedTest
    @EnumSource(NaverApiErrorCode.class)
    void naverLogin_redirectedWithErrorParameter_ApiErrorResponse(NaverApiErrorCode naverApiErrorCode) throws Exception {
        // given
        final ApiErrorResponse expected = ApiErrorResponse.from(naverApiErrorCode);
        // when then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + LOGIN_NAVER_PATH)
                        .param(ERROR, naverApiErrorCode.getCode()) // errorCode 대신 getCode() 사용
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(naverApiErrorCode.getHttpStatus().value())) // HTTP 상태 코드 비교
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(expected.getMessage()))
                .andExpect(jsonPath("$.code").value(expected.getCode()));
    }

    @DisplayName("네이버 로그인 시 authenticateWithNaver 메서드에서 ApiException이 발생할 시 ApiErrorResponse로 응답한다")
    @ParameterizedTest
    @EnumSource(NaverApiErrorCode.class)
    void naverLogin_ApiExceptionAtAuthenticateWithNaver_ApiErrorResponse(NaverApiErrorCode naverApiErrorCode) throws Exception {
        // given
        final ApiErrorResponse expected = ApiErrorResponse.from(naverApiErrorCode);
        final String invalidNaverAuthCode = "invalidNaverAuthCode";
        final String state = "state";
        when(authApiService.authenticateWithNaver(invalidNaverAuthCode, state)).thenThrow(new ApiException(naverApiErrorCode));

        // when then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + LOGIN_NAVER_PATH)
                        .param(CODE, invalidNaverAuthCode)
                        .param("state", state)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(naverApiErrorCode.getHttpStatus().value())) // HTTP 상태 코드 비교
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(expected.getMessage()))
                .andExpect(jsonPath("$.code").value(expected.getCode()));
    }

    @DisplayName("네이버 로그인에 성공하면 LoginDTO를 응답한다")
    @Test
    void naverLogin_success_LoginDTO() throws Exception {
        // given
        final String validNaverAuthCode = "validNaverAuthCode";
        final String state = "state";
        final String dummyRefreshToken = "dummyRefreshToken";
        final String refreshTokenCookie = createRefreshTokenCookie(dummyRefreshToken);
        final IssueTokenResult expected = buildDummyIssueTokenResult(refreshTokenCookie);

        when(authApiService.authenticateWithNaver(validNaverAuthCode, state)).thenReturn(expected);
        // when then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + LOGIN_NAVER_PATH)
                        .param(CODE, validNaverAuthCode) // errorCode 대신 getCode() 사용
                        .param("state", state)//
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful()) // HTTP 상태 코드 비교
                .andExpect(content().contentType("application/json"))
                .andExpect(cookie().value("refreshToken", dummyRefreshToken))
                .andExpect(jsonPath("$.nickname").value(expected.getNickname()))
                .andExpect(jsonPath("$.imageUrl").value(expected.getImage()))
                .andExpect(jsonPath("$.accessToken").value(expected.getAccessToken()));
    }

    // 재발급 실패 테스트
    @DisplayName("카카오 accessToken 재발급 요청 시 refreshToken이 유효하지 않으면 에러 응답한다.")
    @EnumSource(AuthErrorCode.class)
    @ParameterizedTest
    void kakaoReissue_invalidRefreshToken_errorResponse(AuthErrorCode authErrorCode) throws Exception {
        // given
        final String invalidRefreshToken = "invalidRefreshToken";
        when(authService.reissueWithKakao(invalidRefreshToken)).thenThrow(new CustomException(authErrorCode));
        MockCookie cookie = new MockCookie("refreshToken", invalidRefreshToken);
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/reissue/kakao").cookie(cookie))
                .andDo(print())
                .andExpect(content().contentType("application/json"))
                .andExpect(status().is(authErrorCode.getHttpStatus().value()))
                .andExpect(jsonPath("$.message").value(authErrorCode.getMessage()))
                .andExpect(jsonPath("$.code").value(authErrorCode.getCode()));
    }

    @DisplayName("카카오 accessToken 재발급 요청 시 refreshToken이 없으면 에러 응답한다.")
    @Test
    void kakaoReissue_noRefreshToken_errorResponse() throws Exception {
        // given
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/reissue/kakao"))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("요청 시 필수 쿠키 값 없음 refreshToken"))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.MISSING_COOKIE.getCode()));
    }

    @DisplayName("카카오 accessToken 재발급 요청 시 refreshToken이 유효하고 db에 존재하면 토큰을 재발급한다.")
    @Test
    void kakaoReissue_success_reissueToken() throws Exception {
        // given
        final String validRefreshToken = "validRefreshToken";
        final String dummyRefreshToken = "dummyRefreshToken";
        final String refreshTokenCookie = createRefreshTokenCookie(dummyRefreshToken);
        final IssueTokenResult expected = buildDummyIssueTokenResult(refreshTokenCookie);
        when(authService.reissueWithKakao(validRefreshToken)).thenReturn(expected);
        MockCookie cookie = new MockCookie("refreshToken", validRefreshToken);
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/reissue/kakao").cookie(cookie))
                .andDo(print())
                .andExpect(content().contentType("application/json"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(cookie().value("refreshToken", dummyRefreshToken))
                .andExpect(jsonPath("$.nickname").value(expected.getNickname()))
                .andExpect(jsonPath("$.imageUrl").value(expected.getImage()))
                .andExpect(jsonPath("$.accessToken").value(expected.getAccessToken()));
    }

    private static String createRefreshTokenCookie(String value) {
        return ResponseCookie.from("refreshToken", value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite(Cookie.SameSite.NONE.attributeValue())
                .build().toString();
    }

    private static IssueTokenResult buildDummyIssueTokenResult(String refreshTokenCookie) {
        return IssueTokenResult.builder()
                .refreshTokenCookie(refreshTokenCookie)
                .accessToken("accessToken")
                .image("image")
                .nickname("nickname").build();
    }
}