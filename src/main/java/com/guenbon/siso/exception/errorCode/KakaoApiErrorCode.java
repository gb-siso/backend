package com.guenbon.siso.exception.errorCode;

import com.guenbon.siso.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
@Slf4j
public enum KakaoApiErrorCode implements ErrorCode {

    KOE001(HttpStatus.BAD_REQUEST, "KOE001", "잘못된 형식의 요청. 요청 파라미터 확인 후 재요청."),
    KOE002(HttpStatus.BAD_REQUEST, "KOE002", "잘못된 URL로 요청. 요청한 URL 확인 후 재요청."),
    KOE003(HttpStatus.INTERNAL_SERVER_ERROR, "KOE003", "카카오 OAuth 서버의 일시적인 오류. 잠시 후 재요청."),
    KOE004(HttpStatus.FORBIDDEN, "KOE004", "카카오 로그인이 활성화되지 않음. 카카오 로그인 기능을 활성화해야 함."),
    KOE005(HttpStatus.FORBIDDEN, "KOE005", "테스트 앱에서 팀원으로 등록되지 않은 사용자가 로그인 시도."),
    KOE006(HttpStatus.BAD_REQUEST, "KOE006", "등록되지 않은 Redirect URI 사용. 올바른 Redirect URI를 등록 후 사용."),
    KOE007(HttpStatus.BAD_REQUEST, "KOE007", "등록되지 않은 Logout Redirect URI 사용. 올바른 Logout Redirect URI를 등록 후 사용."),
    KOE008(HttpStatus.BAD_REQUEST, "KOE008", "잘못된 앱 타입으로 인가 코드 요청. 올바른 앱 키 사용 필요."),
    KOE009(HttpStatus.BAD_REQUEST, "KOE009", "등록되지 않은 플랫폼에서 액세스 토큰 요청."),
    KOE010(HttpStatus.UNAUTHORIZED, "KOE010", "클라이언트 시크릿이 누락되었거나 잘못된 값 전달."),
    KOE101(HttpStatus.UNAUTHORIZED, "KOE101", "잘못된 앱 키 사용. 올바른 앱 키 확인 후 사용."),
    KOE102(HttpStatus.UNAUTHORIZED, "KOE102", "카카오톡 채널과 연결되지 않은 앱에서 인가 코드 요청."),
    KOE201(HttpStatus.BAD_REQUEST, "KOE201", "지원하지 않는 응답 유형 사용."),
    KOE202(HttpStatus.BAD_REQUEST, "KOE202", "response_type이 잘못 설정됨. 'code' 값으로 변경 필요."),
    KOE203(HttpStatus.FORBIDDEN, "KOE203", "필수 동의 항목을 동의하지 않음."),
    KOE204(HttpStatus.BAD_REQUEST, "KOE204", "유효하지 않은 파라미터 포함."),
    KOE205(HttpStatus.FORBIDDEN, "KOE205", "설정하지 않은 동의항목을 포함한 요청."),
    KOE207(HttpStatus.BAD_REQUEST, "KOE207", "필수 파라미터가 누락됨."),
    KOE303(HttpStatus.BAD_REQUEST, "KOE303", "인가 코드 요청과 액세스 토큰 요청 시 사용한 Redirect URI 불일치."),
    KOE310(HttpStatus.BAD_REQUEST, "KOE310", "지원되지 않는 grant_type 사용."),
    KOE319(HttpStatus.UNAUTHORIZED, "KOE319", "토큰 갱신 요청 시 리프레시 토큰이 누락됨."),
    KOE320(HttpStatus.UNAUTHORIZED, "KOE320", "이미 사용했거나 만료된 인가 코드 사용."),
    KOE322(HttpStatus.UNAUTHORIZED, "KOE322", "유효하지 않거나 만료된 리프레시 토큰 사용."),
    KOE406(HttpStatus.UNAUTHORIZED, "KOE406", "14세 미만 사용자가 14세 미만 미허용 앱을 호출."),
    KOE501(HttpStatus.BAD_REQUEST, "KOE501", "카카오톡 미가입 또는 유예 사용자 요청."),
    KOE602(HttpStatus.BAD_REQUEST, "KOE602", "이미지 업로드 시 최대 용량 초과."),
    KOE903(HttpStatus.UNAUTHORIZED, "KOE903", "등록되지 않은 앱 키 사용."),
    KOE911(HttpStatus.BAD_REQUEST, "KOE911", "지원하지 않는 이미지 포맷 업로드."),
    KOE9798(HttpStatus.SERVICE_UNAVAILABLE, "KOE9798", "서비스 점검 중.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public static KakaoApiErrorCode from(String code) {
        for (KakaoApiErrorCode kakaoApiErrorCode : values()) {
            if (kakaoApiErrorCode.getCode().equals(code)) {
                return kakaoApiErrorCode;
            }
        }
        throw new CustomException(CommonErrorCode.FAIL_EXTERNAL_ERROR_CODE_MAPPING);
    }
}
