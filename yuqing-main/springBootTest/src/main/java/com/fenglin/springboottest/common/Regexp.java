package com.fenglin.springboottest.common;

import java.util.regex.Pattern;

/**
 * 账号相关正则与校验工具，Controller 与 Service 共用，避免规则分散。
 */
public final class Regexp {

    /** 密码：6-15 位，必须包含数字、大小写字母、特殊字符 */
    public static final Pattern PASSWORD = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{6,15}$");

    /** 邮箱（宽松但能挡住明显错误） */
    public static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private Regexp() {
    }

    public static boolean isPassword(String s) {
        return s != null && PASSWORD.matcher(s).matches();
    }

    public static boolean isEmail(String s) {
        return s != null && EMAIL.matcher(s).matches();
    }
}
