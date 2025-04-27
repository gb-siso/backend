package com.guenbon.siso.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.guenbon.siso.exception.ApiException;
import com.guenbon.siso.exception.errorCode.CongressApiErrorCode;
import com.guenbon.siso.util.JsonParserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static com.guenbon.siso.exception.errorCode.CongressApiErrorCode.NO_DATA_FOUND;
import static com.guenbon.siso.support.constants.ApiConstants.*;

@Slf4j
@Component
public class CongressApiClient {

    public static final int PAGE_MAX_SIZE = 1000;
    @Value("${api.bill.key}")
    private String billApikey;

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

//    @Transactional(propagation = Propagation.NEVER)
//    public BillSummaryDTO getBillSummaryResponse(final String baseUrl, final String apiKey, final String userContent) throws JsonProcessingException {
//        String stringResponse = webClient.post()
//                .uri(baseUrl)
//                .header("Authorization", "Bearer " + apiKey)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(Map.of(
//                        "model", "sonar",
//                        "messages", List.of(
//                                Map.of("role", "system", "content", PromptBuilder.getBillSummaryPrompt()),
//                                Map.of("role", "user", "content", userContent)
//                        ),
//                        "max_tokens", 500
//                ))
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();// 응답 JSON을 문자열로 받음
//        return JsonParserUtil.parseBillSummary(stringResponse);
//    }

    private void logResponse(final String responseBody) {
        log.info("API Response Body: {}", responseBody);
    }

    // 특정 필드값 추출하는 메서드
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

    /**
     * 국회 api에 22대 발의안 목록 page에 해당하는 데이터를 요청함
     *
     * @param page
     * @return
     */
    @Transactional(propagation = Propagation.NEVER)
    public JsonNode getBillResponse(int page) {

        log.info("## getBillResponse 메서드 page : {}", page);

        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(API_BILL_URL)
                .queryParam(AGE, "22") // 22 대만
                .queryParam(KEY, billApikey)
                .queryParam(TYPE, "json")
                .queryParam(P_INDEX, page)
                .queryParam(P_SIZE, PAGE_MAX_SIZE);

        String uriString = uriBuilder.build(false).toUriString();

        String response = webClient.get()
                .uri(uriString)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return JsonParserUtil.parseJson(response);
    }

    public boolean isLastPage(JsonNode jsonNode) {
        return NO_DATA_FOUND.equals(CongressApiErrorCode.from(getFieldValue(jsonNode, RESULT, CODE).split("-")[1]));
    }

    public boolean isApiResponseError(final JsonNode rootNode) {
        return getFieldValue(rootNode, RESULT, CODE).contains("-");
    }

    public void handleApiError(final JsonNode rootNode) {
        String errorCode = getFieldValue(rootNode, RESULT, CODE).split("-")[1];
        throw new ApiException(CongressApiErrorCode.from(errorCode));
    }

    public JsonNode getContent(final JsonNode jsonNode, final String apiPath) {
        return jsonNode.path(apiPath).get(1).path("row");
    }
}
