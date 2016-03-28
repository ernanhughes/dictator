package programmer.ie.dictator.event;

public class RecordEvent {
    public Action action;

    public RecordEvent(Action action) {
        this.action = action;
    }

    public enum Action {
        Start,
        Stop
    }
}
