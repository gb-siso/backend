package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PageableErrorCode implements ErrorCode {
    INVALID_PAGE("page 값은 0 이상이어야 합니다."),
    INVALID_SIZE("size 값은 0 이상이어야 합니다."),
    INVALID_FORMAT("page 또는 size 값이 정수가 아닙니다."),
    NULL_VALUE("page 또는 size 값이 null입니다."),
    UNSUPPORTED_SORT_PROPERTY("지원하지 않는 정렬 필드입니다."),
    UNSUPPORTED_SORT_DIRECTION("지원하지 않는 정렬 방향입니다."),
    ;

    private final String message;
}
