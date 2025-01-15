package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AESErrorCode implements ErrorCode {
    INVALID_INPUT("AES 암,복호화 시 잘못된 요청값입니다"),
    INTERNAL_SEVER("AES 암,복호화 시 서버 내부 에러 발생"),
    NULL_VALUE("AES 암,복호화 시 부적절한 NULL 값 입력"),
    ;

    private final String message;
}
