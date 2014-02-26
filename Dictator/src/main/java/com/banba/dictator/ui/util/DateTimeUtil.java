package com.banba.dictator.ui.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ernan on 21/11/13.
 * Copyrite Banba Inc. 2013.
 */
public class DateTimeUtil {

    static final String FILE_NAME_FORMAT = "yyMMddHHmmss";
    static final String DATE_FORMAT = "yy-MM-dd HH:mm:ss";
    static final String SHORT_DATE_FORMAT = "yy-MM-dd";
    static final String SQL_DATE_FORMAT = "yy-MM-dd";
    static final String TIME_FORMAT = "HH:mm:ss";
    static final String DATE_TIME_FORMAT = "yy-MM-dd HH:mm";

    public static String formatTime(int seconds) {
        return Integer.toString(seconds / 60) + ":" + padWithZeros(seconds % 60);
    }

    public static String formatTime(float seconds) {
        return formatTime((int) seconds);
    }

    public static String shortSQLDate(Date date) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        return String.valueOf(df.format(SQL_DATE_FORMAT, date));
    }

    public static String getTime(Date date) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        return String.valueOf(df.format(TIME_FORMAT, date));
    }

    public static String getDateTime(Date date) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        return String.valueOf(df.format(DATE_TIME_FORMAT, date));
    }


    private static String padWithZeros(int seconds) {
        return seconds < 10 ? "0" + seconds : Integer.toString(seconds);
    }

    public static String shortDateFormat(Date date) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        return String.valueOf(df.format(SHORT_DATE_FORMAT, date));
    }

    public static Calendar dateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static Date addOneDay(Date date) {
        return addDays(date, 1);
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_WEEK, days);
        return cal.getTime();
    }


    public static String normalDateFormat(Date date) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        return String.valueOf(df.format(DATE_FORMAT, date));
    }

    public static String shortFileNameFormat(Date date) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        return String.valueOf(df.format(FILE_NAME_FORMAT, date));
    }

}
