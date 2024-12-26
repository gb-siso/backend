package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AESErrorCode implements ErrorCode {
    INVALID_INPUT("잘못된 요청값입니다"),
    INTERNAL_SEVER("서버 내부 에러 발생")
    ;

    private final String message;
}
