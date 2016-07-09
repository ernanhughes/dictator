package programmer.ie.dictator.util;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.TimeZone;

public class CalendarUtil {


    public static String insertEvent(Context context, String title, String description, Calendar beginTime, Calendar endTime) {
        long startMillis = beginTime.getTimeInMillis();
        long endMillis = endTime.getTimeInMillis();

// Insert Event
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

// Retrieve ID for new event
        String eventID = uri.getLastPathSegment();
        return eventID;
    }

}
