package com.guenbon.siso.exception.errorCode.reaction;

import com.guenbon.siso.exception.errorCode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CongressmanDisLikeErrorCode implements ErrorCode {
    DUPLICATED(HttpStatus.BAD_REQUEST, "CDE001", "이미 싫어요 한 국회의원에 대한 중복 싫어요 요청입니다."),
    NOT_DISLIKED(HttpStatus.BAD_REQUEST, "CDE002", "싫어요를 누르지 않은 상태에서는 싫어요 해제 요청을 할 수 없습니다."),
    NOT_MY_DISLIKE(HttpStatus.BAD_REQUEST, "CDE003", "본인이 누른 게 아닌 싫어요에 대해 싫어요 해제 요청을 할 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
