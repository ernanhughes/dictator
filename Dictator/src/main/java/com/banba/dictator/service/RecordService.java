package com.banba.dictator.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.banba.dictator.Util;
import com.banba.dictator.data.Recording;
import com.banba.dictator.event.Record;
import com.banba.dictator.lib.L;

import java.io.File;
import java.util.Date;

import de.greenrobot.event.EventBus;

/**
 * Created by Ernan on 27/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class RecordService extends Service {

    Intent intent;
    MediaRecorder mRecorder;
    String mOutputFile;
    Recording recording;
    long mStartTime = 0l;

    public static final String BROADCAST_ACTION = "com.banba.dictator.recordservice";
    private final Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        intent = new Intent(BROADCAST_ACTION);
    }

    static final private double EMA_FILTER = 0.6;
    private double mEMA = 0.0;


    public void updateRecordingInfo() {
        Bundle b = new Bundle();
        long now = SystemClock.uptimeMillis();
        long totalTime = now - mStartTime;
        b.putLong(Util.DURATION, totalTime);
        int amp = mRecorder.getMaxAmplitude();
        b.putInt(Util.AMPLITUDE, amp);
        double damp = Double.valueOf(amp) / 2700.0;
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        b.putDouble(Util.EMA, mEMA);

        intent.putExtras(b);
        sendBroadcast(intent);
    }

    private Runnable outGoingHandler = new Runnable() {
        public void run() {
            if (mRecorder != null) {
                updateRecordingInfo();
                handler.postDelayed(this, 200);
            }
        }
    };

    public void onEvent(Record event) {
        switch (event.action) {
            case Start: {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOnErrorListener(errorListener);
                mRecorder.setOnInfoListener(infoListener);
                try {
                    String fileName = Util.getRecordingFileName(this);
                    File outputFile = new File(fileName);
                    mOutputFile = outputFile.getAbsolutePath();
                    mRecorder.setOutputFile(mOutputFile);
                    mRecorder.prepare();

                    recording = new Recording();
                    recording.setId(null);
                    recording.setName(Util.getRecordingName(this));
                    recording.setStartTime(new Date());
                    recording.setFileName(mOutputFile);

                    mStartTime = SystemClock.uptimeMillis();
                    handler.removeCallbacks(outGoingHandler);
                    handler.postDelayed(outGoingHandler, 100);
                    mRecorder.start();
                } catch (Exception ex) {
                    L.e(ex.getMessage());
                    return;
                }
                break;
            }
            case Stop: {
                if (mRecorder != null) {
                    try {
                        mRecorder.stop(); // illegal state
                    } catch (Exception ex) {
                    }
                    mRecorder.reset();
                    mRecorder.release();
                    mRecorder = null;
                    recording.setEndTime(new Date());
                    File f = new File(mOutputFile);
                    L.i("File saved: " + mOutputFile + " " + f.length() + " (bytes)");
                    recording.setFileSize(f.length() / 1000);
                    Util.saveRecording(this, recording);
                    Util.addMediaEntry(this, mOutputFile);
                    Util.addCalendarEntry(this, recording);
                }
            }
        }
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            L.e("Error: " + what + ", " + extra);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            L.e("Warning: " + what + ", " + extra);
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                L.e("Maximum Duration Reached");
            } else if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
                L.e("Maximum File size Reached");
            }
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
