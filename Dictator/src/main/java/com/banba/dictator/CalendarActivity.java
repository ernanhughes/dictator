package com.banba.dictator;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.banba.dictator.data.DaoMaster;
import com.banba.dictator.data.DaoSession;
import com.banba.dictator.data.Recording;
import com.banba.dictator.data.RecordingDao;
import com.banba.dictator.ui.timessquare.CalendarPickerView;
import com.banba.dictator.ui.util.CalendarUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * Created by Ernan on 25/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class CalendarActivity extends Activity {
    private CalendarPickerView calendar;
    private AlertDialog theDialog;
    private CalendarPickerView dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DictatorApp.DATABASE_NAME, null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession session = daoMaster.newSession();
        final RecordingDao dataDao = session.getRecordingDao();
        final List<Recording> recordings = dataDao.loadAll();
        Collections.sort(recordings, new Comparator<Recording>() {
            @Override
            public int compare(Recording lhs, Recording rhs) {
                return lhs.getStartTime().compareTo(rhs.getStartTime());
            }
        });

        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        if (recordings.size() > 0) {
            Recording first = recordings.get(0);
            Recording last = recordings.get(recordings.size() - 1);
            Calendar lastDate = CalendarUtil.dateToCalendar(last.getStartTime());
            lastDate.add(Calendar.DAY_OF_WEEK, 7);
            List<Date> dates = new ArrayList<Date>();
            for (Recording r : recordings) {
                dates.add(r.getStartTime());
            }
            calendar.init(first.getStartTime(), lastDate.getTime()) //
                    .inMode(CalendarPickerView.SelectionMode.SINGLE)
                    .withHighlightedDates(dates)
                    .withSelectedDate(last.getStartTime());
        } else {
            final Calendar nextYear = Calendar.getInstance();
            nextYear.add(Calendar.YEAR, 1);
            final Calendar lastYear = Calendar.getInstance();
            lastYear.add(Calendar.YEAR, -1);
            calendar.init(lastYear.getTime(), nextYear.getTime()) //
                    .inMode(CalendarPickerView.SelectionMode.SINGLE) //
                    .withSelectedDate(new Date());
        }

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Toast.makeText(CalendarActivity.this, "You selected date: " + date, Toast.LENGTH_SHORT);
                Date nextDay = CalendarUtil.addDays(date, 1);
                final List<Recording> results = dataDao.queryRaw("where " + RecordingDao.Properties.StartTime.columnName + " between ? and ?", String.valueOf(date.getTime()),
                        String.valueOf(nextDay.getTime()));
                new AlertDialog.Builder(CalendarActivity.this)
                        .setIcon(R.drawable.ic_save)
                        .setTitle("Play Recording")
                        .setPositiveButton("Play",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (results.size() > 0) {
                                            Recording rec = results.get(0);
                                            String path = rec.getFileName();
                                            File f = new File(path);
                                            Uri uri = Uri.fromFile(f);
                                            MediaPlayer player = MediaPlayer.create(CalendarActivity.this, uri);
                                            player.start();
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
    }
}
