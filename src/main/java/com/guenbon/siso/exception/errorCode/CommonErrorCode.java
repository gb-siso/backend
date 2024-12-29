package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommonErrorCode implements ErrorCode {
    NULL_VALUE_NOT_ALLOWED("허용되지 않는 NULL 값입니다")
    ;

    private final String message;
}
