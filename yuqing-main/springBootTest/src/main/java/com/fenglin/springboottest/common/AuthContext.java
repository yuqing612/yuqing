package com.fenglin.springboottest.common;

/**
 * 当前登录用户上下文（基于 ThreadLocal）。
 * 由 AuthInterceptor 在请求开始时写入，请求结束后清除。
 * 受保护的接口可直接取 userId，无需重复解析 token。
 */
public final class AuthContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> TOKEN = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void set(Long userId, String token) {
        USER_ID.set(userId);
        TOKEN.set(token);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static String getToken() {
        return TOKEN.get();
    }

    public static void clear() {
        USER_ID.remove();
        TOKEN.remove();
    }
}
