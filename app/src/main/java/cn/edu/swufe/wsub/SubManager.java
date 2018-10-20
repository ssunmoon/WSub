package cn.edu.swufe.wsub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SubManager {
    private DBHelper dbHelper;
    private String TBNAME;

    public SubManager(Context context, String tbname){
        dbHelper = new DBHelper(context);
        TBNAME = tbname;
    }

    public void add(SubItem item){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Teacher", item.getTeacher());
        values.put("Title", item.getTitle());
        values.put("Status", item.getStatus());
        db.insert(TBNAME, null, values);
        db.close();
    }

    public void addAll(List<SubItem> list){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (SubItem item : list) {
            ContentValues values = new ContentValues();
            values.put("Teacher", item.getTeacher());
            values.put("Title", item.getTitle());
            values.put("Status", item.getStatus());
            db.insert(TBNAME, null, values);
        }
        db.close();
    }

    public void deleteAll(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TBNAME,null,null);
        db.close();
    }

    public void delete_tt(String teacher, String title){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TBNAME, "Teacher=? AND Title=?", new String[]{teacher, title});
        db.close();
    }

    public void delete_t(String teacher){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TBNAME, "Teacher=?", new String[]{teacher});
        db.close();
    }

    public void updated(String teacher, String title){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Status", 1);
        db.update(TBNAME, values, "Teacher=? AND Title=?", new String[]{teacher, title});
        db.close();
    }

    public void updates(String teacher, String title){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Status", -1);
        db.update(TBNAME, values, "Teacher=? AND Title=?", new String[]{teacher, title});
        db.close();
    }

    public List<SubItem> listAll(){
        List<SubItem> subList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TBNAME, null, null, null, null, null, null);
        if(cursor!=null){
            subList = new ArrayList<SubItem>();
            while(cursor.moveToNext()){
                SubItem item = new SubItem();
                item.setTeacher(cursor.getString(cursor.getColumnIndex("Teacher")));
                item.setTitle(cursor.getString(cursor.getColumnIndex("Title")));
                item.setStatus(cursor.getInt(cursor.getColumnIndex("Status")));
                subList.add(item);
            }
            cursor.close();
        }
        db.close();
        return subList;
    }

    public List<SubItem> findById(String teacher){
        List<SubItem> subList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TBNAME, null, "Teacher=?", new String[]{teacher}, null, null, null);
        if(cursor!=null){
            subList = new ArrayList<SubItem>();
            while(cursor.moveToNext()){
                SubItem item = new SubItem();
                item.setTeacher(cursor.getString(cursor.getColumnIndex("Teacher")));
                item.setTitle(cursor.getString(cursor.getColumnIndex("Title")));
                item.setStatus(cursor.getInt(cursor.getColumnIndex("Status")));
                subList.add(item);
            }
            cursor.close();
        }
        db.close();
        return subList;
    }
}
