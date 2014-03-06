package com.banba.dictator;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;

import com.banba.dictator.data.DaoMaster;
import com.banba.dictator.data.DaoSession;
import com.banba.dictator.data.Recording;
import com.banba.dictator.data.RecordingDao;
import com.banba.dictator.lib.L;
import com.banba.dictator.lib.util.CalendarUtil;
import com.banba.dictator.lib.util.DateTimeUtil;

import java.io.File;
import java.io.IOException;
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

    public static final String FILE_NAME = "Uri";
    public static final String POSITION = "Position";
    public static final String DURATION = "Duration";
    public static final String AMPLITUDE = "Amplitude";
    public static final String RECORDING = "Recording";


    public static String getDatabaseName() {
        return DictatorApp.DATABASE_NAME;
    }

    public static List<Recording> getAllRecordings(Context context) {
        L.d("Get all recordings");
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, getDatabaseName(), null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession session = daoMaster.newSession();
        final RecordingDao dataDao = session.getRecordingDao();
        List<Recording> recordings = dataDao.loadAll();
        Collections.sort(recordings, new Comparator<Recording>() {
            @Override
            public int compare(Recording lhs, Recording rhs) {
                return rhs.getStartTime().compareTo(lhs.getStartTime());
            }
        });
        helper.close();
        return recordings;
    }

    public static String getRecordingLength(Recording recording) {
        Date start = recording.getStartTime();
        Date end = recording.getEndTime();
        return DateTimeUtil.milliSecondsToTimer(end.getTime() - start.getTime());
    }

    public static List<Recording> getRecordingsForDate(Context context, Date date) {
        L.d("Get recordings for :  " + date);
        Date nextDay = DateTimeUtil.addDays(date, 1);
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, getDatabaseName(), null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession session = daoMaster.newSession();
        RecordingDao dataDao = session.getRecordingDao();
        List<Recording> results = dataDao.queryRaw("where " + RecordingDao.Properties.StartTime.columnName + " between ? and ?", String.valueOf(date.getTime()),
                String.valueOf(nextDay.getTime()));
        helper.close();
        return results;
    }

    public static long saveRecording(Context context, Recording recording) {
        L.d("Saving recording:  " + recording.getName());
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, getDatabaseName(), null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession session = daoMaster.newSession();
        RecordingDao dataDao = session.getRecordingDao();
        long result = dataDao.insert(recording);
        helper.close();
        return result;
    }

    public static boolean deleteRecording(Context context, Recording recording) {
        L.d("Deleting recording: " + recording.getId() + "  " + recording.getName());
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, getDatabaseName(), null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession session = daoMaster.newSession();
        RecordingDao dataDao = session.getRecordingDao();
        File file = new File(recording.getFileName());
        boolean result = false;
        try {
            result = file.getCanonicalFile().delete();
        } catch (IOException e) {
            L.e(e.getMessage());
        }
        dataDao.delete(recording);
        helper.close();
        return result;
    }

    public static boolean isValidMediaFile(String path) {
        L.d("Is Valid Media : " + path);
        File f = new File(path);
        return f.exists() && f.length() > 10;
    }

    public static void renameRecording(Context context, Recording recording, String newName) {
        L.d("Rename recording: " + recording.getName() + "  " + newName);
        try {
            String fileName = recording.getFileName();
            String shortName = getShortName(fileName);
            String newNameEx = newName + "." + getExtension(fileName);
            String newFileName = fileName.replace(shortName, newNameEx);
            File file = new File(recording.getFileName());
            File to = new File(newFileName);
            file.renameTo(to.getCanonicalFile());
            recording.setFileName(newFileName);
            recording.setName(newName);
            updateRecording(context, recording);
        } catch (IOException e) {
            L.e(e.getMessage());
        }
    }

    public static void updateRecording(Context context, Recording recording) {
        L.d("Update recording: " + recording.getId() + " " + recording.getName());
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, getDatabaseName(), null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession session = daoMaster.newSession();
        RecordingDao dataDao = session.getRecordingDao();
        dataDao.update(recording);
        helper.close();
    }


    public static String getRecordingFileName(Context context) {
        File outputDir = context.getFilesDir();
        StringBuilder buf = new StringBuilder(outputDir.getAbsolutePath())
                .append(File.separator)
                .append(DateTimeUtil.shortFileNameFormat(new Date()))
                .append(".3gpp");
        String fileName = buf.toString();
        L.d("Recoding file name: " + fileName);
        return fileName;
    }


    public static MediaRecorder createRecorder() {
        MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        return recorder;
    }


    public static String getShortName(String fileName) {
        Uri uri = Uri.parse(fileName);
        return uri.getLastPathSegment();
    }


    public static String getExtension(String fileName) {
        String[] split = fileName.split("\\.");
        return split[split.length - 1];
    }


    public static String getRecordingName(Context context) {
        return "Rec " + DateTimeUtil.getDateTime(new Date());
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
        helper.close();
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
                DateTimeUtil.dateToCalendar(recording.getStartTime()),
                DateTimeUtil.dateToCalendar(recording.getEndTime()));
    }


    public static Drawable getImage(Context context, Recording recording) {
        DateTimeUtil.TimeOfDay tod = DateTimeUtil.getTimeOfDay(recording.getEndTime());
        switch (tod) {
            case Morning:
                return context.getResources().getDrawable(R.drawable.morning);
            case Day:
                return context.getResources().getDrawable(R.drawable.day);
        }
        return context.getResources().getDrawable(R.drawable.night);
    }
}
