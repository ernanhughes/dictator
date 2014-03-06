package com.banba.dictator.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table NOTE.
 */
public class NoteDao extends AbstractDao<Note, Long> {

    public static final String TABLENAME = "NOTE";

    /**
     * Properties of entity Note.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Recording_name = new Property(2, String.class, "recording_name", false, "RECORDING_NAME");
        public final static Property Header = new Property(3, String.class, "header", false, "HEADER");
        public final static Property Comment = new Property(4, String.class, "comment", false, "COMMENT");
        public final static Property Note_date = new Property(5, java.util.Date.class, "note_date", false, "NOTE_DATE");
    }


    public NoteDao(DaoConfig config) {
        super(config);
    }

    public NoteDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'NOTE' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'NAME' TEXT NOT NULL ," + // 1: name
                "'RECORDING_NAME' TEXT NOT NULL ," + // 2: recording_name
                "'HEADER' TEXT NOT NULL ," + // 3: header
                "'COMMENT' TEXT," + // 4: comment
                "'NOTE_DATE' INTEGER);"); // 5: note_date
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_NOTE_HEADER ON NOTE" +
                " (HEADER);");
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'NOTE'";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, Note entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getName());
        stmt.bindString(3, entity.getRecording_name());
        stmt.bindString(4, entity.getHeader());

        String comment = entity.getComment();
        if (comment != null) {
            stmt.bindString(5, comment);
        }

        java.util.Date note_date = entity.getNote_date();
        if (note_date != null) {
            stmt.bindLong(6, note_date.getTime());
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    public Note readEntity(Cursor cursor, int offset) {
        Note entity = new Note( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.getString(offset + 1), // name
                cursor.getString(offset + 2), // recording_name
                cursor.getString(offset + 3), // header
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // comment
                cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)) // note_date
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, Note entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.getString(offset + 1));
        entity.setRecording_name(cursor.getString(offset + 2));
        entity.setHeader(cursor.getString(offset + 3));
        entity.setComment(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setNote_date(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(Note entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(Note entity) {
        if (entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

}
