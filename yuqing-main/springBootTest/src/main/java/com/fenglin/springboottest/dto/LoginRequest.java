package com.fenglin.springboottest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求：account 支持用户名或邮箱
 */
public class LoginRequest {

    @NotBlank(message = "请输入账号")
    private String account;

    @NotBlank(message = "请输入密码")
    private String password;

    /** 是否记住登录状态（影响 token 有效期） */
    private Boolean remember;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getRemember() {
        return remember;
    }

    public void setRemember(Boolean remember) {
        this.remember = remember;
    }
}
