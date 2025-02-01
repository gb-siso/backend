package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CongressmanErrorCode implements ErrorCode {
    NOT_EXISTS(HttpStatus.NOT_FOUND, "CMG001", "해당 국회의원이 존재하지 않움"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

