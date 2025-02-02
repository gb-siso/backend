package com.guenbon.siso.interceptor;

import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.AuthErrorCode;
import com.guenbon.siso.service.JwtTokenProvider;
import com.guenbon.siso.support.annotation.Login;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
@RequiredArgsConstructor
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    public static final String ACCESS_TOKEN = "accessToken";
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Login loginAnnotation = handlerMethod.getMethodAnnotation(Login.class);

        if (loginAnnotation == null) {
            return true;
        }

        // 로그인해야 하는 메서드에 대해 jwt 검증
        // 헤더는 Authorization: <type> <credentials> 형태이므로 토큰을 얻기 위해 split
        String accessToken = getAccessTokenFromRequest(request);
        jwtTokenProvider.verifySignature(accessToken);
        return true;
    }

    private String getAccessTokenFromRequest(HttpServletRequest request) {
        String accessToken = request.getHeader(ACCESS_TOKEN);
        if (accessToken == null || accessToken.isBlank()) {
            throw new CustomException(AuthErrorCode.NULL_OR_BLANK_TOKEN);
        }
        return accessToken;
    }
}
