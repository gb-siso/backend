package com.guenbon.siso.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class NaverApiClient {

    private static final WebClient webClient = WebClient.builder().build();
    private static final String NAUTH_GET_TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
    private static final String NAUTH_GET_USER_INFO_URL = "https://openapi.naver.com/v1/nid/me";

    @Value("${api.naver.client.id}")
    private String naverClientId;
    @Value("${api.naver.client.secret}")
    private String naverClientSecret;

    public Mono<String> requestToken(String code, String state) {
        return webClient.get()
                .uri("https://nid.naver.com/oauth2.0/token" +
                        "?grant_type=authorization_code" +
                        "&code=" + code +
                        "&client_id=" + naverClientId +
                        "&client_secret=" + naverClientSecret +
                        "&state=" + state)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(body -> log.info("log naver api body: {}", body));
    }

    public Mono<String> requestUserInfo(String accessToken) {
        return webClient.get()
                .uri(NAUTH_GET_USER_INFO_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("Naver reqeustUserInfo Response: {}", response)); // 응답 로그 출력
    }

}
