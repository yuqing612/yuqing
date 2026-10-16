package com.fenglin.springboottest.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fenglin.springboottest.entity.SysUser;
import com.fenglin.springboottest.mapper.BangumiMapper;
import com.fenglin.springboottest.mapper.SysUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 启动时自动写入一个测试账号（不存在才插入），密码用 BCrypt 加密。
 * 测试账号：sakura / Test@123456（也可用邮箱 sakura@yuqing.dev 登录）
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final SysUserMapper userMapper;
    private final BangumiMapper bangumiMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataInitializer(SysUserMapper userMapper, BangumiMapper bangumiMapper) {
        this.userMapper = userMapper;
        this.bangumiMapper = bangumiMapper;
    }

    @Override
    public void run(String... args) {
        if (bangumiMapper.selectCount(null) == 0) {
            log.warn("bangumi 表为空，首页番剧板块将没有数据，请执行 yuqing_db.sql 导入种子数据");
        }
        Long count = userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, "sakura"));
        if (count != null && count > 0) {
            return;
        }
        SysUser user = new SysUser();
        user.setUsername("sakura");
        user.setEmail("sakura@yuqing.dev");
        user.setPassword(passwordEncoder.encode("Test@123456"));
        user.setNickname("桜守");
        user.setStatus(1);
        userMapper.insert(user);
        log.info("已初始化测试账号 sakura / Test@123456");
    }
}
