package com.guenbon.siso.client;

import static com.guenbon.siso.support.constants.ApiConstants.KEY;
import static com.guenbon.siso.support.constants.ApiConstants.P_INDEX;
import static com.guenbon.siso.support.constants.ApiConstants.P_SIZE;
import static com.guenbon.siso.support.constants.ApiConstants.TYPE;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class CongressApiClient {


    private static final WebClient webClient = WebClient.builder().build();

    @Transactional(propagation = Propagation.NEVER)
    public String getApiResponse(Pageable pageable, String baseUrl, String apiKey, HashMap<String, String> params) {
        String uriString = buildUrlString(pageable, baseUrl, apiKey, params);

        return webClient.get()
                .uri(uriString)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(this::logResponse) // ✅ 응답 바디 로깅
                .block();
    }

    private void logResponse(String responseBody) {
        log.info("🔍 API Response Body: {}", responseBody);
    }

    // ✅ 특정 필드값 추출하는 메서드 추가
    public String getFieldValue(JsonNode rootNode, String... fieldNames) {
        JsonNode node = rootNode;
        for (String field : fieldNames) {
            node = node.path(field);
        }
        return node.asText();
    }

    private String buildUrlString(Pageable pageable, String baseUrl, String apiKey, HashMap<String, String> params) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam(KEY, apiKey)
                .queryParam(TYPE, "json")
                .queryParam(P_INDEX, pageable.getPageNumber() + 1)
                .queryParam(P_SIZE, pageable.getPageSize());
        params.forEach(uriBuilder::queryParam);
        return uriBuilder.build(false).toUriString(); // 인코딩 비활성화
    }
}
