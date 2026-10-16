package com.fenglin.springboottest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * 修改资料：昵称 / 邮箱，均可选填，至少传一个有效字段即生效。
 */
public class UpdateProfileRequest {

    @Size(max = 32, message = "昵称最多 32 个字符")
    private String nickname;

    @Email(message = "邮箱格式不太对哦～")
    private String email;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
