package com.banba.dictator.event;

/**
 * Created by Ernan on 02/03/14.
 * Copyrite Banba Inc. 2013.
 */
public class Record {
    public enum Action {
        Start,
        Stop
    }

    ;

    public Action action;

    public Record(Action action) {
        this.action = action;
    }
}
