package com.guenbon.siso.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.guenbon.siso.client.KakaoApiClient;
import com.guenbon.siso.dto.auth.IssueTokenResult;
import com.guenbon.siso.dto.auth.kakao.KakaoToken;
import com.guenbon.siso.dto.auth.kakao.UserInfo;
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
    private final AuthService authService;

    private UserInfo getUserInfo(KakaoToken kakaoToken) {
        String response = kakaoApiClient.requestUserInfo(kakaoToken.getAccessToken()).block();
        return processResponse(response, UserInfo.class);
    }

    private KakaoToken getToken(String authCode) {
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
        KakaoToken token = getToken(code);
        UserInfo userInfo = getUserInfo(token);
        return authService.issueToken(userInfo.getId());
    }
}