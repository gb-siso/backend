package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RatingErrorCode implements ErrorCode {
    DUPLICATED("중복되는 Rating 입니다"),
    ;

    private final String message;
}
