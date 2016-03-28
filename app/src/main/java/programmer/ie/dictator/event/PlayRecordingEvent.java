package programmer.ie.dictator.event;

import programmer.ie.dictator.data.Recording;

public class PlayRecordingEvent {
    public Recording recording;

    public PlayRecordingEvent(Recording recording) {
        this.recording = recording;
    }
}
