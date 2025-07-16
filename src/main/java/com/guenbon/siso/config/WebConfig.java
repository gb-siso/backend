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
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://sisso.kr:3000", "http://localhost:8080")
                .allowedMethods("GET", "POST", "DELETE", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
