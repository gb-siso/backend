package com.guenbon.siso.controller;

import com.guenbon.siso.dto.auth.IssueTokenResult;
import com.guenbon.siso.dto.error.ApiErrorResponse;
import com.guenbon.siso.exception.ApiException;
import com.guenbon.siso.exception.errorCode.KakaoApiErrorCode;
import com.guenbon.siso.service.auth.AuthApiService;
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
    protected MockMvc mockMvc;
    @MockitoBean
    protected AuthApiService authApiService;
    @MockitoBean
    protected JwtTokenProvider jwtTokenProvider;

    public static final String BASE_URL = "/api/v1/auth";
    public static final String CODE = "code";
    public static final String ERROR = "error";
    public static final String LOGIN_KAKAO_PATH = "/login/kakao";

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
        final String refreshTokenCookie = ResponseCookie.from("refreshToken", dummyRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite(Cookie.SameSite.NONE.attributeValue())
                .build().toString();
        final IssueTokenResult expected = IssueTokenResult.builder()
                .refreshTokenCookie(refreshTokenCookie)
                .accessToken("accessToken")
                .image("image")
                .nickname("nickname").build();

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
}