package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum MemberErrorCode implements ErrorCode {
    NOT_EXISTS(HttpStatus.NOT_FOUND, "MBR001", "해당 회원이 존재하지 않음"),
    RANDOM_NICKNAME_GENERATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MBR002", "랜덤 닉네임 생성 실패"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

