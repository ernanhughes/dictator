package com.banba.dictator.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.banba.dictator.R;
import com.banba.dictator.Util;
import com.banba.dictator.activity.ApacheActivity;
import com.banba.dictator.activity.SearchActivity;
import com.banba.dictator.event.Record;
import com.banba.dictator.event.SectionEvent;
import com.banba.dictator.lib.ColorUtil;
import com.banba.dictator.lib.activity.ExceptionActivity;
import com.banba.dictator.lib.util.DateTimeUtil;
import com.banba.dictator.service.RecordService;

import de.greenrobot.event.EventBus;

/**
 * Created by Ernan on 02/03/14.
 * Copyrite Banba Inc. 2013.
 */
public class MainFragment extends Fragment implements View.OnClickListener {
    ImageButton mRecordButton;
    TextView mRecordText;
    boolean mIsRecording = false;
    Handler mHandler = new Handler();
    private ProgressBar mProgressBar;
    ViewSwitcher viewSwitcher;
    LinearLayout firstView, secondView;
    Animation inAnimRight, outAnimLeft, inAnimLeft, outAnimRight;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        inAnimRight = AnimationUtils.loadAnimation(getActivity(),
                R.anim.grow_from_bottom);
        outAnimLeft = AnimationUtils.loadAnimation(getActivity(),
                R.anim.grow_from_top);
        inAnimLeft = AnimationUtils.loadAnimation(getActivity(),
                R.anim.fragment_slide_left_enter);
        outAnimRight = AnimationUtils.loadAnimation(getActivity(),
                R.anim.fragment_slide_left_exit);

        mRecordButton = (ImageButton) rootView.findViewById(R.id.iconRecord);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsRecording) {
                    viewSwitcher.setInAnimation(inAnimLeft);
                    viewSwitcher.setOutAnimation(outAnimRight);
                    if (viewSwitcher.getCurrentView() != secondView) {
                        viewSwitcher.showNext();
                    }
                    mRecordButton.setImageResource(R.drawable.record_stop_half);

                    EventBus.getDefault().post(new Record(Record.Action.Start));
                    mIsRecording = true;
                } else {
                    mIsRecording = false;
                    viewSwitcher.setInAnimation(inAnimLeft);
                    viewSwitcher.setOutAnimation(outAnimRight);
                    if (viewSwitcher.getCurrentView() != firstView) {
                        viewSwitcher.showPrevious();
                    }
                    mRecordButton.setImageResource(R.drawable.record_start_half);
                    getActivity().getActionBar().setTitle(R.string.app_name);
                    mRecordText.setText("RECORD");
                    EventBus.getDefault().post(new Record(Record.Action.Stop));
                }
            }
        });
        mRecordText = (TextView) rootView.findViewById(R.id.textRecord);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.viewSwitcher);
        firstView = (LinearLayout) rootView.findViewById(R.id.bottomLayout);
        secondView = (LinearLayout) rootView.findViewById(R.id.recordingFeedbackLayout);

        Intent ps = new Intent(getActivity(), RecordService.class);
        getActivity().startService(ps);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(RecordService.BROADCAST_ACTION));
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
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
            Intent search = new Intent(getActivity(), SearchActivity.class);
            startActivity(search);
            return true;
        }
        if (id == R.id.menu_item_ex) {
            Intent search = new Intent(getActivity(), ExceptionActivity.class);
            startActivity(search);
            return true;
        }
        if (id == R.id.menu_item_apache) {
            Intent search = new Intent(getActivity(), ApacheActivity.class);
            startActivity(search);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            int amplitude = b.getInt(Util.AMPLITUDE);
            ;
            mProgressBar.setProgress(amplitude);
            long totalTime = b.getLong(Util.DURATION);
            Spanned text = Html.fromHtml("<font color=\"#800000\">Recording " + DateTimeUtil.formatTime(totalTime / 1000) + "</font>");
            getActivity().getActionBar().setTitle(text);
            mRecordText.setText(text);
        }
    };

    public void onDestroy() {
        EventBus.getDefault().post(new Record(Record.Action.Stop));
    }

}
