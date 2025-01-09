package com.guenbon.siso.config;

import com.guenbon.siso.argumentresolver.CustomPageableArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PageableResolverConfig implements WebMvcConfigurer {

    private final CustomPageableArgumentResolver pageableVerificationArgumentResolver;

    public PageableResolverConfig(CustomPageableArgumentResolver customPageableArgumentResolver) {
        this.pageableVerificationArgumentResolver = customPageableArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(pageableVerificationArgumentResolver);
    }
}