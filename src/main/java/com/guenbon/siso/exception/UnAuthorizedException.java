package com.guenbon.siso.exception;

import com.guenbon.siso.exception.errorCode.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UnAuthorizedException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;

    public UnAuthorizedException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.UNAUTHORIZED;
    }
}
