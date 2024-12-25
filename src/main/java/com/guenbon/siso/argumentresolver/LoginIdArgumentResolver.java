package com.guenbon.siso.argumentresolver;

import com.guenbon.siso.exception.UnAuthorizedException;
import com.guenbon.siso.exception.errorCode.AuthErrorCode;
import com.guenbon.siso.service.JwtTokenProvider;
import com.guenbon.siso.support.annotation.LoginId;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

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
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String accessToken = request.getHeader(ACCESS_TOKEN);
        if (accessToken == null || accessToken.isBlank()) {
            throw new UnAuthorizedException(AuthErrorCode.NULL_OR_BLANK_TOKEN);
        }
        return jwtTokenProvider.getMemberId(accessToken);
    }
}
