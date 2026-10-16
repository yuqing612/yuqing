package com.fenglin.springboottest.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWT 登录令牌服务（HS256）。
 * - 无状态：后端重启后已签发的 token 依然有效；
 * - 勾选"记住我"有效期 7 天，否则 12 小时（app.jwt.expire-* 可配）；
 * - 登出通过 jti 黑名单实现，黑名单条目随 token 过期自动清理。
 */
@Service
public class AuthTokenService {

    private final SecretKey key;
    private final long expireRemember;
    private final long expireSession;

    /** 已登出但未过期的 token jti -> 过期时间戳 */
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public AuthTokenService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expire-remember:604800000}") long expireRemember,
            @Value("${app.jwt.expire-session:43200000}") long expireSession) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireRemember = expireRemember;
        this.expireSession = expireSession;
    }

    /** 登录成功后签发 JWT，subject 为 userId */
    public String create(Long userId, boolean remember) {
        cleanBlacklist();
        long ttl = remember ? expireRemember : expireSession;
        Date now = new Date();
        return Jwts.builder()
                .id(UUID.randomUUID().toString().replace("-", ""))
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttl))
                .signWith(key)
                .compact();
    }

    /** 校验 token，返回 userId；无效、过期或已登出返回 null */
    public Long resolve(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if (claims.getId() != null && blacklist.containsKey(claims.getId())) {
                return null;
            }
            return Long.valueOf(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    /** 登出：把 jti 加入黑名单直至 token 原本过期 */
    public void revoke(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if (claims.getId() != null && claims.getExpiration() != null) {
                blacklist.put(claims.getId(), claims.getExpiration().getTime());
            }
        } catch (JwtException | IllegalArgumentException ignored) {
            /* 无效 token 无需处理 */
        }
    }

    private void cleanBlacklist() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(e -> e.getValue() < now);
    }
}
