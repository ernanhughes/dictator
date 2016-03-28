package programmer.ie.dictator.event;

import android.os.Bundle;

public class PlayEvent {
    public Action action;
    public Bundle bundle = null;

    public PlayEvent(Action action, Bundle bundle) {
        this.action = action;
        this.bundle = bundle;
    }

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

}
