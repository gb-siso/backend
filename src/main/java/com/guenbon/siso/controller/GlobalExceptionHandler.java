package com.guenbon.siso.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.guenbon.siso.dto.error.ErrorResponse;
import com.guenbon.siso.dto.error.ErrorResponse.ValidationError;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.InternalServerException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import com.guenbon.siso.exception.errorCode.ErrorCode;
import com.guenbon.siso.exception.errorCode.InternalServerErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String INVALID_FORMAT_EXCEPTION_MESSAGE_FORMAT = "값 %s를 %s 타입으로 변환할 수 없습니다.";

    // 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception e) {
        e.printStackTrace();
        return handleExceptionInternal(new InternalServerException(InternalServerErrorCode.INTERNAL_SERVER_ERROR));
    }

    // 커스텀 예외 처리
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

    // MethodArgumentNotValidException 예외 처리 (DTO Validation)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        ex.printStackTrace();
        return handleExceptionInternal(ex, CommonErrorCode.INVALID_INPUT_VALUE);
    }

    private ResponseEntity<Object> handleExceptionInternal(BindException e, ErrorCode errorCode) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(makeErrorResponse(e, errorCode));
    }

    private ErrorResponse makeErrorResponse(BindException e, ErrorCode errorCode) {
        List<ValidationError> validationErrorList = e.getBindingResult().getFieldErrors().stream()
                .map(ErrorResponse.ValidationError::from).collect(Collectors.toList());

        return ErrorResponse.builder().code(errorCode.name()).message(errorCode.getMessage())
                .errors(validationErrorList).build();
    }


    // MethodArgumentTypeMismatchException 예외 처리
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        return handleExceptionInternal(e);
    }

    private ResponseEntity<ErrorResponse> handleExceptionInternal(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(makeErrorResponse(CommonErrorCode.TYPE_MISMATCH));
    }

    // @RequestBody HttpMessageNotReadableException 예외 처리
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex);
    }

    private ResponseEntity<Object> handleExceptionInternal(HttpMessageNotReadableException e) {
        if (e.getCause() instanceof InvalidFormatException) {
            return handleInvalidFormatException(e);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(makeErrorResponse(CommonErrorCode.INVALID_REQUEST_BODY_FORMAT));
    }

    private ResponseEntity<Object> handleInvalidFormatException(HttpMessageNotReadableException e) {
        InvalidFormatException cause = (InvalidFormatException) e.getCause();
        // 상세 메시지 생성
        String targetType = cause.getTargetType().getSimpleName();
        String invalidValue = cause.getValue().toString();
        String customMessage = String.format(INVALID_FORMAT_EXCEPTION_MESSAGE_FORMAT, invalidValue, targetType);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(makeErrorResponse(CommonErrorCode.INVALID_REQUEST_BODY_FORMAT, customMessage));
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
        return ErrorResponse.builder().code(errorCode.name()).message(message).build();
    }
}