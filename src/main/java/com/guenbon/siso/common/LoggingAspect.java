package com.guenbon.siso.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = RequestContextHolderUtil.getRequest();
        HttpServletResponse response = RequestContextHolderUtil.getResponse();

        if (request == null || response == null) {
            return joinPoint.proceed();
        }

        // 요청 데이터 읽기 (Body 포함)
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // 요청 정보 로깅
        logRequestInfo(wrappedRequest, joinPoint);

        Object result = joinPoint.proceed();

        // 응답 정보 로깅
        logResponseInfo(wrappedResponse);

        return result;
    }

    private void logRequestInfo(ContentCachingRequestWrapper request, ProceedingJoinPoint joinPoint) {
        try {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            Map<String, String> params = getRequestParams(request);
            String body = getRequestBody(request);

            log.info("==> 요청: [{}] {} | Params: {} | Body: {}", method, uri, params, body);
        } catch (Exception e) {
            log.warn("요청 로깅 중 오류 발생", e);
        }
    }

    private void logResponseInfo(ContentCachingResponseWrapper response) {
        try {
            String body = getResponseBody(response);
            log.info("<== 응답: 상태코드={} | Body={}", response.getStatus(), body);
            response.copyBodyToResponse(); // 중요: 원본 응답 복사
        } catch (Exception e) {
            log.warn("응답 로깅 중 오류 발생", e);
        }
    }

    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            params.put(paramName, request.getParameter(paramName));
        }
        return params;
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        if (!HttpMethod.POST.matches(request.getMethod()) &&
                !HttpMethod.PUT.matches(request.getMethod()) &&
                !HttpMethod.PATCH.matches(request.getMethod())) {
            return "";
        }

        byte[] content = request.getContentAsByteArray();
        return content.length > 0 ? new String(content, StandardCharsets.UTF_8) : "";
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        return content.length > 0 ? new String(content, StandardCharsets.UTF_8) : "";
    }
}

