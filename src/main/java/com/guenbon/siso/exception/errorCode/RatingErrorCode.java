package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum RatingErrorCode implements ErrorCode {
    DUPLICATED(HttpStatus.CONFLICT, "RTG001", "중복된 Rating"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
