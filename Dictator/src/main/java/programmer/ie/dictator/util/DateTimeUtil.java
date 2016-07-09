package programmer.ie.dictator.util;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ernan on 21/11/13.
 * Copyrite Banba Inc. 2013.
 */
public class DateTimeUtil {

    private static final String FILE_NAME_FORMAT = "yyMMddHHmmss";
    private static final String DATE_FORMAT = "yy-MM-dd HH:mm:ss";
    private static final String SHORT_DATE_FORMAT = "yy-MM-dd";
    private static final String SQL_DATE_FORMAT = "yy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String DATE_TIME_FORMAT = "yy-MM-dd HH:mm";

    public static TimeOfDay getTimeOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 12) {
            return TimeOfDay.Morning;
        }
        if (hour < 20) {
            return TimeOfDay.Day;
        }
        return TimeOfDay.Night;
    }

    private static String formatTime(int seconds) {
        return Integer.toString(seconds / 60) + ":" + padWithZeros(seconds % 60);
    }

    public static String formatTime(float seconds) {
        return formatTime((int) seconds);
    }

    public static String shortSQLDate(Date date) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        return String.valueOf(DateFormat.format(SQL_DATE_FORMAT, date));
    }

    public static String getTime(Date date) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        return String.valueOf(DateFormat.format(TIME_FORMAT, date));
    }

    public static String getDateTime(Date date) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        return String.valueOf(DateFormat.format(DATE_TIME_FORMAT, date));
    }

    private static String padWithZeros(int seconds) {
        return seconds < 10 ? "0" + seconds : Integer.toString(seconds);
    }

    public static String shortDateFormat(Date date) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        return String.valueOf(DateFormat.format(SHORT_DATE_FORMAT, date));
    }

    public static String shortTimeFormat(long timeToFormat) {
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(timeToFormat),
                TimeUnit.MILLISECONDS.toSeconds(timeToFormat) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes(timeToFormat)));
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
        return String.valueOf(DateFormat.format(DATE_FORMAT, date));
    }

    public static String shortFileNameFormat(Date date) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        return String.valueOf(DateFormat.format(FILE_NAME_FORMAT, date));
    }

    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * Function to get Progress percentage
     *
     * @param currentDuration
     * @param totalDuration
     */
    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     *
     * @param progress      -
     * @param totalDuration returns current duration in milliseconds
     */
    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);
        return currentDuration * 1000;
    }

    public enum TimeOfDay {
        Morning,
        Day,
        Night
    }

}
