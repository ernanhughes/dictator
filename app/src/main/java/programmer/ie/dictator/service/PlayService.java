package programmer.ie.dictator.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import de.greenrobot.event.EventBus;
import programmer.ie.dictator.Util;
import programmer.ie.dictator.event.PlayEvent;
import programmer.ie.dictator.util.L;

public class PlayService extends Service {

    public static final String BROADCAST_ACTION = "programmer.ie.dictator.playservice";
    static final int SKIP_TIME = 5000;
    final IBinder mBinder = new PlayBinder();
    private final Handler handler = new Handler();
    public MediaPlayer mPlayer;
    Intent intent;
    private Runnable outGoingHandler = new Runnable() {
        public void run() {
            updateRecordingInfo();
            handler.postDelayed(this, 200);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.removeCallbacks(outGoingHandler);
        handler.postDelayed(outGoingHandler, 100);
        return super.onStartCommand(intent, flags, startId);
    }

    public MediaPlayer getMediaPlayer() {
        return mPlayer;
    }

    public void updateRecordingInfo() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            Bundle b = new Bundle();
            b.putInt(Util.DURATION, mPlayer.getDuration());
            b.putInt(Util.POSITION, mPlayer.getCurrentPosition());
            intent.putExtras(b);
            sendBroadcast(intent);
        }
    }

    public void onEvent(PlayEvent event) {
        switch (event.action) {
            case Start:
                L.d("Starting player " + mPlayer != null ? "!!! warning player not null" : "");
                String fileName = event.bundle.getString(Util.FILE_NAME);
                Uri uri = Uri.parse(fileName);
                mPlayer = MediaPlayer.create(this, uri);
                mPlayer.start();
                break;
            case Stop:
                L.d("Stopping player " + mPlayer == null ? "!!! warning player is null on stop." : "");
                if (mPlayer != null) {
                    mPlayer.reset();
                    mPlayer.release();
                    mPlayer = null;
                }
                break;
            case Pause:
                L.d("Pausing player ");
                if (mPlayer != null) {
                    mPlayer.pause();
                }
                break;
            case Resume:
                L.d("Resumeing player ");
                if (mPlayer != null) {
                    mPlayer.start();
                }
                break;
            case Seek:
                L.d("Seeking position ");
                int newPosition = event.bundle.getInt(Util.POSITION);
                if (mPlayer != null) {
                    mPlayer.seekTo(newPosition);
                }
                break;
            case Forward:
                L.d("Skipping ");
                if (mPlayer != null) {
                    int position = mPlayer.getCurrentPosition() + SKIP_TIME;
                    if (position > mPlayer.getDuration()) {
                        mPlayer.seekTo(0);
                    } else {
                        mPlayer.seekTo(position);
                    }
                }
                break;
            case Rewind:
                L.d("Rewinding ");
                if (mPlayer != null) {
                    int position = mPlayer.getCurrentPosition() - SKIP_TIME;
                    if (position < 0) {
                        mPlayer.seekTo(0);
                    } else {
                        mPlayer.seekTo(position);
                    }
                }
                break;
            case Restart:
                L.d("Rewinding ");
                if (mPlayer != null) {
                    mPlayer.seekTo(0);
                    mPlayer.start();
                }
                break;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }
}
