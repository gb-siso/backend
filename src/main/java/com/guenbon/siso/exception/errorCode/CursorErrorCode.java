package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CursorErrorCode implements ErrorCode {
    NULL_OR_EMPTY_VALUE(HttpStatus.BAD_REQUEST, "CSR001", "커서 값이 null이거나 비어 있음"),
    NEGATIVE_VALUE(HttpStatus.BAD_REQUEST, "CSR002", "커서 값은 음수일 수 없음"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
