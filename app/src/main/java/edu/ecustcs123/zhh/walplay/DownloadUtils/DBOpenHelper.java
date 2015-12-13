package edu.ecustcs123.zhh.walplay.DownloadUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by BenZ on 15.12.13.
 * zhengbin0320@gmail.com
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_Name = "walplay.db";
    private static final int VERSION = 1;

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS filedownlog");
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS filedownlog (" +
                "id integer primary key autoincrement, "+
                "downpath varchar(100), "+
                "threadid INTEGER, "+
                "downlength INTEGER)");
    }

    public DBOpenHelper(Context context) {
        super(context, DB_Name, null, VERSION);
    }
}
