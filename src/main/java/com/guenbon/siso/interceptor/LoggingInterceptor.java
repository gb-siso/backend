package com.guenbon.siso.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // ✅ 요청 시작 로그는 preHandle에서 출력
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String remoteAddr = request.getRemoteAddr();
        String userAgent = request.getHeader("user-agent");

        StringBuilder sb = new StringBuilder();
        sb.append("[REQUEST INFO]\n");
        sb.append("├─ Method      : ").append(method).append("\n");
        sb.append("├─ URI         : ").append(uri).append("\n");
        sb.append("├─ Query       : ").append(query != null ? query : "-").append("\n");
        sb.append("├─ IP          : ").append(remoteAddr).append("\n");
        sb.append("└─ User-Agent  : ").append(userAgent);

        log.info(sb.toString());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        int status = response.getStatus();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String memberId = MDC.get("memberId"); // ✅ MDC에서 memberId 꺼내기

        StringBuilder sb = new StringBuilder();
        sb.append("[RESPONSE INFO]\n");
        sb.append("├─ Method      : ").append(method).append("\n");
        sb.append("├─ URI         : ").append(uri).append("\n");
        sb.append("├─ Status Code : ").append(status).append("\n");
        if (memberId != null) {
            sb.append("└─ Member ID   : ").append(memberId);
        } else {
            sb.append("└─ Member ID   : 로그인 정보 X");
        }

        log.info(sb.toString());

        if (ex != null) {
            log.error("[Exception] URI: {}, Message: {}", uri, ex.getMessage(), ex);
        }

        MDC.clear(); // ✅ 요청 종료 시 MDC 정리
    }
}


