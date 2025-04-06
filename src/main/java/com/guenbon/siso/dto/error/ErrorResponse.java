package com.guenbon.siso.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.FieldError;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class ErrorResponse {
    private String message;
    private String code;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<ValidationError> errors;

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class ValidationError {

        private final String field;
        private final String message;

        public static ValidationError from(final FieldError fieldError) {
            return ValidationError.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
        }

        public static ValidationError of(final FieldError fieldError, String message) {
            return ValidationError.builder()
                    .field(fieldError.getField())
                    .message(message)
                    .build();
        }
    }
}
