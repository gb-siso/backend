package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CommonErrorCode implements ErrorCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "CMN001", "잘못된 입력 값"),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "CMN002", "입력 값 타입이 올바르지 않음"),
    INVALID_REQUEST_BODY_FORMAT(HttpStatus.BAD_REQUEST, "CMN003", "요청 본문 형식이 올바르지 않음"),
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "CMN004", "JSON 파싱 오류"),
    FAIL_EXTERNAL_ERROR_CODE_MAPPING(HttpStatus.BAD_REQUEST, "CMN005", "외부 에러 코드 매핑 실패"),
    MISSING_COOKIE(HttpStatus.BAD_REQUEST, "CMN006", "요청 시 필수 쿠키 값 없음"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

