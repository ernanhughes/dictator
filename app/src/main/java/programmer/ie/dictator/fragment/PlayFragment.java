package programmer.ie.dictator.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import programmer.ie.dictator.R;
import programmer.ie.dictator.Util;
import programmer.ie.dictator.event.PlayEvent;
import programmer.ie.dictator.service.PlayService;
import programmer.ie.dictator.util.DateTimeUtil;
import programmer.ie.dictator.view.VisualizerView;
import programmer.ie.dictator.view.visualizer.VisualiserFactory;

public class PlayFragment extends Fragment {
    TextView mStartTime;
    TextView mEndTime;
    VisualizerView mVisualizer;
    SeekBar mSeekBar;
    private PlayService playerService;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            int current = b.getInt(Util.POSITION);
            mStartTime.setText(DateTimeUtil.shortTimeFormat(current));
            int finalTime = b.getInt(Util.DURATION);
            mEndTime.setText(DateTimeUtil.shortTimeFormat(finalTime));
            mSeekBar.setProgress(current);
            mSeekBar.setMax(finalTime);
        }
    };
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            PlayService.PlayBinder playerBinder = (PlayService.PlayBinder) binder;
            playerService = playerBinder.getService();
            mVisualizer.link(playerService.mPlayer);
            VisualiserFactory.addCircleBarRenderer(getActivity(), mVisualizer);
            VisualiserFactory.addBarGraphRenderers(getActivity(), mVisualizer);
        }

        public void onServiceDisconnected(ComponentName className) {
            playerService = null;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getActivity(), PlayService.class);
        getActivity().bindService(intent, mConnection,
                Context.BIND_AUTO_CREATE);
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(PlayService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(mConnection);
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().post(new PlayEvent(PlayEvent.Action.Stop, new Bundle()));
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar1);
        mEndTime = (TextView) rootView.findViewById(R.id.endTimeText);
        mStartTime = (TextView) rootView.findViewById(R.id.startTimeText);

        Intent ps = new Intent(getActivity(), PlayService.class);
        getActivity().startService(ps);

        Intent i = getActivity().getIntent();
        final String sUri = i.getExtras().getString(Util.FILE_NAME);
        EventBus.getDefault().post(new PlayEvent(PlayEvent.Action.Start, i.getExtras()));

        TextView songName = (TextView) rootView.findViewById(R.id.recordingTitle);
        songName.setText(Util.getShortName(sUri));

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Bundle b = new Bundle();
                    b.putInt(Util.POSITION, progress);
                    EventBus.getDefault().post(new PlayEvent(PlayEvent.Action.Seek, b));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final ImageView playButton = (ImageView) rootView.findViewById(R.id.playButton);
        playButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.play_selected));
        final ImageView pauseButton = (ImageView) rootView.findViewById(R.id.pauseButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PlayEvent(PlayEvent.Action.Resume, new Bundle()));
                playButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.play_selected));
                pauseButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pause));
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PlayEvent(PlayEvent.Action.Pause, new Bundle()));
                playButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.play));
                pauseButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pause_selected));
            }
        });
        final ImageView rewindButton = (ImageView) rootView.findViewById(R.id.rewindButton);
        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PlayEvent(PlayEvent.Action.Rewind, new Bundle()));
            }
        });
        final ImageView forwardButton = (ImageView) rootView.findViewById(R.id.forwardButton);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PlayEvent(PlayEvent.Action.Forward, new Bundle()));
            }
        });
        final ImageView restartButton = (ImageView) rootView.findViewById(R.id.restartButton);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PlayEvent(PlayEvent.Action.Restart, new Bundle()));
            }
        });
        mVisualizer = (VisualizerView) rootView.findViewById(R.id.visualizerView);

        return rootView;
    }
}
