package com.guenbon.siso.util;

import static com.guenbon.siso.exception.errorCode.CommonErrorCode.JSON_PARSE_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guenbon.siso.exception.CustomException;

public class JsonParserUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode parseJson(final String response) {
        try {
            return objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            throw new CustomException(JSON_PARSE_ERROR);
        }
    }
}
