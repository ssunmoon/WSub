package cn.edu.swufe.wsub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TeachManager {
    private DBHelper dbHelper;
    private String TBNAME = "TB_T";

    public TeachManager(Context context){
        dbHelper = new DBHelper(context);
    }

    public void add(TeachItem item){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Course", item.getCourse());
        values.put("T_Name", item.getT_name());
        db.insert(TBNAME, null, values);
        db.close();
    }

    public void delete(String course, String t_name){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TBNAME, "Course=? AND T_Name=?", new String[]{course, t_name});
        db.close();
    }
}
