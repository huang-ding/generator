package org.base.hd.generator.util;

/**
 * @author huangding
 * @description
 * @date 2018/11/27 13:56
 */
public class StringUtils {

    /**
     * 首字母大写
     */
    public static String upperCaseFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 首字母小写
     */
    public static String lowerCaseFirst(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * _，转换为驼峰式
     */
    public static String underlineToCamelCase(String underscoreName) {
        StringBuilder result = new StringBuilder();
        if (underscoreName != null && underscoreName.trim().length() > 0) {
            boolean flag = false;
            for (int i = 0; i < underscoreName.length(); i++) {
                char ch = underscoreName.charAt(i);
                if ("_".charAt(0) == ch) {
                    flag = true;
                } else {
                    if (flag) {
                        result.append(Character.toUpperCase(ch));
                        flag = false;
                    } else {
                        result.append(ch);
                    }
                }
            }
        }
        return result.toString();
    }
}