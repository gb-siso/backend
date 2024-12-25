package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AuthErrorCode implements ErrorCode {
    EXPIRED("token이 만료되었습니다"),
    UNSUPPORTED("지원하지 않는 token입니다"),
    MALFORMED("잘못된 구조의 token입니다"),
    SIGNATURE("데이터가 변조된 token입니다"),
    NOT_EXISTS_IN_DATABASE("DB에 존재하지 않는 refresh token입니다"),
    NOT_SEAL_CREATOR("씰을 만든 회원이 아닙니다"),
    NULL_OR_BLANK_TOKEN("토큰 값이 null 또는 blank입니다"),
    ;

    private final String message;
}
