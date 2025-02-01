package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum AESErrorCode implements ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "AES001", "잘못된 AES 암·복호화 요청"),
    INTERNAL_SERVER(HttpStatus.INTERNAL_SERVER_ERROR, "AES002", "AES 암·복호화 중 서버 오류 발생"),
    NULL_VALUE(HttpStatus.BAD_REQUEST, "AES003", "AES 암·복호화 시 null 값"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
