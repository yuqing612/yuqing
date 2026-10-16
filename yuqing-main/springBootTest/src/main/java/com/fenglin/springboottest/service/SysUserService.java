package com.fenglin.springboottest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fenglin.springboottest.entity.SysUser;

public interface SysUserService extends IService<SysUser> {

    /**
     * 登录校验：account 支持用户名或邮箱
     *
     * @throws IllegalArgumentException 账号不存在 / 被禁用 / 密码错误
     */
    SysUser login(String account, String rawPassword);

    /**
     * 注册新用户
     *
     * @throws IllegalArgumentException 用户名或邮箱已被占用 / 密码不符合规则
     */
    SysUser register(String username, String email, String rawPassword, String nickname);

    /**
     * 修改资料（昵称 / 邮箱，可选）
     *
     * @throws IllegalArgumentException 邮箱已被占用 / 账号被禁用 / 无可更新内容
     */
    SysUser updateProfile(Long userId, String nickname, String email);

    /**
     * 修改密码：校验原密码后设置新密码
     *
     * @throws IllegalArgumentException 原密码错误 / 新密码不符合规则 / 与旧密码相同
     */
    void changePassword(Long userId, String oldRaw, String newRaw);
}
