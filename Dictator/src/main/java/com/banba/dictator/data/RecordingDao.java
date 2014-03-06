package com.banba.dictator.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table RECORDING.
 */
public class RecordingDao extends AbstractDao<Recording, Long> {

    public static final String TABLENAME = "RECORDING";

    /**
     * Properties of entity Recording.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property FileName = new Property(2, String.class, "fileName", false, "FILE_NAME");
        public final static Property FileSize = new Property(3, Long.class, "fileSize", false, "FILE_SIZE");
        public final static Property RecordingData = new Property(4, byte[].class, "recordingData", false, "RECORDING_DATA");
        public final static Property StartTime = new Property(5, java.util.Date.class, "startTime", false, "START_TIME");
        public final static Property EndTime = new Property(6, java.util.Date.class, "endTime", false, "END_TIME");
    }


    public RecordingDao(DaoConfig config) {
        super(config);
    }

    public RecordingDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'RECORDING' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'NAME' TEXT NOT NULL ," + // 1: name
                "'FILE_NAME' TEXT," + // 2: fileName
                "'FILE_SIZE' INTEGER," + // 3: fileSize
                "'RECORDING_DATA' BLOB," + // 4: recordingData
                "'START_TIME' INTEGER," + // 5: startTime
                "'END_TIME' INTEGER);"); // 6: endTime
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'RECORDING'";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, Recording entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getName());

        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(3, fileName);
        }

        Long fileSize = entity.getFileSize();
        if (fileSize != null) {
            stmt.bindLong(4, fileSize);
        }

        byte[] recordingData = entity.getRecordingData();
        if (recordingData != null) {
            stmt.bindBlob(5, recordingData);
        }

        java.util.Date startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindLong(6, startTime.getTime());
        }

        java.util.Date endTime = entity.getEndTime();
        if (endTime != null) {
            stmt.bindLong(7, endTime.getTime());
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
    public Recording readEntity(Cursor cursor, int offset) {
        Recording entity = new Recording( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.getString(offset + 1), // name
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // fileName
                cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // fileSize
                cursor.isNull(offset + 4) ? null : cursor.getBlob(offset + 4), // recordingData
                cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // startTime
                cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)) // endTime
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, Recording entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.getString(offset + 1));
        entity.setFileName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFileSize(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setRecordingData(cursor.isNull(offset + 4) ? null : cursor.getBlob(offset + 4));
        entity.setStartTime(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setEndTime(cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(Recording entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(Recording entity) {
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
