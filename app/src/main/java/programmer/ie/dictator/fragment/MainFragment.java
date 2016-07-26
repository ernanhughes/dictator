package programmer.ie.dictator.fragment;

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

import de.greenrobot.event.EventBus;
import programmer.ie.dictator.R;
import programmer.ie.dictator.Util;
import programmer.ie.dictator.activity.SearchActivity;
import programmer.ie.dictator.event.RecordEvent;
import programmer.ie.dictator.event.SectionEvent;
import programmer.ie.dictator.service.RecordService;
import programmer.ie.dictator.util.DateTimeUtil;
import programmer.ie.dictator.view.Visualizer;

public class MainFragment extends Fragment implements View.OnClickListener {
    TextView mRecordText;
    Visualizer eventView;
    boolean isRecording = false;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            int amplitude = b.getInt(Util.AMPLITUDE);
//            eventView.addReading(amplitude);
            long totalTime = b.getLong(Util.DURATION);
            Spanned text = Html.fromHtml("<font color=\"#CF000F\">Recording " + DateTimeUtil.formatTime(totalTime / 1000) + "</font>");
            getActivity().getActionBar().setTitle(text);
            mRecordText.setText(text);
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final ImageView mRecordButton = (ImageButton) rootView.findViewById(R.id.iconRecord);
        if (savedInstanceState != null && savedInstanceState.getSerializable(Util.RECORDING) != null) {
            isRecording = savedInstanceState.getBoolean(Util.RECORDING);
        }

        final ViewSwitcher viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.viewSwitcher);

        final Animation inAnimRight = AnimationUtils.loadAnimation(getActivity(),
                R.anim.grow_from_bottom);
        final Animation outAnimRight = AnimationUtils.loadAnimation(getActivity(),
                R.anim.fragment_slide_left_exit);
        viewSwitcher.setInAnimation(inAnimRight);
        viewSwitcher.setOutAnimation(outAnimRight);
        final View firstView = rootView.findViewById(R.id.mainLayout);
        final View secondView = rootView.findViewById(R.id.recordingFeedbackLayout);

        eventView = (Visualizer) rootView.findViewById(R.id.eventView);
        if (isRecording) {
            if (viewSwitcher.getCurrentView() != secondView) {
                viewSwitcher.showNext();
            }
            eventView.startListening();
        } else {
            if (viewSwitcher.getCurrentView() != firstView) {
                viewSwitcher.showPrevious();
            }
            eventView.startListening();
        }

        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewSwitcher.getCurrentView() != secondView) {
                    viewSwitcher.showNext();
                }
                isRecording = true;
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
                isRecording = false;
            }
        });

        mRecordText = (TextView) rootView.findViewById(R.id.textRecord);
        eventView = (Visualizer) rootView.findViewById(R.id.eventView);

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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(Util.RECORDING, isRecording);
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
}
