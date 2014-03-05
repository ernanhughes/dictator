package com.banba.dictator.fragment;

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
import com.banba.dictator.event.PlayRecordingEvent;
import com.banba.dictator.lib.timessquare.CalendarPickerView;
import com.banba.dictator.lib.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

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
        final List<Date> dates = new ArrayList<Date>();
        if (recordings.size() > 0) {
            // recording are sorted inversely
            Recording first = recordings.get(recordings.size() - 1);
            Recording last = recordings.get(0);
            Calendar lastDate = DateTimeUtil.dateToCalendar(last.getStartTime());
            lastDate.add(Calendar.DAY_OF_WEEK, 7);
            int maxCount = 50;
            for (int i = 0; i < recordings.size() && i < maxCount; ++i) {
                Recording r = recordings.get(i);
                dates.add(r.getStartTime());
            }
            calendar.init(first.getStartTime(), lastDate.getTime()) //
                    .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                    .withSelectedDates(dates);
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
                calendar.selectDate(date);
            }

            @Override
            public void onDateUnselected(Date date) {
                final List<Recording> results = Util.getRecordingsForDate(getActivity(), date);
                CharSequence[] items = new CharSequence[results.size()];
                for (int i = 0; i < results.size(); ++i) {
                    Recording r = results.get(i);
                    items[i] = r.getName();
                }
                if (results.size() > 0) {
                    new AlertDialog.Builder(getActivity())
                            .setIcon(R.drawable.ic_save)
                            .setTitle("PlayEvent Recording for date " + DateTimeUtil.shortDateFormat(date))
                            .setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (results.size() > 0) {
                                        Recording recording = results.get(0);
                                        EventBus.getDefault().post(new PlayRecordingEvent((recording)));
                                    }
                                }
                            }).create().show();
                }
                List<Date> datesl = calendar.getSelectedDates();
                if (!datesl.contains(date)) {
                    calendar.selectDate(date);
                }
            }
        });
        return rootView;
    }
}
