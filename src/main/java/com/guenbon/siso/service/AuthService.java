package com.guenbon.siso.service;

import com.guenbon.siso.dto.auth.kakao.KakaoToken;
import com.guenbon.siso.dto.auth.kakao.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AuthService {
    public static final String KAUTH_GET_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    public static final String KAUTH_GET_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    public static final String REDIRECT_URL = "http://localhost:8080/api/v1/login/kakao";

    @Value("${api.kakao.key}")
    private String kakaoApiKey;


    private WebClient webClient = WebClient.builder().build();

    /**
     * 카카오 인증코드로 엑세스 토큰을 요청한다.
     *
     * @param authCode 카카오 인증코드
     * @return 카카오 토큰 {@link KakaoToken} 객체
     */
    public KakaoToken getToken(String authCode) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", kakaoApiKey);
        requestBody.add("redirect_url", REDIRECT_URL);
        requestBody.add("code", authCode);

        return webClient.post()
                .uri(KAUTH_GET_TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(requestBody))
                .retrieve()
                .bodyToMono(KakaoToken.class)
                .block();
    }

    /**
     * 카카오 토큰으로 회원정보를 요청한다.
     *
     * @param kakaoToken 카카오 토큰
     * @return 카카오 회원 정보 {@link UserInfo} 객체 (필요한 정보인 id만 받음)
     */
    public UserInfo getUserInfo(KakaoToken kakaoToken) {
        return webClient.get()
                .uri(KAUTH_GET_USER_INFO_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoToken.getAccessToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .bodyToMono(UserInfo.class)
                .block();
    }
}
