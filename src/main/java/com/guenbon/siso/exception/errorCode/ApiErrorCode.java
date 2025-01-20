package com.guenbon.siso.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ApiErrorCode implements ErrorCode {

    MISSING_REQUIRED_VALUE("필수 값이 누락되어 있습니다. 요청인자를 참고 하십시오.", "300"),
    INVALID_API_KEY("인증키가 유효하지 않습니다. 인증키가 없는 경우, 홈페이지에서 인증키를 신청하십시오.", "290"),
    TRAFFIC_LIMIT_EXCEEDED("일별 트래픽 제한을 넘은 호출입니다. 오늘은 더이상 호출할 수 없습니다.", "337"),
    SERVICE_NOT_FOUND("해당하는 서비스를 찾을 수 없습니다. 요청인자 중 SERVICE를 확인하십시오.", "310"),
    INVALID_REQUEST_TYPE("요청위치 값의 타입이 유효하지 않습니다. 요청위치 값은 정수를 입력하세요.", "333"),
    MAX_REQUEST_LIMIT_EXCEEDED("데이터요청은 한번에 최대 1,000건을 넘을 수 없습니다.", "336"),
    SERVER_ERROR("서버 오류입니다. 지속적으로 발생시 홈페이지로 문의(Q&A) 바랍니다.", "500"),
    DATABASE_CONNECTION_ERROR("데이터베이스 연결 오류입니다. 지속적으로 발생시 홈페이지로 문의(Q&A) 바랍니다.", "600"),
    SQL_ERROR("SQL 문장 오류 입니다. 지속적으로 발생시 홈페이지로 문의(Q&A) 바랍니다.", "601"),
    CERTIFICATE_REVOKED("인증서가 폐기되었습니다. 홈페이지에서 인증키를 확인하십시오.", "990"),
    API_KEY_RESTRICTED("관리자에 의해 인증키 사용이 제한되었습니다.", "300"),
    NO_DATA_FOUND("해당하는 데이터가 없습니다.", "200");

    private final String message;
    private final String apiErrorCode;

    public static ApiErrorCode from(String errorCode) {
        for (ApiErrorCode apiErrorCode : values()) {
            if (apiErrorCode.getApiErrorCode().equals(errorCode)) {
                return apiErrorCode;
            }
        }
        // TODO : 예외 타입 수정 필요
        throw new IllegalArgumentException("알 수 없는 오류 코드: " + errorCode);
    }
}

