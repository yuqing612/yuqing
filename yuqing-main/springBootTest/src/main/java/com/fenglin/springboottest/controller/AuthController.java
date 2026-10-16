package com.fenglin.springboottest.controller;

import com.fenglin.springboottest.common.AuthContext;
import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.dto.LoginRequest;
import com.fenglin.springboottest.dto.RegisterRequest;
import com.fenglin.springboottest.entity.SysUser;
import com.fenglin.springboottest.service.AuthTokenService;
import com.fenglin.springboottest.service.LoginAttemptService;
import com.fenglin.springboottest.service.SysUserService;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 前后端分离的认证接口，对接 anime-login-vue 前端。
 * 登录/注册公开，me/logout 由 AuthInterceptor 保护。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final SysUserService userService;
    private final AuthTokenService tokenService;
    private final LoginAttemptService loginAttemptService;

    public AuthController(SysUserService userService, AuthTokenService tokenService, LoginAttemptService loginAttemptService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest req) {
        String account = req.getAccount().trim();
        if (loginAttemptService.isLocked(account)) {
            long minutes = loginAttemptService.remainingSeconds(account) / 60 + 1;
            return Result.fail("尝试次数过多，请 " + minutes + " 分钟后再试");
        }
        try {
            SysUser user = userService.login(account, req.getPassword());
            loginAttemptService.onSuccess(account);
            boolean remember = Boolean.TRUE.equals(req.getRemember());
            Map<String, Object> view = buildUserView(user);
            view.put("token", tokenService.create(user.getId(), remember));
            return Result.ok(view);
        } catch (IllegalArgumentException e) {
            loginAttemptService.onFailure(account);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 恢复会话：前端启动时携带 token 调用，换取最新用户信息。
     * userId 已由 AuthInterceptor 写入 AuthContext。
     */
    @GetMapping("/me")
    public Result<Map<String, Object>> me() {
        Long userId = AuthContext.getUserId();
        SysUser user = userService.getById(userId);
        if (user == null || (user.getStatus() != null && user.getStatus() == 0)) {
            return Result.unauthorized("账号不可用，请重新登录");
        }
        return Result.ok(buildUserView(user));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        tokenService.revoke(AuthContext.getToken());
        return Result.ok();
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody RegisterRequest req) {
        String username = req.getUsername().trim();
        String email = StringUtils.hasText(req.getEmail()) ? req.getEmail().trim() : null;
        SysUser user = userService.register(username, email, req.getPassword(), req.getNickname());
        return Result.ok(buildUserView(user));
    }

    private Map<String, Object> buildUserView(SysUser user) {
        Map<String, Object> view = new HashMap<>();
        view.put("id", user.getId());
        view.put("username", user.getUsername());
        view.put("email", user.getEmail());
        view.put("nickname", StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getUsername());
        return view;
    }
}
