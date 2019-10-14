package com.example.newsfocus.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    public static String getTimeStrip() {
        long timecurrentTimeMillis = System.currentTimeMillis();
        return timecurrentTimeMillis + "";
    }

    public static String timeStrip2String(String s) {
        long l = Long.parseLong(s);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(l);
        return simpleDateFormat.format(date);
    }
}
