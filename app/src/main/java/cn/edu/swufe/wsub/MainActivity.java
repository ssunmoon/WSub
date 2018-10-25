package cn.edu.swufe.wsub;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends ListActivity implements View.OnClickListener,Runnable, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    TextView t1,t2,t3,t4;
    Handler handler;
    String da;  //标明日期
    int tnum, snum;   //该科目下老师个数，该老师更新的文章数目
    HashMap<String, String> map1 = new HashMap<String, String>();
    HashMap<String, String> map2 = new HashMap<String, String>();

    String subject = "Pol", teacher = "腿姐考研政治课堂", article;    //科目，老师
    String atitle[];    //文章题目
    int astatu[];   //文章情况
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Thread t;

    private ArrayList<HashMap<String, String>> listItems; // 存放文字
    private int msgWhat = 7;
    SimpleAdapter adapter;
    List<HashMap<String, String>> list; //用于布局

    String url1, url2;

    Calendar cal;
    String year,wyear;
    String month,wmonth;
    String day,wday;
    String hour,whour;
    String layoutstr;

    int lunshu,zonglunshu;

    //界面1和2的数据存放
    TeachItem titem1,titem2;
    SubItem sitem1,sitem2;

    int jiemian=1;
    int Y_N = 0;

    Button buttont,buttond;
    EditText text;

    int gaibian=0;

    String qingqiu;

    TeachManager teachManager;
    SubManager subManager;
    DBHelper dbHelper;

    List tstr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(this);

        tstr = new ArrayList();

        map1.put("Pol", "政治");map1.put("Eng","英语");map1.put("Math","数学");map1.put("Pro","专业课");map1.put("Other","其他");
        map2.put("政治","Pol");map2.put("英语","Eng");map2.put("数学","Math");map2.put("专业课","Pro");map2.put("其他","Other");

        teachManager = new TeachManager(MainActivity.this,dbHelper);
        subManager = new SubManager(MainActivity.this,subject,dbHelper);
        List<TeachItem> teachList = new ArrayList<TeachItem>();
        teachList = teachManager.findById(subject);
        if(teachList.size()==0){
            wubuju();
        }
        else{
            List<SubItem> subList = new ArrayList<SubItem>();
            youbuju(teachList,subList);
        }

        cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        year = String.valueOf(cal.get(Calendar.YEAR));
        month = String.valueOf(Integer.valueOf(String.valueOf(cal.get(Calendar.MONTH))) + 1);
        day = String.valueOf(cal.get(Calendar.DATE));
        hour = String.valueOf(cal.get(Calendar.HOUR));

        SharedPreferences sharedPreferences = getSharedPreferences("UpDateTime", Activity.MODE_PRIVATE);
        wyear = sharedPreferences.getString("year","0");
        wmonth = sharedPreferences.getString("month","0");
        wday = sharedPreferences.getString("day","0");
        whour = sharedPreferences.getString("hour","0");

        int chazhi = (Integer.valueOf(year)*1000000+Integer.valueOf(month)*10000+Integer.valueOf(day)*100+Integer.valueOf(hour))-(Integer.valueOf(wyear)*1000000+Integer.valueOf(wmonth)*10000+Integer.valueOf(wday)*100+Integer.valueOf(whour));

        //初始时或到更新时间时，更新数据库
        if(chazhi > 1){
            quanbugengxin();
            jiemian=1;
            Toast.makeText(MainActivity.this, "定时更新成功", Toast.LENGTH_LONG).show();
        }

        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
    }

    public void quanbugengxin(){
        tstr = new ArrayList();
        jiemian=1;
        List<TeachItem> teachList = new ArrayList<TeachItem>();
        teachList = teachManager.listAll();
        zonglunshu=teachList.size();
        lunshu=0;
        for(int i=0;i<teachList.size();i++){
            while(lunshu!=i){
                try {
                    Thread.currentThread().sleep(500);//阻断6秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            teacher=teachList.get(i).getT_name();
            subject=teachList.get(i).getCourse();
            Log.i("更新到的公众号：",teacher);
            subManager.delete_t(teacher,subject);
            t = new Thread(MainActivity.this);
            t.start();
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if(msg.what == msgWhat){
                        if(lunshu!=zonglunshu){
                            list.clear();
                            HashMap<String, String> m = new HashMap<String, String>();
                            m.put("ItemTitle", "");
                            m.put("ItemDetail1", "");
                            m.put("ItemDetail2", "");
                            list.add(m);
                            adapter.notifyDataSetChanged();
                            buttont.setText("正在更新");
                        }
                        if(lunshu==zonglunshu){
                            subject="Pol";
                            List<TeachItem> teachList2 = new ArrayList<TeachItem>();
                            teachList2 = teachManager.findById(subject);
                            if(teachList2.size()==0){
                                String str;
                                list.clear();
                                HashMap<String, String> m = new HashMap<String, String>();
                                m.put("ItemTitle", map1.get(subject)+"：暂无数据");
                                m.put("ItemDetail1", "");
                                m.put("ItemDetail2", "");
                                list.add(m);
                                adapter.notifyDataSetChanged();
                                jiemian = 1;
                                buttont.setText(map1.get(subject));
//                                Toast.makeText(MainActivity.this, "(无)全部更新成功", Toast.LENGTH_SHORT).show();
                            }
                            if(teachList2.size()!=0){
                                list.clear();
                                for(int i=0;i<teachList2.size();i++){
                                    List<SubItem> ls = subManager.findById(teachList2.get(i).getT_name(),subject);
                                    HashMap<String, String> m = new HashMap<String, String>();
                                    m.put("ItemTitle", teachList2.get(i).getT_name());
                                    m.put("ItemDetail1", ls.get(ls.size()-1).getTitle());
                                    m.put("ItemDetail2", ls.get(ls.size()-1).getTeacher());
                                    list.add(m);
                                }
                                adapter.notifyDataSetChanged();
                                jiemian=1;
                                buttont.setText(map1.get(subject));
                                Toast.makeText(MainActivity.this,"全部更新成功！",Toast.LENGTH_SHORT).show();
                            }
                            cal = Calendar.getInstance();
                            cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                            year = String.valueOf(cal.get(Calendar.YEAR));
                            month = String.valueOf(Integer.valueOf(String.valueOf(cal.get(Calendar.MONTH))) + 1);
                            day = String.valueOf(cal.get(Calendar.DATE));
                            hour = String.valueOf(cal.get(Calendar.HOUR));
                            SharedPreferences sp = getSharedPreferences("UpDateTime",Activity.MODE_PRIVATE);
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("year", year);
                            edit.putString("month", month);
                            edit.putString("day", day);
                            edit.putString("hour", hour);
                            edit.commit();
                            Log.i("文件：","更新时间已写入");
                            Toast.makeText(MainActivity.this, "更新时间已写入", Toast.LENGTH_SHORT).show();
                        }
//                        Toast.makeText(MainActivity.this,teacher+" 更新成功！",Toast.LENGTH_SHORT).show();
                        jiemian=1;
                    }
                    super.handleMessage(msg);
                }
            };
        }
    }

    public void addButton(){
        buttont=new Button(this);
        buttont.setText(map1.get(subject));
        buttont.setBackgroundColor(255239213);
        FrameLayout.LayoutParams lp0 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        lp0.gravity= Gravity.TOP;
        addContentView(buttont,lp0);//这个是重点
        buttont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("请选择公众号类别或更新操作");
                builder.setSingleChoiceItems(new String[] {"政治","英语","数学","专业课","其它","更新全部公众号","删除全部公众号"}, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(which!=5){
                            if(which==0)    subject="Pol";
                            if(which==1)    subject="Eng";
                            if(which==2)    subject="Math";
                            if(which==3)    subject="Pro";
                            if(which==4)    subject="Other";
                            buttont.setText("");
                            List<TeachItem> teachList = new ArrayList<TeachItem>();
                            teachList = teachManager.findById(subject);
                            if(teachList.size()==0){
                                list.clear();
                                HashMap<String, String> m = new HashMap<String, String>();
                                m.put("ItemTitle", map1.get(subject)+"：暂无数据");
                                m.put("ItemDetail1", "");
                                m.put("ItemDetail2", "");
                                list.add(m);
                                adapter.notifyDataSetChanged();
                                jiemian=1;
                                buttont.setText(map1.get(subject));
                            }
                            else{
                                list.clear();
                                for(int i=0;i<teachList.size();i++){
                                    List<SubItem> ls = subManager.findById(teachList.get(i).getT_name(),subject);
                                    HashMap<String, String> m = new HashMap<String, String>();
                                    m.put("ItemTitle", teachList.get(i).getT_name());
                                    m.put("ItemDetail1", ls.get(ls.size()-1).getTitle());
                                    m.put("ItemDetail2", ls.get(ls.size()-1).getTeacher());
                                    list.add(m);
                                }
                                adapter.notifyDataSetChanged();
                                jiemian=1;
                                buttont.setText(map1.get(subject));
                            }
                        }
                        if(which==5){
                            quanbugengxin();
                            jiemian=1;
                        }
                        if(which==6){
                            teachManager.deleteAll();
                            subManager.deleteAll("Pol");
                            subManager.deleteAll("Eng");
                            subManager.deleteAll("Math");
                            subManager.deleteAll("Pro");
                            subManager.deleteAll("Other");
                            list.clear();
                            HashMap<String, String> m = new HashMap<String, String>();
                            m.put("ItemTitle", map1.get(subject)+"：暂无数据");
                            m.put("ItemDetail1", "");
                            m.put("ItemDetail2", "");
                            list.add(m);
                            adapter.notifyDataSetChanged();
                            jiemian = 1;
                            buttont.setText(map1.get(subject));
                            Toast.makeText(MainActivity.this, "全部删除成功", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();
            }
        });

        buttond=new Button(this);
        buttond.setText("+ 添加公众号");
        buttond.setBackgroundColor(255239213);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity= Gravity.BOTTOM;
        addContentView(buttond,lp);//这个是重点
        buttond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                text = new EditText(MainActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("请准确输入要添加的公众号名称");
                builder.setView(text);
                builder.setNegativeButton("取消",null);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Log.i(TAG, "添加公众号成功"+text.getText().toString());
                        teacher = text.getText().toString();
                        teachManager.add(subject,teacher);
                        t = new Thread(MainActivity.this);
                        t.start();
                        handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if(msg.what == msgWhat){
                                    List<TeachItem> teachList = new ArrayList<TeachItem>();
                                    teachList = teachManager.findById(subject);
                                    if(teachList.size()!=0){
                                        list.clear();
                                        for(int i=0;i<teachList.size();i++){
                                            List<SubItem> ls = subManager.findById(teachList.get(i).getT_name(),subject);
                                            HashMap<String, String> m = new HashMap<String, String>();
                                            m.put("ItemTitle", teachList.get(i).getT_name());
                                            m.put("ItemDetail1", ls.get(ls.size()-1).getTitle());
                                            m.put("ItemDetail2", ls.get(ls.size()-1).getTeacher());
                                            list.add(m);
                                        }
                                        adapter.notifyDataSetChanged();
                                        jiemian=1;
                                        buttont.setText(map1.get(subject));
                                    }
                                }
                                super.handleMessage(msg);
                            }
                        };
                    }
                });
                builder.show();
            }
        });
    }

    public void wubuju(){
        list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("ItemTitle", map1.get(subject)+"：暂无数据");
        m.put("ItemDetail1", "");
        m.put("ItemDetail2", "");
        list.add(m);
        adapter = new SimpleAdapter(MainActivity.this, list,
                R.layout.activity_main, // ListItem的XML布局实现
                new String[] { "ItemTitle", "ItemDetail1", "ItemDetail2" },
                new int[] { R.id.itemTitle, R.id.itemDetail1, R.id.itemDetail2 });
        setListAdapter(adapter);
        addButton();
    }

    public void youbuju(List<TeachItem> list1, List<SubItem> list2){
        list = new ArrayList<HashMap<String, String>>();
        if(list1.size()!=0){
            for(int i=0;i<list1.size();i++){
                List<SubItem> ls = subManager.findById(list1.get(i).getT_name(),subject);
                HashMap<String, String> m = new HashMap<String, String>();
                m.put("ItemTitle", list1.get(i).getT_name());
                m.put("ItemDetail1", ls.get(ls.size()-1).getTitle());
                m.put("ItemDetail2", ls.get(ls.size()-1).getTeacher());
                list.add(m);
            }
        }
        if(list2.size()!=0){

        }
        adapter = new SimpleAdapter(MainActivity.this, list,
                R.layout.activity_main, // ListItem的XML布局实现
                new String[] { "ItemTitle", "ItemDetail1", "ItemDetail2" },
                new int[] { R.id.itemTitle, R.id.itemDetail1, R.id.itemDetail2 });
        setListAdapter(adapter);
        addButton();
        Log.i("handler","reset list...");
    }

    @Override
    public void run() {
        Log.i("thread", "run.....");
        boolean marker = false;
        List<HashMap<String, String>> rateList = new ArrayList<HashMap<String, String>>();
        if(jiemian==1){
            gotoWeb();
            biaotihuoqu();
            lunshu++;
        }
        if(jiemian==2){
            gotoWeb();
            try {
                Document doc1 = Jsoup.connect(url1).get();
                Log.i("截取到的公众号 ","没有？" + teacher);
                Log.i("截取到的网页 ","没有？" + doc1.toString());
                String str1[] = doc1.toString().split("var msgList = ");
                if(str1.length==1){
                    Uri uri = Uri.parse(url1);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                String str2[] = str1[1].split("content_url\\\":\\\"|\\\",\\\"copyright_stat\\\":");
                //删除不合规格的字符串
                int k=0;
                for(int i = 0; i < str2.length; i++){
                    if (str2[i].startsWith("/s?timestamp=")) {
                        String str3[] = str2[i].split("amp;");
                        str2[k] = str3[0] + str3[1] + str3[2] + str3[3];
                        Log.i("截取到的链接 ","没有？" + str2[k]);
                        k++;
                    }
                }
                //删除时间不对的链接
                String str4[] = new String[50];
                int t=0;
                for(int i=0;i<k && t<50;i++){
                    Document doc2 = Jsoup.connect("https://mp.weixin.qq.com"+str2[i]).get();
//                Log.i("截取到的页面：","没有？" + doc2.toString());
                    String str5[] = doc2.toString().split("var publish_time = \\\"");
                    String publish_time = str5[1];
                    Log.i("截取到的时间：","没有？" + publish_time);
                    if(publish_time.startsWith(year+"-"+month+"-"+day)){
                        Element h2 = doc2.getElementById("activity-name");
                        String activity_name = h2.text();
                        str4[t] = activity_name;
                        Log.i("截取到的今天的文章：","没有？" + str4[t]);
                        if(str4[t].equals(article)){
                            subManager.updated(teacher,article,subject);
                            Uri uri = Uri.parse("https://mp.weixin.qq.com"+str2[i]);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                        t++;
                    }
                    else{
                        break;
                    }
                }
            } catch (MalformedURLException e) {
                Log.e("www", e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("www", e.toString());
                e.printStackTrace();
            }
        }
        if(jiemian==3){
            gotoWeb();
            Uri uri = Uri.parse(url1);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        marker = true;
        Message msg = handler.obtainMessage();
        msg.what = msgWhat;
        if (marker) {
            msg.arg1 = 1;
        } else {
            msg.arg1 = 0;
        }
        msg.obj = rateList;
        handler.sendMessage(msg);
        Log.i("thread", "sendMessage.....");
    }

    public void biaotihuoqu(){
        try {
            Document doc1 = Jsoup.connect(url1).get();
            Log.i("截取到的公众号 ","没有？" + teacher);
            Log.i("截取到的网页 ","没有？" + doc1.toString());
            String str1[] = doc1.toString().split("var msgList = ");
            if(str1.length==1){
                Uri uri = Uri.parse(url1);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
            String str2[] = str1[1].split("content_url\\\":\\\"|\\\",\\\"copyright_stat\\\":");
            //删除不合规格的字符串
            int k=0;
            for(int i = 0; i < str2.length; i++){
                if (str2[i].startsWith("/s?timestamp=")) {
                    String str3[] = str2[i].split("amp;");
                    str2[k] = str3[0] + str3[1] + str3[2] + str3[3];
                    Log.i("截取到的链接 ","没有？" + str2[k]);
                    k++;
                }
            }
            //删除时间不对的链接
            String str4[] = new String[50];
            int t=0;
            for(int i=0;i<k && t<50;i++){
                Document doc2 = Jsoup.connect("https://mp.weixin.qq.com"+str2[i]).get();
//                Log.i("截取到的页面：","没有？" + doc2.toString());
                String str5[] = doc2.toString().split("var publish_time = \\\"");
                String publish_time = str5[1];
                Log.i("截取到的时间：","没有？" + publish_time);
                if(publish_time.startsWith(year+"-"+month+"-"+day)){
                    Element h2 = doc2.getElementById("activity-name");
                    String activity_name = h2.text();
                    str4[t] = activity_name;
                    Log.i("截取到的今天的文章：","没有？" + str4[t]);
                    t++;
                }
                else{
                    break;
                }
            }

            for(int i=0;i<t;i++)
            {
                subManager.add(teacher,str4[i],0,subject);
            }
        } catch (MalformedURLException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        }
    }

    public void gotoWeb(){
        try {
            Document doc = Jsoup.connect("https://weixin.sogou.com/weixin?type=1&s_from=input&query="+teacher+"&ie=utf8&_sug_=n&_sug_type_=").get();
            Elements as = doc.getElementsByTag("a");
            Element a = as.get(24);
            String str = a.toString();
            Log.i("截取到的<a> ","没有？" + str);
            String str1[] = str.split(" ");
            Log.i("href: ",str1[3]);
            String str2[] = str1[3].split("\\\"");
            String str3[] = str2[1].split("amp;");
            url1 = str3[0] + str3[1] + str3[2] + str3[3];
            Log.i("链接",url1);   //网页与微信有五分钟延迟
        } catch (MalformedURLException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object itemAtPosition = getListView().getItemAtPosition(position);
        HashMap<String,String> map = (HashMap<String, String>) itemAtPosition;
        if(jiemian==2){
            article = map.get("ItemTitle");
            t = new Thread(MainActivity.this);
            t.start();
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if(msg.what == msgWhat){
                        List<SubItem> ls = subManager.findById(teacher,subject);
                        list.clear();
                        for(int i=0;i<ls.size()-1;i++){
                            HashMap<String, String> m = new HashMap<String, String>();
                            m.put("ItemTitle", ls.get(i).getTitle());
                            if(ls.get(i).getStatus()==1){
                                m.put("ItemDetail1", "已读");
                                m.put("ItemDetail2", "");
                            }
                            if(ls.get(i).getStatus()==0){
                                m.put("ItemDetail1", "");
                                m.put("ItemDetail2", "未读");
                            }
                            list.add(m);
                        }
                        adapter.notifyDataSetChanged();
                        buttont.setText(map1.get(subject)+"："+teacher);
                        jiemian=2;
                    }
                    super.handleMessage(msg);
                }
            };
        }
        if(jiemian==1){
            teacher = map.get("ItemTitle");
            List<SubItem> ls = subManager.findById(teacher,subject);
            if(ls.size()==1){
                Toast.makeText(this,"该公众号今天还未更新！",Toast.LENGTH_LONG).show();
            }
            if(ls.size()>1){
                list.clear();
                for(int i=0;i<ls.size()-1;i++){
                    HashMap<String, String> m = new HashMap<String, String>();
                    m.put("ItemTitle", ls.get(i).getTitle());
                    if(ls.get(i).getStatus()==1){
                        m.put("ItemDetail1", "已读");
                        m.put("ItemDetail2", "");
                    }
                    if(ls.get(i).getStatus()==0){
                        m.put("ItemDetail1", "");
                        m.put("ItemDetail2", "未读");
                    }
                    list.add(m);
                }
                adapter.notifyDataSetChanged();
                buttont.setText(map1.get(subject)+"："+teacher);
                jiemian=2;
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Object itemAtPosition = getListView().getItemAtPosition(position);
        HashMap<String,String> map = (HashMap<String, String>) itemAtPosition;
        if(jiemian==1){
            teacher=map.get("ItemTitle");
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("请选择对“"+teacher+"”的操作");
            builder.setSingleChoiceItems(new String[] {"查看历史信息","更新该公众号","删除该公众号"}, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(which==0){
                        jiemian=3;
                        t = new Thread(MainActivity.this);
                        t.start();
                        handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if(msg.what == msgWhat){
                                    jiemian=1;
                                }
                                super.handleMessage(msg);
                            }
                        };
                    }
                    if(which==1){
                        subManager.delete_t(teacher,subject);
                        t = new Thread(MainActivity.this);
                        t.start();
                        handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if(msg.what == msgWhat){
                                    List<TeachItem> teachList = new ArrayList<TeachItem>();
                                    teachList = teachManager.findById(subject);
                                    if(teachList.size()!=0){
                                        list.clear();
                                        for(int i=0;i<teachList.size();i++){
                                            List<SubItem> ls = subManager.findById(teachList.get(i).getT_name(),subject);
                                            HashMap<String, String> m = new HashMap<String, String>();
                                            m.put("ItemTitle", teachList.get(i).getT_name());
                                            m.put("ItemDetail1", ls.get(ls.size()-1).getTitle());
                                            m.put("ItemDetail2", ls.get(ls.size()-1).getTeacher());
                                            list.add(m);
                                        }
                                        adapter.notifyDataSetChanged();
                                        jiemian=1;
                                        buttont.setText(map1.get(subject));
                                        Toast.makeText(MainActivity.this,teacher+" 更新成功！",Toast.LENGTH_LONG).show();
                                    }
                                }
                                super.handleMessage(msg);
                            }
                        };
                    }
                    if(which==2){
                        teachManager.delete(subject,teacher);
                        subManager.delete_t(teacher,subject);
                        List<TeachItem> teachList = new ArrayList<TeachItem>();
                        teachList = teachManager.findById(subject);
                        if(teachList.size()==0){
                            list.clear();
                            HashMap<String, String> m = new HashMap<String, String>();
                            m.put("ItemTitle", map1.get(subject)+"：暂无数据");
                            m.put("ItemDetail1", "");
                            m.put("ItemDetail2", "");
                            list.add(m);
                            adapter.notifyDataSetChanged();
                            jiemian=1;
                            buttont.setText(map1.get(subject));
                        }
                        else{
                            list.clear();
                            for(int i=0;i<teachList.size();i++){
                                List<SubItem> ls = subManager.findById(teachList.get(i).getT_name(),subject);
                                HashMap<String, String> m = new HashMap<String, String>();
                                m.put("ItemTitle", teachList.get(i).getT_name());
                                m.put("ItemDetail1", ls.get(ls.size()-1).getTitle());
                                m.put("ItemDetail2", ls.get(ls.size()-1).getTeacher());
                                list.add(m);
                            }
                            adapter.notifyDataSetChanged();
                            jiemian=1;
                            buttont.setText(map1.get(subject));
                        }
                        Toast.makeText(MainActivity.this,teacher+"删除成功！",Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNegativeButton("取消",null);
            builder.show();
        }
        if(jiemian==2){
            article=map.get("ItemTitle");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示").setMessage("您确认要删除此文章么？").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    subManager.delete_tt(teacher,article,subject);
                    List<SubItem> ls = subManager.findById(teacher,subject);
                    if(ls.size()==1){
                        List<TeachItem> teachList = new ArrayList<TeachItem>();
                        teachList = teachManager.findById(subject);
                        if(teachList.size()!=0) {
                            list.clear();
                            for (int i = 0; i < teachList.size(); i++) {
                                List<SubItem> ls1 = subManager.findById(teachList.get(i).getT_name(), subject);
                                HashMap<String, String> m = new HashMap<String, String>();
                                m.put("ItemTitle", teachList.get(i).getT_name());
                                m.put("ItemDetail1", ls1.get(ls1.size() - 1).getTitle());
                                m.put("ItemDetail2", ls1.get(ls1.size() - 1).getTeacher());
                                list.add(m);
                            }
                            adapter.notifyDataSetChanged();
                            jiemian = 1;
                            buttont.setText(map1.get(subject));
                            Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                        }
                    }
                    if(ls.size()>1){
                        list.clear();
                        for(int i=0;i<ls.size()-1;i++){
                            HashMap<String, String> m = new HashMap<String, String>();
                            m.put("ItemTitle", ls.get(i).getTitle());
                            if(ls.get(i).getStatus()==1){
                                m.put("ItemDetail1", "已读");
                                m.put("ItemDetail2", "");
                            }
                            if(ls.get(i).getStatus()==0){
                                m.put("ItemDetail1", "");
                                m.put("ItemDetail2", "未读");
                            }
                            list.add(m);
                        }
                        adapter.notifyDataSetChanged();
                        buttont.setText(map1.get(subject)+"："+teacher);
                        jiemian=2;
                    }
                }
            }).setNegativeButton("取消", null);
            builder.create().show();
        }
        return true;
    }

    @Override
    public void onClick(View v) {

    }
}
