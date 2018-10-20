package cn.edu.swufe.wsub;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private  static  final  int VERSION = 1;
    private  static  final  String DB_NAME = "mysub.db";
    public  static  final  String TB_Pol = "Pol", TB_Eng = "Eng", TB_Math = "Math", TB_Pro = "Pro", TB_Other = "Other", TB_T = "Subscribes";

    public  DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }

    public DBHelper(Context context) {
        super(context,DB_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TB_Pol+"(Teacher TEXT, Title TEXT, Status INTEGER, PRIMARY KEY (Teacher,Title))");
        db.execSQL("CREATE TABLE "+TB_Eng+"(Teacher TEXT, Title TEXT, Status INTEGER, PRIMARY KEY (Teacher,Title))");
        db.execSQL("CREATE TABLE "+TB_Math+"(Teacher TEXT, Title TEXT, Status INTEGER, PRIMARY KEY (Teacher,Title))");
        db.execSQL("CREATE TABLE "+TB_Pro+"(Teacher TEXT, Title TEXT, Status INTEGER, PRIMARY KEY (Teacher,Title))");
        db.execSQL("CREATE TABLE "+TB_Other+"(Teacher TEXT, Title TEXT, Status INTEGER, PRIMARY KEY (Teacher,Title))");
        db.execSQL("CREATE TABLE "+TB_T+"(Course TEXT, T_Name TEXT, PRIMARY KEY (Course,T_Name))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
