package com.guenbon.siso.interceptor;

import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.service.auth.JwtTokenProvider;
import com.guenbon.siso.support.annotation.Login;
import com.guenbon.siso.support.constants.MemberRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

import static com.guenbon.siso.exception.errorCode.AuthErrorCode.EXPIRED;
import static com.guenbon.siso.exception.errorCode.AuthErrorCode.NOT_ADMIN_ROLE;
import static com.guenbon.siso.interceptor.AuthInterceptor.ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthInterceptorTest {
    public static final String METHOD_WITH_LOGIN_MEMBER_ANNOTATION = "loginMemberAnnotationMethod";
    public static final String METHOD_WITH_LOGIN_ADMIN_ANNOTATION = "loginAdminAnnotationMethod";
    public static final String ACCESS_TOKEN_VALUE = "accessTokenValue";
    @InjectMocks
    private AuthInterceptor authInterceptor;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HandlerMethod handler;

    // 테스트 시 사용할 애너테이션이 달린 메서드 역할을 하기 위해 만듦
    @Login(role = MemberRole.MEMBER)
    public void loginMemberAnnotationMethod() {
    }

    @Login(role = MemberRole.ADMIN)
    public void loginAdminAnnotationMethod() {
    }


    @DisplayName("@Login 이 없는 컨트롤러에 preHandle 이 true 리턴한다.")
    @Test
    void noLogin_preHandle_returnTrue() throws Exception {
        // given
        when(handler.getMethodAnnotation(Login.class)).thenReturn(null);
        // when
        boolean result = authInterceptor.preHandle(request, response, handler);
        // then
        assertThat(result).isTrue();
    }

    private static Login getAnnotation(String methodName) throws NoSuchMethodException {
        Method method = AuthInterceptorTest.class.getMethod(methodName);
        return method.getAnnotation(Login.class);
    }

    @DisplayName("@Login 이 있는 컨트롤러에 유효하지 않은 accessToken 으로 요청 시 preHandle 이 false 리턴한다.")
    @Test
    void loginAnnotationInvalidToken_preHandle_false() throws Exception {
        // given
        Login annotation = getAnnotation(METHOD_WITH_LOGIN_MEMBER_ANNOTATION);
        when(handler.getMethodAnnotation(Login.class)).thenReturn(annotation);
        when(request.getHeader(ACCESS_TOKEN)).thenReturn(ACCESS_TOKEN_VALUE);
        when(jwtTokenProvider.getRole(ACCESS_TOKEN_VALUE)).thenThrow(new CustomException(EXPIRED));

        // when, then
        assertThatThrownBy(
                () -> authInterceptor.preHandle(request, response, handler),
                EXPIRED.getMessage(),
                CustomException.class
        );
    }

    @DisplayName("@Login 이 있는 컨트롤러에 유효한 accessToken 으로 요청 시 true 리턴한다.")
    @EnumSource(MemberRole.class)
    @ParameterizedTest
    void loginAnnotationValidToken_preHandle_true(MemberRole memberRole) throws Exception {
        // given
        Login annotation = getAnnotation(METHOD_WITH_LOGIN_MEMBER_ANNOTATION);
        when(handler.getMethodAnnotation(Login.class)).thenReturn(annotation);
        when(request.getHeader(ACCESS_TOKEN)).thenReturn(ACCESS_TOKEN_VALUE);
        when(jwtTokenProvider.getRole(ACCESS_TOKEN_VALUE)).thenReturn(memberRole);
        // when
        boolean result = authInterceptor.preHandle(request, response, handler);
        // then
        assertThat(result).isTrue();
    }

    @DisplayName("@Login(ADMIN) 이 있는 컨트롤러에 MEMBER 권한 accessToken 으로 요청 시 예외를 던진다.")
    @Test
    void loginAdminAnnotationMemberAccessToken_preHandle_CustomException() throws NoSuchMethodException {
        // given
        Login annotation = getAnnotation(METHOD_WITH_LOGIN_ADMIN_ANNOTATION);
        when(handler.getMethodAnnotation(Login.class)).thenReturn(annotation);
        when(request.getHeader(ACCESS_TOKEN)).thenReturn(ACCESS_TOKEN_VALUE);
        when(jwtTokenProvider.getRole(ACCESS_TOKEN_VALUE)).thenReturn(MemberRole.MEMBER);

        // when, then
        assertThatThrownBy(
                () -> authInterceptor.preHandle(request, response, handler),
                NOT_ADMIN_ROLE.getMessage(),
                CustomException.class
        );
    }

    @DisplayName("@Login(ADMIN) 이 있는 컨트롤러에 ADMIN 권한 accessToken 으로 요청 시 true 리턴한다.")
    @Test
    void loginAdminAnnotationAdminAccessToken_preHandle_true() throws Exception {
        // given
        Login annotation = getAnnotation(METHOD_WITH_LOGIN_ADMIN_ANNOTATION);
        when(handler.getMethodAnnotation(Login.class)).thenReturn(annotation);
        when(request.getHeader(ACCESS_TOKEN)).thenReturn(ACCESS_TOKEN_VALUE);
        when(jwtTokenProvider.getRole(ACCESS_TOKEN_VALUE)).thenReturn(MemberRole.ADMIN);
        // when
        boolean result = authInterceptor.preHandle(request, response, handler);
        // then
        assertThat(result).isTrue();
    }
}