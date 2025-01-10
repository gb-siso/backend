package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum InternalServerErrorCode implements ErrorCode {

    INTERNAL_SERVER_ERROR("서버 내부 에러"),
    ;

    private final String message;
}