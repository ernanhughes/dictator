package com.banba.dictator.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.banba.dictator.R;
import com.banba.dictator.Util;
import com.banba.dictator.activity.SearchActivity;
import com.banba.dictator.event.RecordEvent;
import com.banba.dictator.event.SectionEvent;
import com.banba.dictator.lib.util.DateTimeUtil;
import com.banba.dictator.service.RecordService;
import com.banba.dictator.view.AudioEventView;

import de.greenrobot.event.EventBus;

/**
 * Created by Ernan on 02/03/14.
 * Copyrite Banba Inc. 2013.
 */
public class MainFragment extends Fragment implements View.OnClickListener {
    TextView mRecordText;
    ViewSwitcher viewSwitcher;
    AudioEventView eventView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final ImageView mRecordButton = (ImageButton) rootView.findViewById(R.id.iconRecord);

        final View firstView = rootView.findViewById(R.id.mainLayout);
        final View secondView = rootView.findViewById(R.id.recordingFeedbackLayout);

        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecordButton.setImageResource(R.drawable.record_start_half_selected);
                if (viewSwitcher.getCurrentView() != secondView) {
                    viewSwitcher.showNext();
                }

                EventBus.getDefault().post(new RecordEvent(RecordEvent.Action.Start));
            }
        });
        final ImageView mStopRecordButton = (ImageButton) rootView.findViewById(R.id.iconStopRecording);
        mStopRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewSwitcher.getCurrentView() != firstView) {
                    viewSwitcher.showPrevious();
                }
                mRecordButton.setImageResource(R.drawable.record_start_half);
                getActivity().getActionBar().setTitle(R.string.app_name);
                EventBus.getDefault().post(new RecordEvent(RecordEvent.Action.Stop));
            }
        });

        mRecordText = (TextView) rootView.findViewById(R.id.textRecord);
        viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.viewSwitcher);
        final Animation inAnimRight = AnimationUtils.loadAnimation(getActivity(),
                R.anim.grow_from_bottom);
//        final Animation outAnimLeft = AnimationUtils.loadAnimation(getActivity(),
//                R.anim.grow_from_top);
//        final Animation inAnimLeft = AnimationUtils.loadAnimation(getActivity(),
//                R.anim.fragment_slide_left_enter);
        final Animation outAnimRight = AnimationUtils.loadAnimation(getActivity(),
                R.anim.fragment_slide_left_exit);
        viewSwitcher.setInAnimation(inAnimRight);
        viewSwitcher.setOutAnimation(outAnimRight);

        eventView = (AudioEventView) rootView.findViewById(R.id.eventView);

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
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            int amplitude = b.getInt(Util.AMPLITUDE);
            eventView.addReading(amplitude);
            long totalTime = b.getLong(Util.DURATION);
            Spanned text = Html.fromHtml("<font color=\"#CF000F\">Recording " + DateTimeUtil.formatTime(totalTime / 1000) + "</font>");
            getActivity().getActionBar().setTitle(text);
            mRecordText.setText(text);
        }
    };

    public void onDestroy() {
        EventBus.getDefault().post(new RecordEvent(RecordEvent.Action.Stop));
        super.onDestroy();
    }
}
