package com.guenbon.siso.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class KakaoApiClient {

    private static final WebClient webClient = WebClient.builder().build();
    private static final String KAUTH_GET_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAUTH_GET_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String REDIRECT_URL = "http://localhost:8080/api/v1/login/kakao";

    @Value("${api.kakao.key}")
    private String kakaoApiKey;

    @Transactional(propagation = Propagation.NEVER)
    public Mono<String> requestUserInfo(String accessToken) {
        return webClient.get()
                .uri(KAUTH_GET_USER_INFO_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("Response: {}", response)); // 응답 로그 출력
    }

    @Transactional(propagation = Propagation.NEVER)
    public Mono<String> requestToken(String authCode) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", kakaoApiKey);
        requestBody.add("redirect_url", REDIRECT_URL);
        requestBody.add("code", authCode);

        return webClient.post()
                .uri(KAUTH_GET_TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(requestBody)
                .exchangeToMono(response -> {
                    return response.bodyToMono(String.class)
                            .doOnNext(body -> log.info("log kakao api body: {}", body));
                });
    }
}
