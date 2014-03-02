package com.banba.dictator.event;

/**
 * Created by Ernan on 02/03/14.
 * Copyrite Banba Inc. 2013.
 */
public class Play {
    public enum Action {
        Start,
        Seek,
        Forward,
        Rewind,
        Restart,
        Stop
    }

    ;

    public Action action;
    int position = 0;

    public Play(Action action) {
        this.action = action;
    }

    public Play(int position) {
        this.action = Action.Seek;
        this.position = position;
    }

}
