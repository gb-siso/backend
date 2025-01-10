package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CursorErrorCode implements ErrorCode {
    NULL_OR_EMPTY_VALUE("커서 값이 null이거나 비어있습니다"),
    NEGATIVE_VALUE("커서 값이 음수입니다"),
    ;

    private final String message;
}