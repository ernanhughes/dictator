package com.banba.dictator;

import java.util.Date;

/**
 * Created by Ernan on 21/11/13.
 * Copyrite Banba Inc. 2013.
 */
public class TimeFormatter {
    public static String formatTime(int seconds) {
        return Integer.toString(seconds / 60) + ":" + padWithZeros(seconds % 60);
    }

    public static String formatTime(float seconds) {
        return formatTime((int) seconds);
    }

    private static String padWithZeros(int seconds) {
        return seconds < 10 ? "0" + seconds : Integer.toString(seconds);
    }

    public static String shortFormat(Date date) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        return String.valueOf(df.format("yy-MM-dd HH:mm:ss", date));
    }

    public static String shortFileNameFormat(Date date) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        return String.valueOf(df.format("yyMMddHHmmss", date));
    }

}
