package com.banba.dictator.data;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table NOTE.
 */
public class Note {

    private Long id;
    /** Not-null value. */
    private String name;
    /** Not-null value. */
    private String recording_name;
    /** Not-null value. */
    private String header;
    private String comment;
    private java.util.Date note_date;

    public Note() {
    }

    public Note(Long id) {
        this.id = id;
    }

    public Note(Long id, String name, String recording_name, String header, String comment, java.util.Date note_date) {
        this.id = id;
        this.name = name;
        this.recording_name = recording_name;
        this.header = header;
        this.comment = comment;
        this.note_date = note_date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    /** Not-null value. */
    public String getRecording_name() {
        return recording_name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setRecording_name(String recording_name) {
        this.recording_name = recording_name;
    }

    /** Not-null value. */
    public String getHeader() {
        return header;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setHeader(String header) {
        this.header = header;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public java.util.Date getNote_date() {
        return note_date;
    }

    public void setNote_date(java.util.Date note_date) {
        this.note_date = note_date;
    }

}
