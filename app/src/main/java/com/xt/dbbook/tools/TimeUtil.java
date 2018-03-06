package com.xt.dbbook.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xt on 2018/02/06.
 */

public class TimeUtil {

    /**
     * 计算yyyy-mm-dd HH:mm:ss格式的时间间隔的天数
     *
     * @param date1 yyyy-mm-dd HH:mm:ss
     * @param date2 yyyy-mm-dd HH:mm:ss
     * @return
     */
    public static long daysBetween(String date1, String date2) {
        Date d1 = getDate(date1);
        Date d2 = getDate(date2);
        if (d1 != null && d2 != null) {
            return (Math.abs(d2.getTime() - d1.getTime())) / (3600 * 24 * 1000);
        } else
            return -1;
    }

    /**
     * @param date       yyyy-mm-dd
     * @param timeMillis 毫秒
     * @return
     */
    public static long daysBetween(String date, long timeMillis) {
        Date d1 = getDate(date);
        if (d1 != null && timeMillis > 0) {
            return (Math.abs(timeMillis - d1.getTime()) + 1000000) / (3600 * 24 * 1000);
        } else
            return -1;
    }

    /**
     * 将yyyy-mm-dd HH:mm:ss字符串转换为Date对象
     *
     * @param date
     * @return
     */
    public static Date getDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        Date d = null;
        try {
            d = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
}
