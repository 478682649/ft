package com.guazi.ft.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author shichunyang
 */
public class DateUtil {

    /**
     * 默认时间格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 私有构造方法
     */
    private DateUtil() {
    }

    /**
     * 获取当前系统时间
     *
     * @return 当前系统时间
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    /**
     * 获取当前系统时间字符串
     *
     * @return 当前系统时间字符串
     */
    public static String getCurrentDateStr() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return dateFormat.format(new Date());
    }

    /**
     * 将时间字符串转换成日期格式
     *
     * @param dateStr 时间字符串
     * @param pattern 格式化模版
     * @return 转换后的Date对象
     */
    public static Date str2Date(String dateStr, String pattern) {
        Date date = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    /**
     * 将日期类型转换成字符串
     *
     * @param date    日期
     * @param pattern 格式化模版
     * @return 格式化后的时间字符串
     */
    public static String date2Str(Date date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    /**
     * 根据日历的规则,为给定的日期字段添加或减去指定的时间量(例如:要从当前日历时间减去5天)
     *
     * @return 计算后的新日期
     */
    public static Date getDateAddAmount(Date date, int field, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.add(field, amount);

        return cal.getTime();
    }

    /**
     * 计算某天开始时间
     *
     * @return 某天开始时间
     */
    public static Date getDayStartTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return cal.getTime();
    }

    /**
     * 计算某天的结束时间
     *
     * @return 某天的结束时间
     */
    public static Date getDayEndTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        return cal.getTime();
    }

    /**
     * 计算月初时间
     *
     * @return 月初时间
     */
    public static Date getMonthStartTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return cal.getTime();
    }

    /**
     * 计算月末时间
     *
     * @return 月末时间
     */
    public static Date getMonthEndTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        return cal.getTime();
    }

    public static void main(String[] args) {
    }
}
