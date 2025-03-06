package com.guenbon.siso.exception.errorCode.reaction;

import com.guenbon.siso.exception.errorCode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum RatingLikeErrorCode implements ErrorCode {
    DUPLICATED(HttpStatus.BAD_REQUEST, "RLE001", "이미 좋아요 한 평가에 대한 중복 좋아요 요청입니다."),
    NOT_LIKED(HttpStatus.BAD_REQUEST, "RLE002", "좋아요를 누르지 않은 상태에서는 좋아요 해제 요청을 할 수 없습니다."),
    NOT_MY_LIKE(HttpStatus.BAD_REQUEST, "RLE003", "본인이 누른 게 아닌 좋아요에 대해 좋아요 해제 요청을 할 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
