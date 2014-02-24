package com.banba.dictator.event;

/**
 * Created by Ernan on 23/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class SectionEvent {
    public static final String RECORD = "Record";
    public static final String MANAGE = "Manage";
    public static final String ENTRIES = "Entries";
    public static final String SEARCH = "Search";
    public static final String HELP = "Help";
    public static final String ABOUT = "About";

    public String section;

    public SectionEvent(String section) {
        this.section = section;
    }
}