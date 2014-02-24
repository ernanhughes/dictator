package com.banba.dictator;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ShareActionProvider;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        EventBus.getDefault().register(this);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
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

            startRecording();
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

    MediaRecorder mRecorder;
    String mOutputFile;
    File mSampleFile = null;

    protected void startRecording() {
        mIsRecording = true;
        mUpdateTimeTask.run();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            File outputDir = getCacheDir();
            File outputFile = File.createTempFile("prefix", "extension", outputDir);
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
        mRecorder.stop();
        mRecorder.release();
        ContentValues values = new ContentValues(3);
        long current = System.currentTimeMillis();
        values.put(MediaStore.MediaColumns.TITLE, "test_audio");
        values.put(MediaStore.MediaColumns.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/3gpp");
        values.put(MediaStore.MediaColumns.DATA, mOutputFile);
        ContentResolver contentResolver = getContentResolver();
        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
    }


    boolean mIsRecording = false;
    long mLastLogTime, mTotalTime = 0l;
    Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mIsRecording) {
                long now = SystemClock.uptimeMillis();
                mTotalTime += now - mLastLogTime;
                mLastLogTime = now;
                setTitle("Recording  " + TimeFormatter.formatTime(mTotalTime / 1000));
                mHandler.postDelayed(mUpdateTimeTask, 1000);
            }
        }
    };


    public static class MainFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }
    }
}
