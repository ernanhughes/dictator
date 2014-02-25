package com.banba.dictator.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.banba.dictator.R;
import com.banba.dictator.Util;
import com.banba.dictator.data.Recording;
import com.banba.dictator.ui.timessquare.CalendarPickerView;
import com.banba.dictator.ui.util.CalendarUtil;
import com.banba.dictator.ui.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Ernan on 25/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class CalendarFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_calendar, container, false);
        final List<Recording> recordings = Util.getAllRecordings(getActivity());
        final CalendarPickerView calendar = (CalendarPickerView) rootView.findViewById(R.id.calendar_view);
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
            final Calendar nextMonth = Calendar.getInstance();
            nextMonth.add(Calendar.MONTH, 1);
            final Calendar lastMonth = Calendar.getInstance();
            lastMonth.add(Calendar.MONTH, -1);
            calendar.init(lastMonth.getTime(), nextMonth.getTime()) //
                    .inMode(CalendarPickerView.SelectionMode.SINGLE) //
                    .withSelectedDate(new Date());
        }

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                final List<Recording> results = Util.getRecordingsForDate(getActivity(), date);
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.ic_save)
                        .setTitle("Play Recording for date " + DateTimeUtil.shortDateFormat(date))
                        .setPositiveButton("Play",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (results.size() > 0) {
                                            Recording recording = results.get(0);
                                            Util.playRecording(getActivity(), recording);
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
        return rootView;
    }
}
