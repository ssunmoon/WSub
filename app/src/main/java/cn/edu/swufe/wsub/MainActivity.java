package cn.edu.swufe.wsub;

import android.app.Activity;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity implements Runnable, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    TextView t1,t2,t3,t4;
    Handler handler;
    String da;  //标明日期
    int tnum, snum;   //该科目下老师个数，该老师更新的文章数目
    String subjevt, teacher;    //科目，老师
    String atitle[];    //文章题目
    int astatu[];   //文章情况
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private ArrayList<HashMap<String, String>> listItems; // 存放文字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.classify,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.Pol){

        }
        if(item.getItemId()==R.id.Eng){

        }
        if(item.getItemId()==R.id.Math){

        }
        if(item.getItemId()==R.id.Pro){

        }
        if(item.getItemId()==R.id.Other){

        }
        if(item.getItemId()==R.id.Update){

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void run() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
}
