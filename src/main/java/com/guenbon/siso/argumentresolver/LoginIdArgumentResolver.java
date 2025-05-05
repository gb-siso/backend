package com.guenbon.siso.argumentresolver;

import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.AuthErrorCode;
import com.guenbon.siso.service.auth.JwtTokenProvider;
import com.guenbon.siso.support.annotation.LoginId;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginIdArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String ACCESS_TOKEN = "accessToken";
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginId.class) && Long.class.isAssignableFrom(
                parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        final LoginId loginId = parameter.getParameterAnnotation(LoginId.class);
        final boolean required = loginId.required();

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String accessToken = request.getHeader(ACCESS_TOKEN);

        if (accessToken == null || accessToken.isBlank()) {
            if (required) {
                throw new CustomException(AuthErrorCode.NULL_OR_BLANK_TOKEN);
            }
            return null;
        }

        final Long memberId = jwtTokenProvider.getMemberId(accessToken);
        MDC.put("memberId", String.valueOf(memberId)); // ✅ MDC에 memberId 저장
        return memberId;
    }
}
