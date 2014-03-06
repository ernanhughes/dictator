package com.banba.dictator.event;

/**
 * Created by Ernan on 23/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class AboutEvent {

    enum Section {
        Help,
        ContactUs,
        Rate,
        Bug,
        Apps,

    }

    public static final String RECORD = "RecordEvent";
    public static final String MANAGE = "Manage";
    public static final String CALENDAR = "Calendar";
    public static final String PLAY_LIST = "PlayList";
    public static final String SEARCH = "Search";
    public static final String HELP = "Help";
    public static final String ABOUT = "About";

    public String section;

    public AboutEvent(String section) {
        this.section = section;
    }
}
