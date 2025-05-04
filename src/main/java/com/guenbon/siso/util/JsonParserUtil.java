package com.guenbon.siso.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guenbon.siso.dto.bill.BillSummaryDTO;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;

import static com.guenbon.siso.exception.errorCode.CommonErrorCode.JSON_PARSE_ERROR;

@Slf4j
public class JsonParserUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static final String ERROR = "error";

    /**
     * api 응답 String 을 JsonNode 형태로 파싱한다
     * @param response String 자료형 외부 api 응답
     * @return
     */
    public static JsonNode parseJson(final String response) {
        try {
            return objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            throw new CustomException(JSON_PARSE_ERROR);
        }
    }

    /**
     * responseType 클래스 타입으로 String 을 파싱해서 반환한다
     *
     * @param json 파싱할 json
     * @param responseType 반환할 타입
     * @return
     * @param <T>
     */
    public static <T> T parseJson(String json, Class<T> responseType) {
        try {
            JsonNode rootNode = parseJson(json);
            return objectMapper.treeToValue(rootNode, responseType);
        } catch (Exception e) {
            log.error("Failed to map JSON to object : {}", e.getMessage());
            throw new CustomException(CommonErrorCode.JSON_PARSE_ERROR);
        }
    }

    public static boolean hasErrorCode(JsonNode rootNode) {
        return rootNode.has(ERROR);
    }

    public static String extractErrorCode(JsonNode rootNode, String errorCodePath) {
        return rootNode.path(errorCodePath).asText();
    }

    /**
     * 발의안 요약 api 호출 결과를 파싱
     * @param stringResponse 요약 api 호출 결과
     * @return BillSummaryParseResult 파싱 결과 dto. 예외 여부도 담고, 예외 발생시 예외메세지도 담음
     */
    public static BillSummaryDTO parseBillSummary(String stringResponse) throws JsonProcessingException {
            JsonNode root = objectMapper.readTree(stringResponse);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            String[] lines = content.split("\n");
            return BillSummaryDTO.of(lines[0], lines[1], lines[2], lines[3]);
    }
}
