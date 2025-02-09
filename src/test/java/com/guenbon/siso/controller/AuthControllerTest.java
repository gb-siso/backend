package com.guenbon.siso.controller;

import com.guenbon.siso.dto.error.ApiErrorResponse;
import com.guenbon.siso.exception.errorCode.KakaoApiErrorCode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest extends ControllerTest {

    public static final String BASE_URL = "/api/v1/auth";

    @ParameterizedTest
    @ValueSource(strings = {
            "KOE001", "KOE002", "KOE003", "KOE004", "KOE005", "KOE006", "KOE007", "KOE008",
            "KOE009", "KOE010", "KOE101", "KOE102", "KOE201", "KOE202", "KOE203", "KOE204",
            "KOE205", "KOE207", "KOE303", "KOE310", "KOE319", "KOE320", "KOE322", "KOE406",
            "KOE501", "KOE602", "KOE903", "KOE911", "KOE9798"
    })
    void kakaoLogin_authCodeError_ApiException(String errorCode) throws Exception {

        final KakaoApiErrorCode kakaoApiErrorCode = KakaoApiErrorCode.from(errorCode);
        final ApiErrorResponse expected = ApiErrorResponse.from(kakaoApiErrorCode);
        // when then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/login/kakao")
                        .param("error", errorCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(kakaoApiErrorCode.getHttpStatus().value()))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value(expected.getMessage()))
                .andExpect(jsonPath("$.code").value(expected.getCode()));
    }
}