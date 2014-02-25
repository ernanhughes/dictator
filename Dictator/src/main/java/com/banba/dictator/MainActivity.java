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
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
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
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.banba.dictator.data.DaoMaster;
import com.banba.dictator.data.DaoSession;
import com.banba.dictator.data.Recording;
import com.banba.dictator.data.RecordingDao;
import com.banba.dictator.event.SectionEvent;
import com.banba.dictator.ui.ColorUtil;
import com.banba.dictator.ui.L;
import com.banba.dictator.ui.util.CalendarUtil;
import com.banba.dictator.ui.util.DateTimeUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    File mSampleFile = null;
    ImageButton mRecordButton;
    TextView mRecordText;
    boolean mIsRecording = false;
    long mLastLogTime = 0l, mTotalTime = 0l;
    Handler mHandler = new Handler();
    int oldActionBarColor = Color.BLACK;
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
        inAnimRight = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.grow_from_bottom);
        outAnimLeft = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.grow_from_top);
        inAnimLeft = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fragment_slide_left_enter);
        outAnimRight = AnimationUtils.loadAnimation(getApplicationContext(),
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
        mUpdateTimeTask.run();
        mRecordButton.setImageResource(R.drawable.record_stop_large);
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mSoundLevel.run();
        try {
            File outputDir = getFilesDir();
            StringBuilder buf = new StringBuilder(outputDir.getAbsolutePath())
                    .append(File.separator)
                    .append("Dictator_")
                    .append(DateTimeUtil.shortFileNameFormat(new Date()))
                    .append(".3gpp");

            String fileName = buf.toString();
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
        ContentValues values = new ContentValues();
        long current = System.currentTimeMillis();
        values.put(MediaStore.MediaColumns.TITLE, "test_audio");
        values.put(MediaStore.MediaColumns.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/3gpp");
        values.put(MediaStore.MediaColumns.DATA, mOutputFile);

        File f = new File(mOutputFile);
        Toast.makeText(this, "File saved: " + mOutputFile + " " + f.length() + " (bytes)", Toast.LENGTH_LONG).show();

        recording.setEndTime(new Date());
        recording.setFileSize(f.length() / 1000);
        try {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DictatorApp.DATABASE_NAME, null);
            DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
            DaoSession session = daoMaster.newSession();
            RecordingDao dataDao = session.getRecordingDao();
            dataDao.insert(recording);
            for (int i = 0; i < 1000; ++i) {
                recording.setId(null);
                String dt = "2010-01-01";  // Start date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                c.setTime(sdf.parse(dt));
                c.add(Calendar.DATE, i);  // number of days to add
                c.add(Calendar.HOUR_OF_DAY, i);  // number of days to add
                recording.setStartTime(c.getTime());
                c.add(Calendar.HOUR_OF_DAY, 1);  // number of days to add
                recording.setEndTime(c.getTime());
                dataDao.insert(recording);
            }
        } catch (Exception ex) {
            L.e(ex.getMessage());
        }

        try {
            CalendarUtil.insertEvent(this, recording.getName(), recording.getFileName(), CalendarUtil.dateToCalendar(recording.getStartTime()), CalendarUtil.dateToCalendar(recording.getEndTime()));
        } catch (Exception ex) {
            L.e(ex.getMessage());
        }

        ContentResolver contentResolver = getContentResolver();
        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
    }


    void hideButtons() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.bottomLayout);
        ll.startAnimation(AnimationUtils.loadAnimation(this, R.anim.vanish));
        ll.setVisibility(View.GONE);
        ll = (LinearLayout) findViewById(R.id.recordingFeedbackLayout);
        ll.setVisibility(View.VISIBLE);
    }

    void showButtons() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.recordingFeedbackLayout);
        ll.setVisibility(View.GONE);
        ll = (LinearLayout) findViewById(R.id.bottomLayout);
        ll.startAnimation(AnimationUtils.loadAnimation(this, R.anim.vanish));
        ll.setVisibility(View.VISIBLE);
    }


    Runnable mUpdateTimeTask = new Runnable() {
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
                mHandler.postDelayed(mUpdateTimeTask, 1000);
            }
        }
    };

    Runnable mSoundLevel = new Runnable() {
        public void run() {
            if (mIsRecording) {
                int amplitude = mRecorder.getMaxAmplitude();
                System.out.println(amplitude);
                mProgressBar.setProgress(amplitude);
                mHandler.postDelayed(mSoundLevel, 100);
            }
        }
    };


}
