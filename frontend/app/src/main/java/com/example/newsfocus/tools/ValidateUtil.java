package com.example.newsfocus.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证工具
 *
 */
public class ValidateUtil {

    private ValidateUtil() {
        // 防止被实例化
    }

    /**
     * 字符串是否符合正则表达式的规则
     *
     * @param text 匹配文本
     * @param format 匹配规则
     * @return true 匹配成功 flase 匹配失败
     */
    private static boolean isMatches(String text, String format) {
        Pattern pattern = Pattern.compile(format);
        Matcher m = pattern.matcher(text);
        return m.matches();
    }

    /**
     * 匹配帐号类型是否正确（只能输入大小写字母和数字，最大不超过20个字符）
     *
     * @param str 帐号
     * @return true= 符合 false=不符合
     */
    public static boolean isAccount(String str) {
        String format = "[a-zA-Z0-9]{1,20}";
        return isMatches(str, format);
    }

    /**
     * 匹配金额是否符合要求（99999999.99）
     *
     * @param money 金额字符串
     * @return true= 符合 false=不符合
     */
    public static boolean isMoney(String money) {
        String regex = "(^[1-9][0-9]{0,7}(\\.[0-9]{0,2})?)|(^0(\\.[0-9]{0,2})?)";
        return isMatches(money, regex);
    }

    public static boolean isPhone(String phone) {
        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}";
        return isMatches(phone, regex);
    }

    public static boolean isPassword(String password) {
        String regex = "^[\\s\\S]{8,}";
        return isMatches(password, regex);
    }
}