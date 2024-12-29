package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CongressmanErrorCode implements ErrorCode {
    NOT_EXISTS("존재하지 않는 Congressman 입니다"),
    NULL_PARAMETER("허용되지 않는 NULL 파라미터입니다")
    ;

    private final String message;
}
