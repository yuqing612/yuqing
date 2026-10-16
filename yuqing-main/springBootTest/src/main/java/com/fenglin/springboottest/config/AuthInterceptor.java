package com.fenglin.springboottest.config;

import com.fenglin.springboottest.common.AuthContext;
import com.fenglin.springboottest.service.AuthTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 登录鉴权拦截器：对所有 /api/**（除登录、注册）校验 JWT，
 * 通过后将 userId 与原始 token 写入 AuthContext，供后续接口直接取用。
 * token 缺失或无效直接返回 401，不再进入 Controller。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthTokenService tokenService;

    public AuthInterceptor(AuthTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = extractToken(request.getHeader("Authorization"));
        Long userId = tokenService.resolve(token);
        if (userId == null) {
            writeUnauthorized(response);
            return false;
        }
        AuthContext.set(userId, token);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private String extractToken(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        return authorization.startsWith("Bearer ") ? authorization.substring(7).trim() : authorization.trim();
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"登录状态已失效，请重新登录\",\"data\":null}");
    }
}
