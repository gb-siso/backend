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
import com.guenbon.siso.exception.errorCode.KakaoApiErrorCode;
import com.guenbon.siso.util.JsonParserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthApiService {

    private final KakaoApiClient kakaoApiClient;
    private final NaverApiClient naverApiClient;
    private final AuthService authService;

    private NaverUserInfo getNaverUserInfo(NaverToken naverToken) {
        String response = naverApiClient.requestUserInfo(naverToken.getAccessToken()).block();
        return processResponse(response, NaverUserInfo.class);
    }

    private NaverToken getNaverToken(String authCode, String state) {
        String response = naverApiClient.requestToken(authCode, state).block();
        return processResponse(response, NaverToken.class);
    }

    private UserInfo getKakaoUserInfo(KakaoToken kakaoToken) {
        String response = kakaoApiClient.requestUserInfo(kakaoToken.getAccessToken()).block();
        return processResponse(response, UserInfo.class);
    }

    private KakaoToken getKakaoToken(String authCode) {
        String response = kakaoApiClient.requestToken(authCode).block();
        return processResponse(response, KakaoToken.class);
    }

    private <T> T processResponse(String response, Class<T> responseType) {
        JsonNode rootNode = JsonParserUtil.parseJson(response);
        if (JsonParserUtil.hasErrorCode(rootNode)) {
            handleErrorResponse(rootNode);
        }
        return JsonParserUtil.parseJson(response, responseType);
    }

    private void handleErrorResponse(JsonNode errorResponse) {
        String errorCode = JsonParserUtil.extractErrorCode(errorResponse);
        log.error("Kakao API Error Code: {}", errorCode);
        throw new ApiException(KakaoApiErrorCode.from(String.valueOf(errorCode)));
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