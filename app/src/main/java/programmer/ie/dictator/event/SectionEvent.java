package programmer.ie.dictator.event;

public class SectionEvent {
    public static final String RECORD = "RecordEvent";
    public static final String MANAGE = "Manage";
    public static final String CALENDAR = "Calendar";
    public static final String PLAY_LIST = "PlayList";
    public static final String SEARCH = "Search";
    public static final String HELP = "Help";
    public static final String ABOUT = "About";

    public String section;

    public SectionEvent(String section) {
        this.section = section;
    }
}
