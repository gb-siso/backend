package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum AuthErrorCode implements ErrorCode {
    EXPIRED(HttpStatus.UNAUTHORIZED, "ATH001", "토큰이 만료됨"),
    UNSUPPORTED(HttpStatus.BAD_REQUEST, "ATH002", "지원되지 않는 토큰"),
    MALFORMED(HttpStatus.BAD_REQUEST, "ATH003", "토큰 구조가 올바르지 않음"),
    SIGNATURE(HttpStatus.UNAUTHORIZED, "ATH004", "변조된 토큰"),
    NOT_EXISTS_IN_DATABASE(HttpStatus.NOT_FOUND, "ATH005", "데이터베이스에 존재하지 않는 리프레시 토큰"),
    NULL_OR_BLANK_TOKEN(HttpStatus.BAD_REQUEST, "ATH007", "토큰 값이 비어있거나 null"),
    NOT_ADMIN_ROLE(HttpStatus.BAD_REQUEST, "ATH008", "ADMIN 권한만 접근할 수 있습니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "ATH009", "토큰의 권한이 유효하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

