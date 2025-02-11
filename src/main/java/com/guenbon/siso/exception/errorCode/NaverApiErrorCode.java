package com.guenbon.siso.exception.errorCode;

import com.guenbon.siso.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
@Slf4j
public enum NaverApiErrorCode implements ErrorCode {

    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "024", "인증에 실패했습니다."),
    AUTH_HEADER_NOT_EXISTS(HttpStatus.UNAUTHORIZED, "028", "OAuth 인증 헤더(authorization header)가 없습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "403", "호출 권한이 없습니다. API 요청 헤더에 클라이언트 ID와 Secret 값을 정확히 전송했는지 확인해보시길 바랍니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "404", "검색 결과가 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "서버 내부 에러가 발생하였습니다. 포럼에 올려주시면 신속히 조치하겠습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "invalid_request", "파라미터가 잘못되었거나 요청문이 잘못되었습니다."),
    UNAUTHORIZED_CLIENT(HttpStatus.UNAUTHORIZED, "unauthorized_client", "인증받지 않은 인증 코드(authorization code)로 요청했습니다."),
    UNSUPPORTED_RESPONSE_TYPE(HttpStatus.BAD_REQUEST, "unsupported_response_type", "정의되지 않은 반환 형식으로 요청했습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "server_error", "네이버 인증 서버의 오류로 요청을 처리하지 못했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public static NaverApiErrorCode from(String code) {
        for (NaverApiErrorCode naverApiErrorCode : values()) {
            if (naverApiErrorCode.getCode().equals(code)) {
                return naverApiErrorCode;
            }
        }
        throw new CustomException(CommonErrorCode.FAIL_EXTERNAL_ERROR_CODE_MAPPING);
    }
}
