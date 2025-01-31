package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MemberErrorCode implements ErrorCode {
    NOT_EXISTS("존재하지 않는 Member 입니다"),
    RANDOM_NICKNAME_GENERATE_FAILED("랜덤 닉네임 생성에 실패했습니다"),
    ;

    private final String message;
}
