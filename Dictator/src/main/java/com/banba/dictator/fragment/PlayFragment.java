package com.banba.dictator.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.banba.dictator.R;
import com.banba.dictator.Util;
import com.banba.dictator.lib.util.DateTimeUtil;
import com.banba.dictator.service.PlayService;
import com.banba.dictator.visualizer.VisualiserFactory;
import com.banba.dictator.visualizer.VisualizerView;

/**
 * Created by Ernan on 26/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class PlayFragment extends Fragment {
    private PlayService playerService;
    TextView endTimeField;
    TextView startTimeField;
    VisualizerView visualizerView;
    SeekBar seekbar;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            int finalTime = b.getInt(Util.DURATION);
            endTimeField.setText(DateTimeUtil.shortTimeFormat(finalTime));
            int current = b.getInt(Util.POSITION);
            startTimeField.setText(DateTimeUtil.shortTimeFormat(current));
            seekbar.setProgress(current);
            seekbar.setMax(finalTime);
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

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            PlayService.PlayBinder playerBinder = (PlayService.PlayBinder) binder;
            playerService = playerBinder.getService();
            visualizerView.link(playerService.mPlayer);
            VisualiserFactory.addCircleBarRenderer(visualizerView);
            VisualiserFactory.addBarGraphRenderers(visualizerView);
        }

        public void onServiceDisconnected(ComponentName className) {
            playerService = null;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        seekbar = (SeekBar) rootView.findViewById(R.id.seekBar1);
        endTimeField = (TextView) rootView.findViewById(R.id.endTimeText);
        startTimeField = (TextView) rootView.findViewById(R.id.startTimeText);

        Intent i = getActivity().getIntent();
        final String sUri = i.getExtras().getString(Util.FILE_NAME);
        sendMessage(PlayService.MSG_START, i.getExtras());

        TextView songName = (TextView) rootView.findViewById(R.id.recordingTitle);
        songName.setText(Util.getShortName(sUri));

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Bundle b = new Bundle();
                    b.putInt(Util.POSITION, progress);
                    sendMessage(PlayService.MSG_SEEK_TO, b);
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
        final ImageView pauseButton = (ImageView) rootView.findViewById(R.id.pauseButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(PlayService.MSG_RESUME, new Bundle());
                playButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.play));
                pauseButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pause_pressed));
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(PlayService.MSG_PAUSE, new Bundle());
                playButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.play));
                pauseButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pause_pressed));
            }
        });

        final ImageView rewindButton = (ImageView) rootView.findViewById(R.id.rewindButton);
        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(PlayService.MSG_REWIND, new Bundle());
            }
        });
        final ImageView forwardButton = (ImageView) rootView.findViewById(R.id.forwardButton);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(PlayService.MSG_FORWARD, new Bundle());
            }
        });
        final ImageView restartButton = (ImageView) rootView.findViewById(R.id.restartButton);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(PlayService.MSG_RESTART, new Bundle());
            }
        });


        visualizerView = (VisualizerView) rootView.findViewById(R.id.visualizerView);


        return rootView;
    }

    void sendMessage(int type, Bundle data) {
        Intent ps = new Intent(getActivity(), PlayService.class);
        Message message = new Message();
        message.what = type;
        message.setData(data);
        ps.putExtra(PlayService.MSG_MESSAGE, message);
        getActivity().startService(ps);
    }

}
