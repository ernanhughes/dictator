package com.banba.dictator.event;

import com.banba.dictator.data.Recording;

/**
 * Created by Ernan on 26/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class PlayRecordingEvent {
    public Recording recording;

    public PlayRecordingEvent(Recording recording) {
        this.recording = recording;
    }
}
