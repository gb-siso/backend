package com.guenbon.siso.exception;

import com.guenbon.siso.exception.errorCode.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends CustomException {
    public ApiException(final ErrorCode errorCode) {
        super(errorCode, HttpStatus.BAD_REQUEST);
    }
}
