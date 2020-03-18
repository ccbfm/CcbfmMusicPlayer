package com.ccbfm.music.player.tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateTools {

    public static final String FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";

    /**
     * Date类型转为指定格式的String类型
     *
     */
    public static String dateToString(Date source, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return simpleDateFormat.format(source);
    }
}
