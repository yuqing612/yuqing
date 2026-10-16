package com.fenglin.springboottest.controller;

import com.fenglin.springboottest.common.AuthContext;
import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.dto.ChangePasswordRequest;
import com.fenglin.springboottest.dto.UpdateProfileRequest;
import com.fenglin.springboottest.entity.SysUser;
import com.fenglin.springboottest.service.SysUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录后受保护账号接口：改资料、改密码。
 * 所有请求经由 AuthInterceptor 校验 JWT 并写入 AuthContext。
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final SysUserService userService;

    public UserController(SysUserService userService) {
        this.userService = userService;
    }

    @PutMapping("/profile")
    public Result<Map<String, Object>> updateProfile(@Valid @RequestBody UpdateProfileRequest req) {
        SysUser user = userService.updateProfile(AuthContext.getUserId(), req.getNickname(), req.getEmail());
        return Result.ok(buildUserView(user));
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        userService.changePassword(AuthContext.getUserId(), req.getOldPassword(), req.getNewPassword());
        return Result.ok();
    }

    private Map<String, Object> buildUserView(SysUser user) {
        Map<String, Object> view = new HashMap<>();
        view.put("id", user.getId());
        view.put("username", user.getUsername());
        view.put("email", user.getEmail());
        view.put("nickname", user.getNickname());
        return view;
    }
}
