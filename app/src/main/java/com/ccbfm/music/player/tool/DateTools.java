package com.ccbfm.music.player.tool;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateTools {

    public static final String FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";

    public static final String FORMAT_MS = "mm:ss";


    /**
     * Date类型转为指定格式的String类型
     *
     */
    public static String dateToString(Date source, String format) {
        DateFormat dateFormat = getDateFormat(format);
        return dateFormat.format(source);
    }

    public static String longToString(long time, String format) {
        DateFormat dateFormat = getDateFormat(format);
        return dateFormat.format(time);
    }

    public static DateFormat getDateFormat(String format) {
        return new SimpleDateFormat(format, Locale.getDefault());
    }
}
