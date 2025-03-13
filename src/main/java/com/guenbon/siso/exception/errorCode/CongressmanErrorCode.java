package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CongressmanErrorCode implements ErrorCode {
    NOT_EXISTS(HttpStatus.NOT_FOUND, "CMG001", "해당 국회의원이 존재하지 않움"),
    API_CALL_LIMIT_EXCEEDED(HttpStatus.INTERNAL_SERVER_ERROR, "CMG002", "국회의원 정보 얻기 api 요청 시 요청 횟수 제한 초과"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

