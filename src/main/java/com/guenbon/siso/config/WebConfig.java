package com.guenbon.siso.config;

import com.guenbon.siso.argumentresolver.CustomPageableResolver;
import com.guenbon.siso.argumentresolver.LoginIdArgumentResolver;
import com.guenbon.siso.interceptor.AuthInterceptor;
import com.guenbon.siso.interceptor.LoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final LoginIdArgumentResolver loginIdArgumentResolver;
    private final CustomPageableResolver customPageableResolver;
    private final LoggingInterceptor loggingInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginIdArgumentResolver);
        resolvers.add(customPageableResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**");

        registry.addInterceptor(authInterceptor)
                .excludePathPatterns();
    }

    // CORS 설정 추가
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 엔드포인트에서 CORS 허용
                .allowedOrigins("*") // 모든 도메인에서 요청 가능
                .allowedMethods("GET", "POST", "DELETE", "PATCH") // 모든 HTTP 메서드 허용
                .allowedHeaders("*") // 모든 요청 헤더 허용
                .allowCredentials(false); // 인증 정보 포함 여부 (true 사용 시 특정 origin 필요)
    }
}
