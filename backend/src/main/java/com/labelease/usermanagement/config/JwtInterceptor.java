package com.labelease.usermanagement.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.labelease.usermanagement.common.JwtUtil;
import com.labelease.usermanagement.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 鉴权拦截器 - 非公开接口需携带有效 Token
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // 放行 OPTIONS 请求（CORS 预检）
        if ("OPTIONS".equalsIgnoreCase(method)) {
            log.debug("放行OPTIONS请求: {}", requestURI);
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        log.debug("请求URI: {}, 方法: {}, Authorization头: {}", requestURI, method,
                authHeader != null ? authHeader.substring(0, Math.min(authHeader.length(), 30)) + "..." : "null");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isValid = jwtUtil.validateToken(token);
            if (isValid) {
                log.debug("Token验证通过");
                return true;
            } else {
                log.warn("Token验证失败: {}", token.substring(0, Math.min(token.length(), 20)));
            }
        } else {
            log.warn("未找到有效的Authorization头");
        }

        // 未通过鉴权
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(
                Result.unauthorized("未登录或令牌已过期，请重新登录")
        ));
        return false;
    }
}
