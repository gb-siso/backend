package com.guenbon.siso.util;

import static com.guenbon.siso.exception.errorCode.CommonErrorCode.JSON_PARSE_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonParserUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
            log.error("Failed to map JSON to object", e);
            throw new CustomException(CommonErrorCode.JSON_PARSE_ERROR);
        }
    }

    public static boolean hasErrorCode(JsonNode rootNode) {
        return rootNode.has("code");
    }

    public static int extractErrorCode(JsonNode rootNode) {
        return rootNode.path("code").asInt();
    }
}
