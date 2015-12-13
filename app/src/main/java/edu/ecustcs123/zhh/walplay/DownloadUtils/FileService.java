package edu.ecustcs123.zhh.walplay.DownloadUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BenZ on 15.12.13.
 * zhengbin0320@gmail.com
 */
public class FileService {
    private DBOpenHelper dbOpenHelper;
    public FileService(Context context) {
        dbOpenHelper = new DBOpenHelper(context);
    }

    /**
     * 从数据库获取每条线程已经下载的进度
     * @param path
     * @return
     */
    public Map<Integer,Integer> getData(String path){
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "select threadid, downlength from filedownlog where downpath=?",
                new String[]{path});//还非得是string[]……
        Map<Integer,Integer> data  = new HashMap<Integer, Integer>();
        //遍历获得map
        while(cursor.moveToNext()){
            data.put(cursor.getInt(0),cursor.getInt(1));
        }
        cursor.close();
        db.close();
        return data;
    }

    /**
     * 保存每条线程已经下载的文件长度
     *
     * @param path
     * @param map
     */
    public void save(String path, Map<Integer, Integer> map){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            for(Map.Entry<Integer,Integer> entry : map.entrySet()){
                db.execSQL(
                        "insert into filedownlog(downpath," +
                                "threadid, downlength) values(?,?,?)",
                        new Object[]{path, entry.getKey(), entry.getValue()}
                );

            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    /**
     * 更新单条线程下载进度
     *
     * @param path
     * @param threadId
     * @param pos
     */
    public void update(String path, int threadId, int pos){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("update filedownlog set downlength = ? where downpath = ? and threadid = ?",
                new Object[]{pos, path, threadId});
        db.close();
    }

    /**
     * 删除已经下载完的下载记录
     * @param path 根据文件的地址删除记录
     */
    public void delete(String path){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from filedownlog where downpath=?",
                new Object[] {path});
        db.close();
    }
}
