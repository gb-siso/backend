package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommonErrorCode implements ErrorCode {
    NULL_VALUE_NOT_ALLOWED("허용되지 않는 NULL 값입니다"),
    INVALID_INPUT_VALUE("유효하지 않은 입력 값입니다"),
    TYPE_MISMATCH("입력 값의 타입이 올바르지 않습니다"),
    ;

    private final String message;
}
