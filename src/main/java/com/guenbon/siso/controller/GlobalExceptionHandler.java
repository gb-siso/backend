package com.guenbon.siso.controller;

import com.guenbon.siso.dto.error.ErrorResponse;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        return handleExceptionInternal(e);
    }

    private ResponseEntity<ErrorResponse> handleExceptionInternal(CustomException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(makeErrorResponse(e.getErrorCode()));
    }
    private ErrorResponse makeErrorResponse(ErrorCode errorCode) {
        return ErrorResponse.builder().code(errorCode.name()).message(errorCode.getMessage()).build();
    }
}
