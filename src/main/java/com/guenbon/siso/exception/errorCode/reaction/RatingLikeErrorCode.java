package com.guenbon.siso.exception.errorCode.reaction;

import com.guenbon.siso.exception.errorCode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum RatingLikeErrorCode implements ErrorCode {
    DUPLICATED(HttpStatus.BAD_REQUEST, "RLE001", "이미 좋아요 한 평가에 대한 중복 좋아요 요청입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
