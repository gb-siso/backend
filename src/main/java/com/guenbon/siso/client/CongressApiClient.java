package com.guenbon.siso.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.guenbon.siso.dto.bill.BillSummaryDTO;
import com.guenbon.siso.util.JsonParserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

import static com.guenbon.siso.support.constants.ApiConstants.*;

@Slf4j
@Component
public class CongressApiClient {
    private static final WebClient webClient = WebClient.builder()
            .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB로 증가
                    .build())
            .build();

    @Transactional(propagation = Propagation.NEVER)
    public String getApiResponse(final Pageable pageable, final String baseUrl, final String apiKey, final Map<String, String> params) {
        final String uriString = buildUrlString(pageable, baseUrl, apiKey, params);

        return webClient.get()
                .uri(uriString)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Transactional(propagation = Propagation.NEVER)
    public BillSummaryDTO getBillSummaryResponse(final String baseUrl, final String apiKey, final String userContent) throws JsonProcessingException {
        String stringResponse = webClient.post()
                .uri(baseUrl)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "model", "sonar",
                        "messages", List.of(
                                Map.of("role", "system", "content", "Please summarize the following Korean legislative proposal in the following format.\n" +
                                        "Respond in Korean only, without field names or labels.\n" +
                                        "Use line breaks to separate the following 4 items in this exact order:\n" +
                                        "1. Category of the bill (e.g., 환경, 교육, 복지, etc.)\n" +
                                        "2. A brief one-line summary of the bill's content (ending with '이다.')\n" +
                                        "3. A one-line explanation of the reason behind the proposal (ending with '되었다.' or '위해 발의되었다.')\n" +
                                        "4. A one-line summary of the expected effect or impact (ending with '기대된다.')\n" +
                                        "\n" +
                                        "Return only these 4 lines, without labels or extra explanations. Keep Korean characters intact in UTF-8.\n"
                                ),
                                Map.of("role", "user", "content", userContent)
                        ),
                        "max_tokens", 500
                ))
                .retrieve()
                .bodyToMono(String.class)
                .block();// 응답 JSON을 문자열로 받음
        return JsonParserUtil.parseBillSummary(stringResponse);
    }

    private void logResponse(final String responseBody) {
        log.info("API Response Body: {}", responseBody);
    }

    // ✅ 특정 필드값 추출하는 메서드
    public String getFieldValue(final JsonNode rootNode, final String... fieldNames) {
        JsonNode node = rootNode;
        for (final String field : fieldNames) {
            node = node.path(field);
        }
        return node.asText();
    }

    private String buildUrlString(final Pageable pageable, final String baseUrl, final String apiKey, final Map<String, String> params) {
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam(KEY, apiKey)
                .queryParam(TYPE, "json")
                .queryParam(P_INDEX, pageable.getPageNumber() + 1)
                .queryParam(P_SIZE, pageable.getPageSize());

        if (params != null) {
            params.forEach(uriBuilder::queryParam);
        }

        return uriBuilder.build(false).toUriString(); // 인코딩 비활성화
    }
}
