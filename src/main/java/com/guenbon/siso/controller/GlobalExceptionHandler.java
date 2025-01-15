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
    public static final String TYPE_MISMATCH_ERROR_MESSAGE_FORMAT = "입력값 %s 를 %s 타입으로 변환할 수 없습니다.";

    // 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception e) {
        e.printStackTrace();
        return handleExceptionInternal(new InternalServerException(InternalServerErrorCode.INTERNAL_SERVER_ERROR));
    }

    // 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        e.printStackTrace();
        return handleExceptionInternal(e);
    }

    private ResponseEntity<ErrorResponse> handleExceptionInternal(CustomException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(makeErrorResponse(e.getErrorCode()));
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode) {
        return ErrorResponse.builder().code(errorCode.name()).message(errorCode.getMessage()).build();
    }

    // @Vaild 필드 검증 실패 처리
    // @ModelAttribute 자료형 불일치로 바인딩 실패 처리 (isBindingFailure = true)
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
                .map(fieldError -> {
                    if (fieldError.isBindingFailure()) {
                        // 바인딩 실패
                        return ValidationError.of(
                                fieldError, String.format(TYPE_MISMATCH_ERROR_MESSAGE_FORMAT,
                                        fieldError.getRejectedValue(),
                                        fieldError.getField().getClass().getSimpleName())
                        );
                    } else {
                        // Validation 실패
                        return ValidationError.from(fieldError);
                    }
                })
                .collect(Collectors.toList());

        return ErrorResponse.builder().code(errorCode.name()).message(errorCode.getMessage())
                .errors(validationErrorList).build();
    }

    // @PathVariable, @RequestParam 자료형 불일치로 바인딩 실패 처리
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        return handleExceptionInternal(e);
    }

    private ResponseEntity<ErrorResponse> handleExceptionInternal(MethodArgumentTypeMismatchException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(CommonErrorCode.TYPE_MISMATCH.name())
                .message(String.format(TYPE_MISMATCH_ERROR_MESSAGE_FORMAT, e.getValue(),
                        e.getRequiredType().getSimpleName()))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // @RequestBody json 형식 예외 처리
    // @ReqeustBdoy 자료형 불일치로 바인딩 실패 처리
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

    // @RequestBody 바인딩 에러 (타입 불일치)
    private ResponseEntity<Object> handleInvalidFormatException(HttpMessageNotReadableException e) {
        InvalidFormatException cause = (InvalidFormatException) e.getCause();
        // 상세 메시지 생성
        String customMessage = String.format(TYPE_MISMATCH_ERROR_MESSAGE_FORMAT,
                cause.getValue().toString(), cause.getTargetType().getSimpleName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(makeErrorResponse(CommonErrorCode.INVALID_REQUEST_BODY_FORMAT, customMessage));
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
        return ErrorResponse.builder().code(errorCode.name()).message(message).build();
    }
}
