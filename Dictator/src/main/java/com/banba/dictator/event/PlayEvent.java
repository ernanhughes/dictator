package com.banba.dictator.event;

import android.os.Bundle;

/**
 * Created by Ernan on 02/03/14.
 * Copyrite Banba Inc. 2013.
 */
public class PlayEvent {
    public enum Action {
        Start,
        Seek,
        Forward,
        Rewind,
        Restart,
        Pause,
        Resume,
        Stop
    }

    public Action action;
    public Bundle bundle = null;

    public PlayEvent(Action action, Bundle bundle) {
        this.action = action;
        this.bundle = bundle;
    }

}
