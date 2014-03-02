package com.banba.dictator.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.banba.dictator.Util;
import com.banba.dictator.lib.L;
import com.banba.dictator.visualizer.VisualiserFactory;
import com.banba.dictator.visualizer.VisualizerView;

import java.lang.ref.WeakReference;

/**
 * Created by Ernan on 27/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class PlayService extends Service {

    public MediaPlayer mPlayer;
    static final int SKIP_TIME = 5000;

    public static final String MSG_MESSAGE = "MESSAGE_MSG";
    public static final String BROADCAST_ACTION = "com.banba.dictator.playservice";

    final IBinder mBinder = new PlayBinder();
    private final Handler handler = new Handler();
    Intent intent;

    public static final int MSG_START = 1;
    public static final int MSG_STOP = 2;
    public static final int MSG_PAUSE = 3;
    public static final int MSG_RESUME = 4;
    public static final int MSG_SEEK_TO = 5;
    public static final int MSG_FORWARD = 6;
    public static final int MSG_REWIND = 7;
    public static final int MSG_RESTART = 8;
    public static final int MSG_LINK_VISUALISER = 9;

    protected final Messenger mServerMessenger = new Messenger(new IncomingHandler(this));

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message m = (Message) intent.getExtras().get(MSG_MESSAGE);
        try {
            mServerMessenger.send(m);
        } catch (RemoteException e) {
            L.e(e.getMessage());
        }
        handler.removeCallbacks(outGoingHandler);
        handler.postDelayed(outGoingHandler, 100);
        return super.onStartCommand(intent, flags, startId);
    }

    public MediaPlayer getMediaPlayer() {
        return mPlayer;
    }

    public void updateRecordingInfo() {
        Bundle b = new Bundle();
        b.putInt(Util.DURATION, mPlayer.getDuration());
        b.putInt(Util.POSITION, mPlayer.getCurrentPosition());
        intent.putExtras(b);
        sendBroadcast(intent);
    }

    private Runnable outGoingHandler = new Runnable() {
        public void run() {
            if (mPlayer != null && mPlayer.isPlaying()) {
                updateRecordingInfo();
                handler.postDelayed(this, 200);
            }
        }
    };

    protected static class IncomingHandler extends Handler {
        private WeakReference<PlayService> mtarget;

        IncomingHandler(PlayService target) {
            mtarget = new WeakReference<PlayService>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            final PlayService target = mtarget.get();
            Bundle bundle = msg.getData();
            L.i("message received: " + msg);
            switch (msg.what) {
                case MSG_START:
                    L.d("Starting player " + target.mPlayer != null ? "!!! warning player not null" : "");
                    String fileName = bundle.getString(Util.FILE_NAME);
                    Uri uri = Uri.parse(fileName);
                    target.mPlayer = MediaPlayer.create(target, uri);
                    target.mPlayer.start();
                    break;
                case MSG_STOP:
                    L.d("Stopping player " + target.mPlayer == null ? "!!! warning player is null on stop." : "");
                    if (target.mPlayer != null) {
                        target.mPlayer.reset();
                        target.mPlayer.release();
                        target.mPlayer = null;
                    }
                    break;
                case MSG_PAUSE:
                    L.d("Pausing player ");
                    if (target.mPlayer != null) {
                        target.mPlayer.pause();
                    }
                    break;
                case MSG_RESUME:
                    L.d("Resumeing player ");
                    if (target.mPlayer != null) {
                        target.mPlayer.start();
                    }
                    break;
                case MSG_SEEK_TO:
                    L.d("Seeking position ");
                    int newPosition = bundle.getInt(Util.POSITION);
                    if (target.mPlayer != null) {
                        target.mPlayer.seekTo(newPosition);
                    }
                    break;
                case MSG_FORWARD:
                    L.d("Skipping ");
                    if (target.mPlayer != null) {
                        int position = target.mPlayer.getCurrentPosition() + SKIP_TIME;
                        if (position > target.mPlayer.getDuration()) {
                            target.mPlayer.seekTo(0);
                        } else {
                            target.mPlayer.seekTo(position);
                        }
                    }
                    break;
                case MSG_REWIND:
                    L.d("Rewinding ");
                    if (target.mPlayer != null) {
                        int position = target.mPlayer.getCurrentPosition() - SKIP_TIME;
                        if (position < 0) {
                            target.mPlayer.seekTo(0);
                        } else {
                            target.mPlayer.seekTo(position);
                        }
                    }
                    break;
                case MSG_RESTART:
                    L.d("Rewinding ");
                    if (target.mPlayer != null) {
                        target.mPlayer.seekTo(0);
                        target.mPlayer.start();
                    }
                    break;
                case MSG_LINK_VISUALISER:
                    L.d("Rewinding ");

                    if (target.mPlayer != null) {
                        VisualizerView visualizerView = (VisualizerView) bundle.get(Util.VISUALISER);
                        visualizerView.link(target.mPlayer);
                        VisualiserFactory.addCircleBarRenderer(visualizerView);
                        VisualiserFactory.addBarGraphRenderers(visualizerView);
                    }
                    break;


            }
        }
    }

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
