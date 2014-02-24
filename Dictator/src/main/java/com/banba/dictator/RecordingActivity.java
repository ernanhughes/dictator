package com.banba.dictator;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.widget.Button;

import com.banba.dictator.ui.L;

import java.io.File;
import java.io.IOException;

public class RecordingActivity extends Activity {
    MediaRecorder mRecorder;
    File mSampleFile = null;
    static final String SAMPLE_PREFIX = "recording";
    static final String SAMPLE_EXTENSION = ".3gpp";
    private static final String OUTPUT_FILE = "/sdcard/audiooutput.3gpp";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recording);
        this.mRecorder = new MediaRecorder();

        Button startRecording = (Button) findViewById(R.id.startrecording);
        Button stopRecording = (Button) findViewById(R.id.stoprecording);

        startRecording.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startRecording();
            }
        });

        stopRecording.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopRecording();
                addToDB();
            }

        });
    }

    protected void addToDB() {
        ContentValues values = new ContentValues(3);
        long current = System.currentTimeMillis();

        values.put(MediaColumns.TITLE, "test_audio");
        values.put(MediaColumns.DATE_ADDED, (int) (current / 1000));
        values.put(MediaColumns.MIME_TYPE, "audio/3gpp");
        values.put(MediaColumns.DATA, OUTPUT_FILE);
        ContentResolver contentResolver = getContentResolver();

        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
    }

    protected void startRecording() {
        this.mRecorder = new MediaRecorder();
        this.mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        this.mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        this.mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        this.mRecorder.setOutputFile(OUTPUT_FILE);
        try {
            this.mRecorder.prepare();
        } catch (IllegalStateException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        this.mRecorder.start();

        if (this.mSampleFile == null) {
            File sampleDir = Environment.getExternalStorageDirectory();

            try {
                this.mSampleFile = File.createTempFile(RecordingActivity.SAMPLE_PREFIX,
                        RecordingActivity.SAMPLE_EXTENSION, sampleDir);
            } catch (IOException e) {
                L.e("sdcard access error");
                return;
            }
        }
    }

    protected void stopRecording() {
        this.mRecorder.stop();
        this.mRecorder.release();
    }
}
