package com.guenbon.siso.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guenbon.siso.dto.auth.kakao.KakaoToken;
import com.guenbon.siso.dto.auth.kakao.UserInfo;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import com.guenbon.siso.exception.errorCode.KakaoApiErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AuthService {
    public static final String KAUTH_GET_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    public static final String KAUTH_GET_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    public static final String REDIRECT_URL = "http://localhost:8080/api/v1/login/kakao";

    @Value("${api.kakao.key}")
    private String kakaoApiKey;
    private final WebClient webClient = WebClient.builder().build();

    private Mono<? extends Throwable> handleErrorResponse(String errorBody) {
        log.error("Error response body: {}", errorBody);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode errorResponse = objectMapper.readTree(errorBody);
            int code = errorResponse.path("code").asInt();

            log.error("Error code: {}", code);
            return Mono.error(new CustomException(KakaoApiErrorCode.from(String.valueOf(code))));
        } catch (JsonProcessingException e) {
            log.error("Failed to parse error response", e);
            return Mono.error(new CustomException(CommonErrorCode.JSON_PARSE_ERROR));
        }
    }

    public UserInfo getUserInfo(KakaoToken kakaoToken) {
        return webClient.get()
                .uri(KAUTH_GET_USER_INFO_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoToken.getAccessToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .onStatus(httpStatus -> httpStatus != HttpStatus.OK, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> handleErrorResponse(errorBody))
                )
                .bodyToMono(UserInfo.class)
                .block();
    }

    public KakaoToken getToken(String authCode) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", kakaoApiKey);
        requestBody.add("client_id", "123abcdef");
        requestBody.add("redirect_url", REDIRECT_URL);
        requestBody.add("code", authCode);

        return webClient.post()
                .uri(KAUTH_GET_TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(requestBody))
                .retrieve()
                .onStatus(httpStatus -> httpStatus != HttpStatus.OK, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> handleErrorResponse(errorBody))
                )
                .bodyToMono(KakaoToken.class)
                .block();
    }

}
