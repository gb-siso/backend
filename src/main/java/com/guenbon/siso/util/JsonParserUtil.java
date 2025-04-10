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

    public static JsonNode parseJson(final String response) {
        try {
            return objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            throw new CustomException(JSON_PARSE_ERROR);
        }
    }

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

    public static BillSummaryDTO parseBillSummary(String stringResponse) throws JsonProcessingException {
        final JsonNode root = objectMapper.readTree(stringResponse);
        final String content = root
                .path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();
        return parseBillSummaryFromContent(content);
    }

    public static BillSummaryDTO parseBillSummaryFromContent(final String content) {
        final String[] lines = content.split("\n");
        return new BillSummaryDTO(lines[0], lines[1], lines[2], lines[3]);
    }
}
