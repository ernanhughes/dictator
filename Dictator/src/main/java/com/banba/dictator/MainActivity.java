package com.banba.dictator;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.banba.dictator.data.Recording;
import com.banba.dictator.event.SectionEvent;
import com.banba.dictator.ui.ColorUtil;
import com.banba.dictator.ui.L;
import com.banba.dictator.ui.util.DateTimeUtil;

import java.io.File;
import java.util.Date;

import de.greenrobot.event.EventBus;

/**
 * Created by Ernan on 23/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class MainActivity extends Activity {
    private ShareActionProvider mShareActionProvider;
    MediaRecorder mRecorder;
    String mOutputFile;
    ImageButton mRecordButton;
    TextView mRecordText;
    boolean mIsRecording = false;
    long mLastLogTime = 0l, mTotalTime = 0l;
    Handler mHandler = new Handler();
    private ProgressBar mProgressBar;
    Recording recording;
    ViewSwitcher viewSwitcher;
    LinearLayout firstView, secondView;
    Animation inAnimRight, outAnimLeft, inAnimLeft, outAnimRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        inAnimRight = AnimationUtils.loadAnimation(this,
                R.anim.grow_from_bottom);
        outAnimLeft = AnimationUtils.loadAnimation(this,
                R.anim.grow_from_top);
        inAnimLeft = AnimationUtils.loadAnimation(this,
                R.anim.fragment_slide_left_enter);
        outAnimRight = AnimationUtils.loadAnimation(this,
                R.anim.fragment_slide_left_exit);

        mRecordButton = (ImageButton) findViewById(R.id.iconRecord);
        mRecordText = (TextView) findViewById(R.id.textRecord);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
        firstView = (LinearLayout) findViewById(R.id.bottomLayout);
        secondView = (LinearLayout) findViewById(R.id.recordingFeedbackLayout);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_share) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            startActivity(Intent.createChooser(share, getString(R.string.share_using)));
            return true;
        }
        if (id == R.id.menu_item_search) {
            Intent search = new Intent(this, SearchActivity.class);
            startActivity(search);
            return true;
        }
        if (id == R.id.menu_item_ex) {
            Intent search = new Intent(this, ExceptionActivity.class);
            startActivity(search);
            return true;
        }
        if (id == R.id.menu_item_apache) {
            Intent search = new Intent(this, ApacheActivity.class);
            startActivity(search);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onEvent(SectionEvent event) {
        EventBus.getDefault().cancelEventDelivery(event);
        if (SectionEvent.SEARCH.equalsIgnoreCase(event.section)) {
            Intent i = new Intent(this, SearchActivity.class);
            startActivity(i);
        } else if (SectionEvent.RECORD.equalsIgnoreCase(event.section)) {
            if (mIsRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        } else if (SectionEvent.MANAGE.equalsIgnoreCase(event.section)) {
            Intent i = new Intent(this, ManageActivity.class);
            startActivity(i);
        } else if (SectionEvent.CALENDAR.equalsIgnoreCase(event.section)) {
            Intent i = new Intent(this, CalendarActivity.class);
            startActivity(i);
        } else if (SectionEvent.HELP.equalsIgnoreCase(event.section)) {
            Intent i = new Intent(this, HelpActivity.class);
            startActivity(i);
        } else if (SectionEvent.ABOUT.equalsIgnoreCase(event.section)) {
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
        }
    }

    protected void startRecording() {
        viewSwitcher.setInAnimation(inAnimLeft);
        viewSwitcher.setOutAnimation(outAnimRight);
        if (viewSwitcher.getCurrentView() != secondView) {
            viewSwitcher.showNext();
        }
        mRecordButton.setImageResource(R.drawable.record_stop_large);

        new Runnable() {
            public void run() {
                if (mIsRecording) {
                    if (mLastLogTime == 0l) {
                        mLastLogTime = SystemClock.uptimeMillis();
                    }
                    long now = SystemClock.uptimeMillis();
                    mTotalTime += now - mLastLogTime;
                    mLastLogTime = now;
                    Spanned text = Html.fromHtml("<font color=\"#800000\">Recording " + DateTimeUtil.formatTime(mTotalTime / 1000) + "</font>");
                    getActionBar().setTitle(text);
                    mRecordText.setText(text);
                    mHandler.postDelayed(this, 1000);
                }
            }
        }.run();

        new Runnable() {
            public void run() {
                if (mIsRecording) {
                    int amplitude = mRecorder.getMaxAmplitude();
                    mProgressBar.setProgress(amplitude);
                    mHandler.postDelayed(this, 200);
                }
            }
        }.run();


        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            String fileName = Util.getRecordingFileName(this);
            File outputFile = new File(fileName);
            mOutputFile = outputFile.getAbsolutePath();
            mRecorder.setOutputFile(mOutputFile);
            mRecorder.prepare();

            recording = new Recording();
            recording.setId(null);
            recording.setName("Recording on " + new Date());
            recording.setStartTime(new Date());
            recording.setFileName(Uri.fromFile(outputFile).toString());

            mRecorder.start();
        } catch (Exception ex) {
            L.e(ex.getMessage());
            return;
        }
        mIsRecording = true;
    }

    protected void stopRecording() {
        mIsRecording = false;
        viewSwitcher.setInAnimation(inAnimLeft);
        viewSwitcher.setOutAnimation(outAnimRight);
        if (viewSwitcher.getCurrentView() != firstView) {
            viewSwitcher.showPrevious();
        }
        mTotalTime = 0l;
        try {
            mRecorder.stop(); // illegal state
        } catch (Exception ex) {
        }
        mRecorder.release();
        mRecordButton.setImageResource(R.drawable.record_start_large);
        getActionBar().setTitle(R.string.app_name);
        mRecordText.setText("RECORD");
        recording.setEndTime(new Date());
        File f = new File(mOutputFile);
        L.i("File saved: " + mOutputFile + " " + f.length() + " (bytes)");
        recording.setFileSize(f.length() / 1000);

        Util.saveRecording(this, recording);
        Util.addMediaEntry(this, mOutputFile);
        Util.addCalendarEntry(this, recording);
    }
}
