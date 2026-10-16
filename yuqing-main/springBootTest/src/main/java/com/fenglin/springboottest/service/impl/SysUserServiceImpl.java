package com.fenglin.springboottest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fenglin.springboottest.common.Regexp;
import com.fenglin.springboottest.entity.SysUser;
import com.fenglin.springboottest.mapper.SysUserMapper;
import com.fenglin.springboottest.service.SysUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public SysUser login(String account, String rawPassword) {
        SysUser user = this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, account)
                .or()
                .eq(SysUser::getEmail, account)
                .last("limit 1"), false);
        if (user == null) {
            throw new IllegalArgumentException("账号不存在，先去注册一个吧");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new IllegalArgumentException("账号已被禁用，请联系管理员");
        }
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("密码错误，再试一次吧");
        }
        return user;
    }

    @Override
    public SysUser register(String username, String email, String rawPassword, String nickname) {
        if (this.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)) > 0) {
            throw new IllegalArgumentException("用户名已被占用");
        }
        if (StringUtils.hasText(email)) {
            if (!Regexp.isEmail(email)) {
                throw new IllegalArgumentException("邮箱格式不太对哦～");
            }
            if (this.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, email)) > 0) {
                throw new IllegalArgumentException("该邮箱已注册过账号");
            }
        }
        if (!Regexp.isPassword(rawPassword)) {
            throw new IllegalArgumentException("密码需为 6-15 位，且包含数字、大小写字母和特殊字符");
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setEmail(StringUtils.hasText(email) ? email : null);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setNickname(StringUtils.hasText(nickname) ? nickname : username);
        user.setStatus(1);
        this.save(user);
        return user;
    }

    @Override
    public SysUser updateProfile(Long userId, String nickname, String email) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("账号不存在");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new IllegalArgumentException("账号已被禁用，请联系管理员");
        }
        boolean changed = false;
        if (StringUtils.hasText(nickname)) {
            if (nickname.trim().length() > 32) {
                throw new IllegalArgumentException("昵称最多 32 个字符");
            }
            user.setNickname(nickname.trim());
            changed = true;
        }
        if (StringUtils.hasText(email)) {
            if (!Regexp.isEmail(email)) {
                throw new IllegalArgumentException("邮箱格式不太对哦～");
            }
            if (this.count(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getEmail, email)
                    .ne(SysUser::getId, userId)) > 0) {
                throw new IllegalArgumentException("该邮箱已注册过账号");
            }
            user.setEmail(email.trim());
            changed = true;
        }
        if (!changed) {
            throw new IllegalArgumentException("没有可更新的内容");
        }
        this.updateById(user);
        return user;
    }

    @Override
    public void changePassword(Long userId, String oldRaw, String newRaw) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("账号不存在");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new IllegalArgumentException("账号已被禁用，请联系管理员");
        }
        if (!passwordEncoder.matches(oldRaw, user.getPassword())) {
            throw new IllegalArgumentException("原密码错误");
        }
        if (!Regexp.isPassword(newRaw)) {
            throw new IllegalArgumentException("新密码需为 6-15 位，且包含数字、大小写字母和特殊字符");
        }
        if (passwordEncoder.matches(newRaw, user.getPassword())) {
            throw new IllegalArgumentException("新密码不能与旧密码相同");
        }
        user.setPassword(passwordEncoder.encode(newRaw));
        this.updateById(user);
    }
}
