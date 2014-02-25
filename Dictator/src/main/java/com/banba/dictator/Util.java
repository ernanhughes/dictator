package com.banba.dictator;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import com.banba.dictator.data.DaoMaster;
import com.banba.dictator.data.DaoSession;
import com.banba.dictator.data.Recording;
import com.banba.dictator.data.RecordingDao;
import com.banba.dictator.ui.L;
import com.banba.dictator.ui.util.CalendarUtil;
import com.banba.dictator.ui.util.DateTimeUtil;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Ernan on 25/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class Util {

    public static String getDatabaseName() {
        return DictatorApp.DATABASE_NAME;
    }

    public static List<Recording> getAllRecordings(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, getDatabaseName(), null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession session = daoMaster.newSession();
        final RecordingDao dataDao = session.getRecordingDao();
        List<Recording> recordings = dataDao.loadAll();
        Collections.sort(recordings, new Comparator<Recording>() {
            @Override
            public int compare(Recording lhs, Recording rhs) {
                return lhs.getStartTime().compareTo(rhs.getStartTime());
            }
        });
        return recordings;
    }

    public static List<Recording> getRecordingsForDate(Context context, Date date) {
        Date nextDay = CalendarUtil.addDays(date, 1);
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, getDatabaseName(), null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession session = daoMaster.newSession();
        RecordingDao dataDao = session.getRecordingDao();
        List<Recording> results = dataDao.queryRaw("where " + RecordingDao.Properties.StartTime.columnName + " between ? and ?", String.valueOf(date.getTime()),
                String.valueOf(nextDay.getTime()));
        return results;
    }

    public static void saveRecording(Context context, Recording recording) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, getDatabaseName(), null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession session = daoMaster.newSession();
        RecordingDao dataDao = session.getRecordingDao();
        dataDao.insert(recording);
    }

    public static void save100(Context context, Recording recording) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, getDatabaseName(), null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession session = daoMaster.newSession();
        RecordingDao dataDao = session.getRecordingDao();
        for (int i = 0; i < 100; ++i) {
            recording.setId(null);
            String dt = "2011-01-01";  // Start date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(sdf.parse(dt));
            } catch (ParseException e) {
                L.e(e.getMessage());
            }
            c.add(Calendar.DATE, i);  // number of days to add
            c.add(Calendar.HOUR_OF_DAY, i);  // number of days to add
            recording.setStartTime(c.getTime());
            c.add(Calendar.HOUR_OF_DAY, 1);  // number of days to add
            recording.setEndTime(c.getTime());
            dataDao.insert(recording);
        }
    }

    public static void addMediaEntry(Context context, String fileName) {
        ContentValues values = new ContentValues();
        long current = System.currentTimeMillis();
        values.put(MediaStore.MediaColumns.TITLE, "Dictator " + DateTimeUtil.shortDateFormat(new Date()));
        values.put(MediaStore.MediaColumns.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/3gpp");
        values.put(MediaStore.MediaColumns.DATA, fileName);
        ContentResolver contentResolver = context.getContentResolver();
        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
    }

    public static void addCalendarEntry(Context context, Recording recording) {
        CalendarUtil.insertEvent(context, recording.getName(), recording.getFileName(),
                CalendarUtil.dateToCalendar(recording.getStartTime()),
                CalendarUtil.dateToCalendar(recording.getEndTime()));
    }

    public static void playRecording(Context context, Recording recording) {
        String path = recording.getFileName();
        File f = new File(path);
        Uri uri = Uri.fromFile(f);
        MediaPlayer player = MediaPlayer.create(context, uri);
        player.start();
    }


}
