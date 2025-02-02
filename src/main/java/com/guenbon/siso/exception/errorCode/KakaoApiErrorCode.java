package com.guenbon.siso.exception.errorCode;

import com.guenbon.siso.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum KakaoApiErrorCode implements ErrorCode {

    SERVER_ERROR(HttpStatus.BAD_REQUEST, "-1", "서버 내부에서 처리 중에 에러가 발생한 경우"),
    MISSING_OR_INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "-2",
            "필수 인자가 포함되지 않은 경우나 호출 인자값의 데이터 타입이 적절하지 않거나 허용된 범위를 벗어난 경우"),
    FEATURE_NOT_ENABLED(HttpStatus.FORBIDDEN, "-3", "해당 API를 사용하기 위해 필요한 기능(간편가입, 동의항목, 서비스 설정 등)이 활성화되지 않은 경우"),
    ACCOUNT_RESTRICTED(HttpStatus.FORBIDDEN, "-4", "계정이 제재된 경우나 해당 계정에 제재된 행동을 하는 경우"),
    INSUFFICIENT_PERMISSIONS(HttpStatus.FORBIDDEN, "-5", "해당 API에 대한 요청 권한이 없는 경우"),
    SERVICE_MAINTENANCE(HttpStatus.BAD_REQUEST, "-7", "서비스 점검 또는 내부 문제가 있는 경우"),
    INVALID_HEADER(HttpStatus.BAD_REQUEST, "-8", "올바르지 않은 헤더로 요청한 경우"),
    API_DEPRECATED(HttpStatus.BAD_REQUEST, "-9", "서비스가 종료된 API를 호출한 경우"),
    QUOTA_EXCEEDED(HttpStatus.BAD_REQUEST, "-10", "허용된 요청 회수를 초과한 경우"),
    INVALID_APP_KEY_OR_TOKEN(HttpStatus.UNAUTHORIZED, "-401", "유효하지 않은 앱키나 액세스 토큰으로 요청한 경우"),
    KAKAOTALK_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "-501", "카카오톡 미가입 또는 유예 사용자가 카카오톡 또는 톡캘린더 API를 호출한 경우"),
    IMAGE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "-602", "이미지 업로드 시 최대 용량을 초과한 경우"),
    REQUEST_TIMEOUT(HttpStatus.BAD_REQUEST, "-603", "카카오 플랫폼 내부에서 요청 처리 중 타임아웃이 발생한 경우"),
    MAX_IMAGE_COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "-606", "업로드할 수 있는 최대 이미지 개수를 초과한 경우"),
    UNREGISTERED_APP_KEY(HttpStatus.BAD_REQUEST, "-903", "등록되지 않은 개발자의 앱키나 등록되지 않은 개발자의 앱키로 구성된 액세스 토큰으로 요청한 경우"),
    UNSUPPORTED_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "-911", "지원하지 않는 포맷의 이미지를 업로드 하는 경우"),
    SERVICE_UNDER_MAINTENANCE(HttpStatus.SERVICE_UNAVAILABLE, "-9798", "서비스 점검 중"),
    ACCOUNT_NOT_CONNECTED(HttpStatus.BAD_REQUEST, "-101", "해당 앱에 카카오계정 연결이 완료되지 않은 사용자가 호출한 경우"),
    DUPLICATE_CONNECTION_REQUEST(HttpStatus.BAD_REQUEST, "-102", "이미 앱과 연결되어 있는 사용자의 토큰으로 연결하기 요청한 경우"),
    INACTIVE_OR_NONEXISTENT_ACCOUNT(HttpStatus.BAD_REQUEST, "-103", "휴면 상태, 또는 존재하지 않는 카카오계정으로 요청한 경우"),
    INVALID_USER_PROPERTY(HttpStatus.BAD_REQUEST, "-201",
            "사용자 정보 요청 API나 사용자 정보 저장 API 호출 시 앱에 추가하지 않은 사용자 프로퍼티 키 값을 불러오거나 저장하려고 한 경우"),
    CONSENT_REQUIRED(HttpStatus.FORBIDDEN, "-402", "해당 API에서 접근하는 리소스에 대해 사용자의 동의를 받지 않은 경우"),
    AGE_RESTRICTION(HttpStatus.UNAUTHORIZED, "-406", "14세 미만 미허용 설정이 되어 있는 앱으로 14세 미만 사용자가 API 호출한 경우");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public static KakaoApiErrorCode from(String code) {
        for (KakaoApiErrorCode kakaoApiErrorCode : values()) {
            if (kakaoApiErrorCode.getCode().equals(code)) {
                return kakaoApiErrorCode;
            }
        }
        throw new CustomException(CommonErrorCode.INVALID_ERROR_CODE);
    }
}
