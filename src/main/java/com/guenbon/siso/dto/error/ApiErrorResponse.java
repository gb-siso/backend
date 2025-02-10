package com.guenbon.siso.dto.error;

import com.guenbon.siso.exception.errorCode.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ApiErrorResponse {
    private final String apiErrorDetail = "외부 api 에러 발생";
    private String message;
    private String code;

    public static ApiErrorResponse from(ErrorCode errorCode) {
        return ApiErrorResponse.builder().message(errorCode.getMessage())
                .code(errorCode.getCode())
                .build();
    }
}
