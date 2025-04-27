package com.guenbon.siso.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.guenbon.siso.client.KakaoApiClient;
import com.guenbon.siso.client.NaverApiClient;
import com.guenbon.siso.dto.auth.IssueTokenResult;
import com.guenbon.siso.dto.auth.kakao.KakaoToken;
import com.guenbon.siso.dto.auth.kakao.UserInfo;
import com.guenbon.siso.dto.auth.naver.NaverToken;
import com.guenbon.siso.dto.auth.naver.NaverUserInfo;
import com.guenbon.siso.exception.ApiException;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import com.guenbon.siso.exception.errorCode.ErrorCode;
import com.guenbon.siso.exception.errorCode.KakaoApiErrorCode;
import com.guenbon.siso.exception.errorCode.NaverApiErrorCode;
import com.guenbon.siso.util.JsonParserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthApiService {

    public static final String KAKAO_ERROR_PATH = "error_code";
    public static final String NAVER_ERROR_PATH = "errorCode";

    private final KakaoApiClient kakaoApiClient;
    private final NaverApiClient naverApiClient;
    private final AuthService authService;

    private NaverUserInfo getNaverUserInfo(NaverToken naverToken) {
        String response = naverApiClient.requestUserInfo(naverToken.getAccessToken()).block();
        return processResponse(response, NAVER_ERROR_PATH, NaverApiErrorCode.class, NaverUserInfo.class);
    }

    private NaverToken getNaverToken(String authCode, String state) {
        String response = naverApiClient.requestToken(authCode, state).block();
        return processResponse(response, NAVER_ERROR_PATH, NaverApiErrorCode.class, NaverToken.class);
    }

    private UserInfo getKakaoUserInfo(KakaoToken kakaoToken) {
        String response = kakaoApiClient.requestUserInfo(kakaoToken.getAccessToken()).block();
        return processResponse(response, KAKAO_ERROR_PATH, KakaoApiErrorCode.class, UserInfo.class);
    }

    private KakaoToken getKakaoToken(String authCode) {
        String response = kakaoApiClient.requestToken(authCode).block();
        return processResponse(response, KAKAO_ERROR_PATH, KakaoApiErrorCode.class, KakaoToken.class);
    }

    /**
     * 외부 api 요청 결과 에러 여부를 확인해 예외처리하거나 responseType으로 파싱한다.
     *
     * @param response 외부 api 요청 결과
     * @param errorCodePath 외부 api 예외처리 json path (에러코드 찾기 위함)
     * @param errorCodeEnum 외부 api 에러코드 매핑한 enum class
     * @param responseType 정상 응답일 경우 파싱해서 반환할 타입
     * @return
     * @param <T>
     * @param <E>
     */
    private <T, E extends Enum<E> & ErrorCode> T processResponse(
            String response,
            String errorCodePath,
            Class<E> errorCodeEnum, // ✅ 수정된 부분
            Class<T> responseType
    ) {
        JsonNode rootNode = JsonParserUtil.parseJson(response);
        if (JsonParserUtil.hasErrorCode(rootNode)) {
            handleErrorResponse(rootNode, errorCodePath, errorCodeEnum);
        }
        return JsonParserUtil.parseJson(response, responseType);
    }

    private <T extends Enum<T> & ErrorCode> void handleErrorResponse(
            JsonNode errorResponse,
            String errorCodePath,
            Class<T> errorCodeEnum
    ) {
        String errorCode = JsonParserUtil.extractErrorCode(errorResponse, errorCodePath);
        log.error("API Error Code: {}", errorCode);

        try {
            Method fromMethod = errorCodeEnum.getMethod("from", String.class);
            ErrorCode errorEnum = (ErrorCode) fromMethod.invoke(null, errorCode);
            throw new ApiException(errorEnum);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new CustomException(CommonErrorCode.FAIL_EXTERNAL_ERROR_CODE_MAPPING);
        }
    }

    public IssueTokenResult authenticateWithKakao(String code) {
        KakaoToken token = getKakaoToken(code);
        UserInfo userInfo = getKakaoUserInfo(token);
        return authService.issueTokenWithKakaoId(userInfo.getId());
    }

    public IssueTokenResult authenticateWithNaver(String code, String state) {
        NaverToken naverToken = getNaverToken(code, state);
        NaverUserInfo naverUserInfo = getNaverUserInfo(naverToken);
        return authService.issueTokenWithNaverId(naverUserInfo.getResponse().getId());
    }
}