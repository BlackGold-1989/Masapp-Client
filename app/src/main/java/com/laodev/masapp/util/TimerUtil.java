package com.laodev.masapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimerUtil {

    public static String getCurrentDate(String type) {
        SimpleDateFormat sdf = new SimpleDateFormat(type, Locale.US);
        return sdf.format(Calendar.getInstance().getTime());
    }

    public static String getStringByCalendar(String type, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat(type, Locale.US);
        return sdf.format(calendar.getTime());
    }

    public static String getYYYYMMDD() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        return simpleDateFormat.format(date);
    }

    public static Date getDateByString(String type, String str_date) {
        SimpleDateFormat sdf = new SimpleDateFormat(type, Locale.US);
        try {
            return sdf.parse(str_date);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static String getDelayTime(String time, String type) {
        Date localTime;
        try {
            localTime = new SimpleDateFormat(type, Locale.getDefault()).parse(time);
        } catch (ParseException e) {
            return "Unknown";
        }
        Date currentTime = new Date();
        double diff = (currentTime.getTime() - localTime.getTime()) / 1000;
        if (diff > 3600 * 24) {
            int day = (int) (diff / (3600 * 24));
            return day + "d ago";
        } else if (diff > 3600) {
            int hour = (int) (diff / 3600);
            return hour + "h ago";
        } else if (diff > 60) {
            int min = (int) (diff / 60);
            return min + "m ago";
        } else {
            return "Just";
        }
    }

}
