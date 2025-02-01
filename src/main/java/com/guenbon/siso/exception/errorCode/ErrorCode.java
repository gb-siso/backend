package com.guenbon.siso.exception.errorCode;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String name();

    String getMessage();

    HttpStatus getHttpStatus();

    String getCode();
}
