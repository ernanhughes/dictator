package com.banba.dictator;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.banba.dictator.ui.L;
import com.banba.dictator.ui.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
        setContentView(R.layout.sample_calendar_picker);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }


        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);

        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        calendar.init(lastYear.getTime(), nextYear.getTime()) //
                .inMode(CalendarPickerView.SelectionMode.SINGLE) //
                .withSelectedDate(new Date());

        final Button single = (Button) findViewById(R.id.button_single);
        final Button multi = (Button) findViewById(R.id.button_multi);
        final Button range = (Button) findViewById(R.id.button_range);
        final Button displayOnly = (Button) findViewById(R.id.button_display_only);
        final Button dialog = (Button) findViewById(R.id.button_dialog);
        single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                single.setEnabled(false);
                multi.setEnabled(true);
                range.setEnabled(true);
                displayOnly.setEnabled(true);

                calendar.init(lastYear.getTime(), nextYear.getTime()) //
                        .inMode(CalendarPickerView.SelectionMode.SINGLE) //
                        .withSelectedDate(new Date());
            }
        });

        multi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                single.setEnabled(true);
                multi.setEnabled(false);
                range.setEnabled(true);
                displayOnly.setEnabled(true);

                Calendar today = Calendar.getInstance();
                ArrayList<Date> dates = new ArrayList<Date>();
                for (int i = 0; i < 5; i++) {
                    today.add(Calendar.DAY_OF_MONTH, 3);
                    dates.add(today.getTime());
                }
                calendar.init(new Date(), nextYear.getTime()) //
                        .inMode(CalendarPickerView.SelectionMode.MULTIPLE) //
                        .withSelectedDates(dates);
            }
        });

        range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                single.setEnabled(true);
                multi.setEnabled(true);
                range.setEnabled(false);
                displayOnly.setEnabled(true);

                Calendar today = Calendar.getInstance();
                ArrayList<Date> dates = new ArrayList<Date>();
                today.add(Calendar.DATE, 3);
                dates.add(today.getTime());
                today.add(Calendar.DATE, 5);
                dates.add(today.getTime());
                calendar.init(new Date(), nextYear.getTime()) //
                        .inMode(CalendarPickerView.SelectionMode.RANGE) //
                        .withSelectedDates(dates);
            }
        });

        displayOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                single.setEnabled(true);
                multi.setEnabled(true);
                range.setEnabled(true);
                displayOnly.setEnabled(false);

                calendar.init(new Date(), nextYear.getTime()) //
                        .inMode(CalendarPickerView.SelectionMode.SINGLE) //
                        .withSelectedDate(new Date())
                        .displayOnly();
            }
        });

        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogView = (CalendarPickerView) getLayoutInflater().inflate(R.layout.dialog, null, false);
                dialogView.init(lastYear.getTime(), nextYear.getTime()) //
                        .withSelectedDate(new Date());
                theDialog =
                        new AlertDialog.Builder(CalendarActivity.this).setTitle("I'm a dialog!")
                                .setView(dialogView)
                                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .create();
                theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        L.d("onShow: fix the dimens!");
                        dialogView.fixDialogDimens();
                    }
                });
                theDialog.show();
            }
        });

        findViewById(R.id.done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                L.d("Selected time in millis: " +  calendar.getSelectedDate().getTime());
                String toast = "Selected: " + calendar.getSelectedDate().getTime();
                Toast.makeText(CalendarActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        boolean applyFixes = theDialog != null && theDialog.isShowing();
        if (applyFixes) {
            L.d("Config change: unfix the dimens so I'll get remeasured!");
            dialogView.unfixDialogDimens();
        }
        super.onConfigurationChanged(newConfig);
        if (applyFixes) {
            dialogView.post(new Runnable() {
                @Override
                public void run() {
                    L.d("Config change done: re-fix the dimens!");
                    dialogView.fixDialogDimens();
                }
            });
        }
    }
}
