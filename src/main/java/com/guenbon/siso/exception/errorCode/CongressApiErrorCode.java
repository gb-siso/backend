package com.guenbon.siso.exception.errorCode;

import com.guenbon.siso.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 국회 api 에러코드와 연동
 */
@RequiredArgsConstructor
@Getter
public enum CongressApiErrorCode implements ErrorCode {

    MISSING_REQUIRED_VALUE(HttpStatus.BAD_REQUEST, "300", "필수 값이 누락되어 있습니다. 요청인자를 참고 하십시오."),
    INVALID_CAP_KEY(HttpStatus.UNAUTHORIZED, "290", "인증키가 유효하지 않습니다. 인증키가 없는 경우, 홈페이지에서 인증키를 신청하십시오."),
    TRAFFIC_LIMIT_EXCEEDED(HttpStatus.FORBIDDEN, "337", "일별 트래픽 제한을 넘은 호출입니다. 오늘은 더이상 호출할 수 없습니다."),
    SERVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "310", "해당하는 서비스를 찾을 수 없습니다. 요청인자 중 SERVICE를 확인하십시오."),
    INVALID_REQUEST_TYPE(HttpStatus.BAD_REQUEST, "333", "요청위치 값의 타입이 유효하지 않습니다. 요청위치 값은 정수를 입력하세요."),
    MAX_REQUEST_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "336", "데이터요청은 한번에 최대 1,000건을 넘을 수 없습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "서버 오류입니다. 지속적으로 발생시 홈페이지로 문의(Q&A) 바랍니다."),
    DATABASE_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "600",
            "데이터베이스 연결 오류입니다. 지속적으로 발생시 홈페이지로 문의(Q&A) 바랍니다."),
    SQL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "601", "SQL 문장 오류 입니다. 지속적으로 발생시 홈페이지로 문의(Q&A) 바랍니다."),
    CERTIFICATE_REVOKED(HttpStatus.UNAUTHORIZED, "990", "인증서가 폐기되었습니다. 홈페이지에서 인증키를 확인하십시오."),
    CAP_KEY_RESTRICTED(HttpStatus.FORBIDDEN, "300", "관리자에 의해 인증키 사용이 제한되었습니다."),
    NO_DATA_FOUND(HttpStatus.NOT_FOUND, "200", "해당하는 데이터가 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public static CongressApiErrorCode from(String code) {
        for (CongressApiErrorCode congressApiErrorCode : values()) {
            if (congressApiErrorCode.getCode().equals(code)) {
                return congressApiErrorCode;
            }
        }
        throw new CustomException(CommonErrorCode.INVALID_ERROR_CODE);
    }
}

