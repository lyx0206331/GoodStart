package com.adrian.goodstart.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;


import com.adrian.goodstart.pojo.Cmd;

import java.sql.Blob;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by adrian on 16-3-20.
 */
public class CmdDao extends AbstractDao<Cmd, Long> {

    public static final String TABLENAME = "CMD";

    /**
     * Properties of entity Note.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property cmd = new Property(1, Blob.class, "cmd", false, "CMD");
        public final static Property ip = new Property(2, String.class, "ip", false, "IP");
        public final static Property type = new Property(3, Integer.class, "type", false, "TYPE");
        public final static Property ssid = new Property(4, String.class, "ssid", false, "SSID");
    };

    public CmdDao(DaoConfig config) {
        super(config);
    }

    public CmdDao(DaoConfig config, AbstractDaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"" + TABLENAME + "\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"CMD\" BLOB," + // 1: cmd
                "\"IP\" TEXT," + // 2: ip
                "\"TYPE\" INTEGER," + // 3: type
                "\"SSID\" TEXT);"); //4: ssid
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"" + TABLENAME +"\"";
        db.execSQL(sql);
    }

    @Override
    protected Cmd readEntity(Cursor cursor, int offset) {
        Cmd entity = new Cmd( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.getBlob(offset + 1), // cmd
                cursor.getString(offset + 2), // ip
                cursor.getInt(offset + 3), // type
                cursor.getString(offset + 4)    //ssid
        );
        return entity;
    }

    @Override
    protected Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    @Override
    protected void readEntity(Cursor cursor, Cmd entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCmd(cursor.getBlob(offset + 1));
        entity.setIp(cursor.getString(offset + 2));
        entity.setType(cursor.getInt(offset + 3));
        entity.setSsid(cursor.getString(offset + 4));
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, Cmd entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindBlob(2, entity.getCmd());

        String ip = entity.getIp();
        if (ip != null) {
            stmt.bindString(3, ip);
        }

        Integer type = entity.getType();
        stmt.bindLong(4, type);

        String ssid = entity.getSsid();
        if (ssid != null) {
            stmt.bindString(5, ssid);
        }
    }

    @Override
    protected Long updateKeyAfterInsert(Cmd entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    @Override
    protected Long getKey(Cmd entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
}
