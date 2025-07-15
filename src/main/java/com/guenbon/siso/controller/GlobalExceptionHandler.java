package com.guenbon.siso.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.guenbon.siso.dto.error.ApiErrorResponse;
import com.guenbon.siso.dto.error.ErrorResponse;
import com.guenbon.siso.dto.error.ErrorResponse.ValidationError;
import com.guenbon.siso.exception.ApiException;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import com.guenbon.siso.exception.errorCode.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

import static com.guenbon.siso.exception.errorCode.CommonErrorCode.*;
import static com.guenbon.siso.exception.errorCode.InternalServerErrorCode.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    public static final String TYPE_MISMATCH_ERROR_MESSAGE_FORMAT = "입력값 %s 를 %s 타입으로 변환할 수 없습니다.";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception e, HttpServletRequest request) {
        logStructuredException("UnhandledException", e, request);
        log.error("...", e);
        return handleExceptionInternal(e);
    }

    private ResponseEntity<ErrorResponse> handleExceptionInternal(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(makeErrorResponse(e));
    }

    private ErrorResponse makeErrorResponse(Exception exception) {
        return ErrorResponse.builder().code(INTERNAL_SERVER_ERROR.name()).message(exception.getMessage()).build();
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e, HttpServletRequest request) {
        logStructuredException("CustomException", e, request);
        log.error("...", e);
        return handleExceptionInternal(e);
    }

    private ResponseEntity<ErrorResponse> handleExceptionInternal(CustomException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(makeErrorResponse(e.getErrorCode()));
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode) {
        return ErrorResponse.builder().code(errorCode.getCode()).message(errorCode.getMessage()).build();
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException e, HttpServletRequest request) {
        logStructuredException("ApiException", e, request);
        return handleExceptionInternal(e);
    }

    private ResponseEntity<ApiErrorResponse> handleExceptionInternal(ApiException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ApiErrorResponse.from(e.getErrorCode()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        log.error("[Validation Error] {}: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return handleExceptionInternal(ex, CommonErrorCode.INVALID_INPUT_VALUE);
    }

    private ResponseEntity<Object> handleExceptionInternal(BindException e, ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus()).body(makeErrorResponse(e, errorCode));
    }

    private ErrorResponse makeErrorResponse(BindException e, ErrorCode errorCode) {
        List<ValidationError> validationErrorList = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    if (fieldError.isBindingFailure()) {
                        return ValidationError.of(
                                fieldError, String.format(TYPE_MISMATCH_ERROR_MESSAGE_FORMAT,
                                        fieldError.getRejectedValue(),
                                        fieldError.getField().getClass().getSimpleName())
                        );
                    } else {
                        return ValidationError.from(fieldError);
                    }
                })
                .collect(Collectors.toList());

        return ErrorResponse.builder().code(errorCode.getCode()).message(errorCode.getMessage())
                .errors(validationErrorList).build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        logStructuredException("TypeMismatchException", e, request);
        return handleExceptionInternal(e);
    }

    private ResponseEntity<ErrorResponse> handleExceptionInternal(MethodArgumentTypeMismatchException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(TYPE_MISMATCH.getCode())
                .message(String.format(TYPE_MISMATCH_ERROR_MESSAGE_FORMAT, e.getValue(),
                        e.getRequiredType().getSimpleName()))
                .build();
        return ResponseEntity.status(TYPE_MISMATCH.getHttpStatus()).body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("[InvalidRequestBody] {}: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return handleExceptionInternal(ex);
    }

    private ResponseEntity<Object> handleExceptionInternal(HttpMessageNotReadableException e) {
        if (e.getCause() instanceof InvalidFormatException) {
            return handleInvalidFormatException(e);
        }
        return ResponseEntity.status(INVALID_REQUEST_BODY_FORMAT.getHttpStatus())
                .body(makeErrorResponse(INVALID_REQUEST_BODY_FORMAT));
    }

    private ResponseEntity<Object> handleInvalidFormatException(HttpMessageNotReadableException e) {
        InvalidFormatException cause = (InvalidFormatException) e.getCause();
        String customMessage = String.format(TYPE_MISMATCH_ERROR_MESSAGE_FORMAT, cause.getValue().toString(),
                cause.getTargetType().getSimpleName());
        return ResponseEntity.status(INVALID_REQUEST_BODY_FORMAT.getHttpStatus())
                .body(makeErrorResponse(INVALID_REQUEST_BODY_FORMAT, customMessage));
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
        return ErrorResponse.builder().code(errorCode.getCode()).message(message).build();
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponse> handleMissingCookieException(MissingRequestCookieException e, HttpServletRequest request) {
        logStructuredException("MissingCookie", e, request);
        return ResponseEntity.status(MISSING_COOKIE.getHttpStatus())
                .body(makeErrorResponse(e));
    }

    private static ErrorResponse makeErrorResponse(MissingRequestCookieException e) {
        return ErrorResponse.builder()
                .code(MISSING_COOKIE.getCode())
                .message(String.format("%s %s", MISSING_COOKIE.getMessage(), e.getCookieName())).build();
    }

    private void logStructuredException(String type, Exception e, HttpServletRequest request) {
        log.error("""
                        [EXCEPTION]
                        ├─ Type    : {}
                        ├─ Message : {}
                        ├─ URI     : {}
                        ├─ Method  : {}
                        └─ Member  : {}
                        """,
                type,
                e.getMessage(),
                request.getRequestURI(),
                request.getMethod(),
                MDC.get("memberId") != null ? MDC.get("memberId") : "로그인 정보 X",
                e);
    }
}
