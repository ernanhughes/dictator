package programmer.ie.dictator.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import java.io.File;
import java.util.Date;

import de.greenrobot.event.EventBus;
import programmer.ie.dictator.Util;
import programmer.ie.dictator.data.Recording;
import programmer.ie.dictator.event.RecordEvent;
import programmer.ie.dictator.util.L;

public class RecordService extends Service {

    public static final String BROADCAST_ACTION = "programmer.ie.dictator.recordservice";
    private final Handler handler = new Handler();
    Intent intent;
    MediaRecorder mRecorder;
    String mOutputFile;
    Recording recording;
    long mStartTime = 0l;

    private Runnable outGoingHandler = new Runnable() {
        public void run() {
            if (mRecorder != null) {
                updateRecordingInfo();
                handler.postDelayed(this, 100);
            }
        }
    };

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
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        intent = new Intent(BROADCAST_ACTION);
    }

    public void updateRecordingInfo() {
        Bundle b = new Bundle();
        long now = SystemClock.uptimeMillis();
        long totalTime = now - mStartTime;
        b.putLong(Util.DURATION, totalTime);
        int amp = mRecorder.getMaxAmplitude();
        b.putInt(Util.AMPLITUDE, amp);
        intent.putExtras(b);
        sendBroadcast(intent);
    }

    public void onEvent(RecordEvent event) {
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
                        mRecorder.stop();
                    } catch (Exception ex) {
                        L.e(ex.getMessage());
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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
