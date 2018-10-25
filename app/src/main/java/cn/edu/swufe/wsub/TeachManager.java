package cn.edu.swufe.wsub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TeachManager {
    private DBHelper dbHelper;
    private String TBNAME = "Subscribes";

    public TeachManager(Context context,DBHelper db){
        dbHelper = db;
    }

    public void add(String subject,String teacher){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Course", subject);
        values.put("T_Name", teacher);
        db.insert(TBNAME, null, values);
        db.close();
    }

    public void delete(String course, String t_name){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TBNAME, "Course=? AND T_Name=?", new String[]{course, t_name});
        db.close();
    }

    public List<TeachItem> listAll(){
        List<TeachItem> teachList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TBNAME, null, null, null, null, null, null);
        if(cursor!=null){
            teachList = new ArrayList<TeachItem>();
            while(cursor.moveToNext()){
                TeachItem item = new TeachItem();
                item.setCourse(cursor.getString(cursor.getColumnIndex("Course")));
                item.setT_name(cursor.getString(cursor.getColumnIndex("T_Name")));
                teachList.add(item);
            }
            cursor.close();
        }
        db.close();
        return teachList;
    }

    public List<TeachItem> findById(String subject){
        List<TeachItem> teachList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TBNAME, null, "Course=?", new String[]{subject}, null, null, null);
        if(cursor!=null){
            teachList = new ArrayList<TeachItem>();
            while(cursor.moveToNext()){
                TeachItem item = new TeachItem();
                item.setCourse(cursor.getString(cursor.getColumnIndex("Course")));
                item.setT_name(cursor.getString(cursor.getColumnIndex("T_Name")));
                teachList.add(item);
            }
            cursor.close();
        }
        db.close();
        return teachList;
    }

    public void deleteAll(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TBNAME,null,null);
        db.close();
    }
}
