package com.banba.dictator.test;

import android.content.Context;
import android.test.ActivityTestCase;

import com.banba.dictator.Util;
import com.banba.dictator.data.Recording;

import junit.framework.Assert;

import java.util.Date;
import java.util.List;

/**
 * Created by Ernan on 22/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class RecordingText extends ActivityTestCase {
    public void testAllRecordings() throws Exception {
        Context context = getInstrumentation().getTargetContext();
        List<Recording> recordings = Util.getAllRecordings(context);
        for (Recording recording : recordings) {
            Util.addCalendarEntry(context, recording);
            String len = Util.getRecordingLength(recording);

            Date date = recording.getStartTime();
            List<Recording> dated = Util.getRecordingsForDate(context, date);
            Assert.assertTrue(dated.size() > 0);

            Assert.assertTrue(Util.isValidMediaFile(recording.getFileName()));
        }
    }

    public void testSaveDeleteRecordings() throws Exception {
        Context context = getInstrumentation().getTargetContext();
        List<Recording> recordings = Util.getAllRecordings(context);
        for (Recording recording : recordings) {
            Util.addCalendarEntry(context, recording);
            String len = Util.getRecordingLength(recording);

            Date date = recording.getStartTime();
            List<Recording> dated = Util.getRecordingsForDate(context, date);
            Assert.assertTrue(dated.size() > 0);

            Util.addMediaEntry(context, recording.getFileName());

            Util.addMediaEntry(context, recording.getFileName());
        }
    }

}
