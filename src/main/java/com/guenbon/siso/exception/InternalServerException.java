package com.guenbon.siso.exception;

import com.guenbon.siso.exception.errorCode.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InternalServerException extends CustomException {
    public InternalServerException(final ErrorCode errorCode) {
        super(errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
