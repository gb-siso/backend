package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum PageableErrorCode implements ErrorCode {
    INVALID_PAGE(HttpStatus.BAD_REQUEST, "PGE001", "page 값은 1 이상이어야 합니다"),
    INVALID_SIZE(HttpStatus.BAD_REQUEST, "PGE002", "size 값은 0 이상이어야 합니다"),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST, "PGE003", "page 또는 size 값이 정수가 아님"),
    NULL_VALUE(HttpStatus.BAD_REQUEST, "PGE004", "page 또는 size 값이 null임"),
    UNSUPPORTED_SORT_PROPERTY(HttpStatus.BAD_REQUEST, "PGE005", "지원되지 않는 정렬 필드"),
    UNSUPPORTED_SORT_DIRECTION(HttpStatus.BAD_REQUEST, "PGE006", "지원되지 않는 정렬 방향"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

