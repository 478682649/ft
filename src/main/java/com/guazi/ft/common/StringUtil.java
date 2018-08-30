package com.guazi.ft.common;

import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

/**
 * 字符串工具类
 *
 * @author shichunyang
 */
public class StringUtil {

    public static final String NULL = "null";

    /**
     * redis key 分隔符
     */
    public static final String REDIS_SPLIT = ":";

    private StringUtil() {
    }

    /**
     * 判断字符串是否为null、""、" "、"null"
     *
     * @param str 要判断的字符串
     * @return 结果
     */
    public static boolean isNull(String str) {
        return (str == null || str.trim().isEmpty() || str.trim().toLowerCase().equals(NULL));
    }

    /**
     * 判断对象是否为空
     *
     * @param obj 对象
     * @return true:对象为空
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof String) {
            return isNull((String) obj);
        }

        if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        }

        if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        }

        if (obj.getClass().isArray()) {
            return ArrayUtils.getLength(obj) == 0;
        }

        return false;
    }

    public static boolean equals(Object obj1, Object obj2) {
        return (obj1 == obj2) || (obj1 != null && obj1.equals(obj2));
    }

    /**
     * 将字符串数组通过分隔符连接成字符串
     *
     * @param split  分隔符
     * @param strArr 字符串数组
     * @return 目标字符串
     */
    public static String append(String split, String... strArr) {
        StringBuilder sb = new StringBuilder();

        for (String s : strArr) {
            sb.append(s).append(split);
        }

        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 替换
     */
    public static String replace(
            String input,
            String oldStr,
            String replaceStr
    ) {
        StringBuilder sb = new StringBuilder(input.length());

        int startIndex = 0;

        int index;

        while ((index = input.indexOf(oldStr, startIndex)) != -1) {
            sb.append(input.substring(startIndex, index)).append(replaceStr);

            startIndex = index + oldStr.length();
        }

        return sb.append(input.substring(startIndex)).toString();
    }

    /**
     * 返回对象(字符串,集合,Map,迭代器,枚举)长度
     *
     * @param obj 对象
     * @return 对象长度
     */
    public static int length(Object obj) {
        if (obj == null) {
            return 0;
        }

        if (obj instanceof String) {
            return ((String) obj).length();
        }

        if (obj instanceof Collection) {
            return ((Collection) obj).size();
        }

        if (obj instanceof Map) {
            return ((Map) obj).size();
        }

        if (obj instanceof Iterator) {
            Iterator iterator = (Iterator) obj;
            int count = 0;
            while (iterator.hasNext()) {
                count++;
                iterator.next();
            }
            return count;
        }

        if (obj instanceof Enumeration) {
            Enumeration enumeration = (Enumeration) obj;

            int count = 0;

            while (enumeration.hasMoreElements()) {
                count++;
                enumeration.nextElement();
            }

            return count;
        }

        throw new RuntimeException("length 未知对象类型");
    }

    /**
     * 转义输入的内容
     */
    private static String escFilter(String message) {
        if (StringUtil.isNull(message)) {
            return null;
        }

        char[] contentArr = message.toCharArray();

        StringBuilder result = new StringBuilder();
        for (char ch : contentArr) {
            switch (ch) {

                case '<':
                    result.append("&lt;");
                    break;

                case '>':
                    result.append("&gt;");
                    break;

                case '&':
                    result.append("&amp;");
                    break;

                case '"':
                    result.append("&quot;");
                    break;

                default:
                    result.append(ch);
                    break;
            }
        }

        return result.toString();
    }

    /**
     * 拷贝数组
     *
     * @param src     源数组
     * @param srcPos  源数组中的起始位置
     * @param dest    目标数组
     * @param destPos 目标数组中的起始位置
     * @param length  要复制的数组元素的数量
     */
    @SuppressWarnings("all")
    public static void arrayCopy(Object src, int srcPos,
                                 Object dest, int destPos,
                                 int length) {
        System.arraycopy(src, srcPos, dest, destPos, length);
    }

    public static void main(String[] args) {
    }
}
