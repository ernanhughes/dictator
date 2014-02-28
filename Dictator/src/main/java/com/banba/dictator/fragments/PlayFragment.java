package com.banba.dictator.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.banba.dictator.R;
import com.banba.dictator.Util;
import com.banba.dictator.ui.util.DateTimeUtil;
import com.banba.dictator.visualizer.VisualiserFactory;
import com.banba.dictator.visualizer.VisualizerView;

import rx.util.functions.Func0;
import rx.util.functions.Func1;

import static android.media.MediaPlayer.create;

/**
 * Created by Ernan on 26/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class PlayFragment extends Fragment {
    MediaPlayer mPlayer;
    static final int SKIP_TIME = 5000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        Intent i = getActivity().getIntent();
        final String sUri = i.getExtras().getString(Util.FILE_NAME);


        if (initPlayer(sUri)) {
            final VisualizerView visualizerView = (VisualizerView) rootView.findViewById(R.id.visualizerView);
            visualizerView.link(mPlayer);
            VisualiserFactory.addCircleBarRenderer(visualizerView);
            VisualiserFactory.addBarGraphRenderers(visualizerView);

            int finalTime = mPlayer.getDuration();
            final TextView endTimeField = (TextView) rootView.findViewById(R.id.endTimeText);
            endTimeField.setText(DateTimeUtil.shortTimeFormat(finalTime));
            final SeekBar seekbar = (SeekBar) rootView.findViewById(R.id.seekBar1);
            seekbar.setMax(finalTime);

            TextView songName = (TextView) rootView.findViewById(R.id.recordingTitle);
            songName.setText(Util.getShortName(sUri));
            final TextView startTimeField = (TextView) rootView.findViewById(R.id.startTimeText);

            final Func1<Integer, Void> updatePosition = new Func1<Integer, Void>() {
                @Override
                public Void call(Integer newPosition) {
                    if (mPlayer != null) {
                        mPlayer.seekTo(newPosition);
                        seekbar.setProgress(newPosition);
                        startTimeField.setText(DateTimeUtil.shortTimeFormat(newPosition));
                    }
                    return null;
                }
            };
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        updatePosition.call(progress);
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
            final Func0 play = new Func0() {
                @Override
                public Object call() {
                    if (mPlayer != null) {
                        final Handler handler = new Handler();
                        new Runnable() {
                            public void run() {
                                if (mPlayer != null) {
                                    int currentPosition = mPlayer.getCurrentPosition();
                                    startTimeField.setText(DateTimeUtil.shortTimeFormat(currentPosition));
                                    seekbar.setProgress(currentPosition);
                                    handler.postDelayed(this, 100);
                                }
                            }
                        }.run();
                        if (!mPlayer.isPlaying()) {
                            mPlayer.start();
                            // mMediaplayer.seekTo(0) should be called whenever a Track has just been prepared
                            // and is being started. This workaround is needed because of a bug in Android 4.4.
                            if (mPlayer.getCurrentPosition() == 0) {
                                mPlayer.seekTo(0);
                            }

                        }
                        playButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.play_pressed));
                        pauseButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pause));
                    }
                    return null;
                }
            };

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play.call();
                }
            });
            pauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPlayer != null && mPlayer.isPlaying()) {
                        mPlayer.pause();
                        playButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.play));
                        pauseButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pause_pressed));
                    }
                }
            });

            final ImageView rewindButton = (ImageView) rootView.findViewById(R.id.rewindButton);
            rewindButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int current = mPlayer.getCurrentPosition();
                    updatePosition.call(current - SKIP_TIME);
                }
            });
            final ImageView forwardButton = (ImageView) rootView.findViewById(R.id.forwardButton);
            forwardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int current = mPlayer.getCurrentPosition();
                    updatePosition.call(current + SKIP_TIME);
                }
            });
            final ImageView restartButton = (ImageView) rootView.findViewById(R.id.restartButton);
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePosition.call(0);
                }
            });

            play.call();
        }
        return rootView;
    }

    boolean initPlayer(String filePath) {
        if (null != filePath) {
            final Uri fileToPlay = Uri.parse(filePath);
            mPlayer = create(getActivity(), fileToPlay);
            if (null != mPlayer) {
                mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Toast.makeText(getActivity(), "There was an error playing file " + fileToPlay, Toast.LENGTH_LONG);
                        mPlayer = create(getActivity(), fileToPlay);
                        return false;
                    }
                });
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null
                && savedInstanceState.containsKey(Util.POSITION)) {
            if (mPlayer != null) {
                mPlayer.seekTo(savedInstanceState.getInt(Util.POSITION));
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPlayer != null) {
            outState.putInt(Util.POSITION, mPlayer.getCurrentPosition());
        }
    }

}
