package com.fenglin.springboottest.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录失败限流：同一账号连续失败达到阈值后锁定一段时间，缓解暴力破解。
 * 单机内存实现，重启即清空（开发/演示足够；生产建议换 Redis）。
 */
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_MILLIS = 15 * 60 * 1000L; // 15 分钟

    private final ConcurrentHashMap<String, Entry> attempts = new ConcurrentHashMap<>();

    private static final class Entry {
        int fails;
        long lockUntil;
    }

    public boolean isLocked(String account) {
        Entry e = attempts.get(keyOf(account));
        return e != null && System.currentTimeMillis() < e.lockUntil;
    }

    /** 剩余锁定秒数，未锁定返回 0 */
    public long remainingSeconds(String account) {
        Entry e = attempts.get(keyOf(account));
        if (e == null) {
            return 0;
        }
        long left = e.lockUntil - System.currentTimeMillis();
        return left > 0 ? left / 1000 : 0;
    }

    public void onFailure(String account) {
        Entry e = attempts.computeIfAbsent(keyOf(account), k -> new Entry());
        synchronized (e) {
            e.fails++;
            if (e.fails >= MAX_ATTEMPTS) {
                e.lockUntil = System.currentTimeMillis() + LOCK_MILLIS;
            }
        }
    }

    public void onSuccess(String account) {
        attempts.remove(keyOf(account));
    }

    private static String keyOf(String account) {
        return account == null ? "" : account.trim().toLowerCase();
    }
}
