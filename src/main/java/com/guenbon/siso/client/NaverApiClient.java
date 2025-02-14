package com.guenbon.siso.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.guenbon.siso.support.constants.ApiConstants.*;

@Slf4j
@Component
public class NaverApiClient {

    private static final WebClient webClient = WebClient.builder().build();

    @Value("${api.naver.client.id}")
    private String naverClientId;
    @Value("${api.naver.client.secret}")
    private String naverClientSecret;

    public Mono<String> requestToken(String code, String state) {
        return webClient.get()
                .uri(NAUTH_GET_TOKEN_URL, uriBuilder -> uriBuilder
                        .queryParam(GRANT_TYPE, AUTHORIZATION_CODE)
                        .queryParam(NAVER_CODE_PARAMETER, code)
                        .queryParam(CLIENT_ID, naverClientId)
                        .queryParam(CLIENT_SECRET, naverClientSecret)
                        .queryParam(STATE, state)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(body -> log.info("log naver api body: {}", body));
    }

    public Mono<String> requestUserInfo(String accessToken) {
        return webClient.get()
                .uri(NAUTH_GET_USER_INFO_URL)
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("Naver reqeustUserInfo Response: {}", response)); // 응답 로그 출력
    }
}
