package com.example.bank_rest_test_task.util.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * Фильтр в цепочке для получения ip пользователя который отправил запрос
 */
@Component
public class IpFilter implements Filter {

    /**
     * Получает ip пользователя и прикрепляет его к текущему потоку выполнения
     */
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String ip = getClientIp(request);

        MDC.put("ip", ip);

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.remove("ip");
        }
    }

    /**
     * Получает запрос и достает ip из заголовка {@code X-Forwarded-For} если он не null и не пустой достает его
     * в противном случае пытается получить его из заголовка {@code X-Real-Ip}
     *
     * @param request запрос пользователя
     * @return ip в виде строки
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknow".equalsIgnoreCase(ip)) {
            return ip.split(",")[0];
        }
        ip = request.getHeader("X-Real-Ip");
        return (ip != null && !ip.isEmpty()) ? ip : request.getRemoteAddr();
    }
}
