package com.banba.dictator;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.banba.dictator.event.SectionEvent;
import com.banba.dictator.ui.ColorUtil;
import com.banba.dictator.ui.L;

import java.io.File;

import de.greenrobot.event.EventBus;

/**
 * Created by Ernan on 23/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class MainActivity extends Activity {
    private ShareActionProvider mShareActionProvider;

    MediaRecorder mRecorder;
    String mOutputFile;
    File mSampleFile = null;
    ImageButton mRecordButton;
    TextView mRecordText;
    boolean mIsRecording = false;
    long mLastLogTime = 0l, mTotalTime = 0l;
    Handler mHandler = new Handler();
    int oldActionBarColor = Color.BLACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        mRecordButton = (ImageButton) findViewById(R.id.iconRecord);
        mRecordText = (TextView) findViewById(R.id.textRecord);

        EventBus.getDefault().register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        return super.onCreateOptionsMenu(menu);
    }

    public void onClick(View view) {
        ImageButton button = (ImageButton) view;
        String tag = (String) button.getTag();
        ColorUtil.applyTempColorFilter(button.getDrawable(), getResources().getColor(R.color.icon_selected_color));
        EventBus.getDefault().post(new SectionEvent(tag));
    }

    public void onEvent(SectionEvent section) {
        if (SectionEvent.SEARCH.equalsIgnoreCase(section.section)) {
            Intent i = new Intent(this, SearchActivity.class);
            startActivity(i);
        } else if (SectionEvent.RECORD.equalsIgnoreCase(section.section)) {
            if (mIsRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        } else if (SectionEvent.MANAGE.equalsIgnoreCase(section.section)) {
            // manage of entries
        } else if (SectionEvent.ENTRIES.equalsIgnoreCase(section.section)) {
            // calandar of entries
        } else if (SectionEvent.HELP.equalsIgnoreCase(section.section)) {
            Intent i = new Intent(this, HelpActivity.class);
            startActivity(i);
        } else if (SectionEvent.ABOUT.equalsIgnoreCase(section.section)) {
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
        }
    }


    protected void startRecording() {
        mIsRecording = true;
        mUpdateTimeTask.run();
        mRecordButton.setImageResource(R.drawable.record_stop_large);
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            File outputDir = getCacheDir();
            File outputFile = File.createTempFile("prefix", ".3gpp", outputDir);
            mOutputFile = outputFile.getAbsolutePath();
            mRecorder.setOutputFile(mOutputFile);
            mRecorder.prepare();
        } catch (Exception ex) {
            L.e(ex.getMessage());
        }
        mRecorder.start();
        if (this.mSampleFile == null) {
            File sampleDir = Environment.getExternalStorageDirectory();
            try {
                mSampleFile = File.createTempFile("Recording",
                        ".ogg", sampleDir);
            } catch (Exception e) {
                L.e("Access error " + e.getMessage());
                return;
            }
        }
    }

    protected void stopRecording() {
        mIsRecording = false;
        mTotalTime = 0l;
        mRecorder.stop();
        mRecorder.release();
        mRecordButton.setImageResource(R.drawable.record_start_large);
        getActionBar().setTitle(R.string.app_name);
        mRecordText.setText("RECORD");

        ContentValues values = new ContentValues();
        long current = System.currentTimeMillis();
        values.put(MediaStore.MediaColumns.TITLE, "test_audio");
        values.put(MediaStore.MediaColumns.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/3gpp");
        values.put(MediaStore.MediaColumns.DATA, mOutputFile);

        Toast.makeText(this, "File saved: " + mOutputFile, Toast.LENGTH_LONG).show();

        ContentResolver contentResolver = getContentResolver();
        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
    }


    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mIsRecording) {
                if (mLastLogTime == 0l) {
                    mLastLogTime = SystemClock.uptimeMillis();
                }
                long now = SystemClock.uptimeMillis();
                mTotalTime += now - mLastLogTime;
                mLastLogTime = now;
                Spanned text = Html.fromHtml("<font color=\"#800000\">Recording " + TimeFormatter.formatTime(mTotalTime / 1000) + "</font>");
                getActionBar().setTitle(text);
                mRecordText.setText(text);
                mHandler.postDelayed(mUpdateTimeTask, 1000);
            }
        }
    };
}
