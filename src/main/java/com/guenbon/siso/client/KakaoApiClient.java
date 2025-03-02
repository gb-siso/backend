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
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.guenbon.siso.support.constants.ApiConstants.*;

@Slf4j
@Component
public class KakaoApiClient {
    private static final WebClient webClient = WebClient.builder().build();

    @Value("${api.kakao.key}")
    private String kakaoApiKey;

    @Transactional(propagation = Propagation.NEVER)
    public Mono<String> requestUserInfo(String accessToken) {
        return webClient.get()
                .uri(KAUTH_GET_USER_INFO_URL)
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("Response: {}", response)); // 응답 로그 출력
    }

    @Transactional(propagation = Propagation.NEVER)
    public Mono<String> requestToken(String authCode) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add(GRANT_TYPE, AUTHORIZATION_CODE);
        requestBody.add(CLIENT_ID, kakaoApiKey);
        requestBody.add(REDIRECT_URL, URLEncoder.encode(KAUTH_REDIRECT_URL, StandardCharsets.UTF_8)); // URL 인코딩
        requestBody.add(CODE_QUERY_PARAMETER_LOWERCASE, authCode);

        return webClient.post()
                .uri(KAUTH_GET_TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(requestBody)) // bodyValue() 대신 사용
                .exchangeToMono(response -> response.bodyToMono(String.class)
                        .doOnNext(body -> log.info("log kakao api body: {}", body))
                );
    }
}
