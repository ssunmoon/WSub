package cn.edu.swufe.wsub;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    Thread t;

    private ArrayList<HashMap<String, String>> listItems; // 存放文字
    private int msgWhat = 7;
    SimpleAdapter adapter;
    List<HashMap<String, String>> retList;

    String url1, url2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        t = new Thread(this);
        t.start();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == msgWhat){

//                    Uri uri = Uri.parse(url);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    startActivity(intent);

                    retList = (List<HashMap<String, String>>)msg.obj;
                    adapter = new SimpleAdapter(MainActivity.this, retList,
                            R.layout.activity_main, // ListItem的XML布局实现
                            new String[] { "ItemTitle", "ItemDetail1", "ItemDetail2" },
                            new int[] { R.id.itemTitle, R.id.itemDetail1, R.id.itemDetail2 });
                    setListAdapter(adapter);
//                    getListView().setEmptyView(findViewById(R.id.nodata2));
                    Log.i("handler","reset list...");
                }
                super.handleMessage(msg);
            }
        };

        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public void run() {
        Log.i("thread", "run.....");
        boolean marker = false;
        List<HashMap<String, String>> rateList = new ArrayList<HashMap<String, String>>();
        try {
            teacher = "考研数学汤家凤";
            gotoWeb();
//            Uri uri = Uri.parse(url1);
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(intent);

            Document doc1 = Jsoup.connect(url1).get();
            Log.i("截取到的网页 ","没有？" + doc1.toString());
            String str1[] = doc1.toString().split("var msgList = ");
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
            String str4[] = new String[6];
            int t=0;
            for(int i=0;i<k;i++){
                Document doc2 = Jsoup.connect("https://mp.weixin.qq.com"+str2[i]).get();
//                Log.i("截取到的页面：","没有？" + doc2.toString());
                String str5[] = doc2.toString().split("var publish_time = \\\"");
                String publish_time = str5[1];
                Log.i("截取到的时间：","没有？" + publish_time);
                if(publish_time.startsWith("2018-10-20")){
                    Element h2 = doc2.getElementById("activity-name");
                    String activity_name = h2.text();
                    str4[t] = activity_name;
                    Log.i("截取到的昨天的文章：","没有？" + str4[t]);
                    t++;
                }
                else{
                    t = i-1;
                    break;
                }
            }

            Uri uri = Uri.parse("https://mp.weixin.qq.com"+str2[t]);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
//            String str1[] = str.split(" ");
//            Log.i("href: ",str1[3]);
//            String str2[] = str1[3].split("\\\"");
//            String str3[] = str2[1].split("amp;");
//            String url2 = str3[0] + str3[1] + str3[2] + str3[3];
//            Log.i("链接",url2);   //网页与微信有五分钟延迟

//            Elements tds = table.getElementsByTag("td");
//            for (int i = 5; i < tds.size(); i += 5) {
//                Element td = tds.get(i);
//                Element td2 = tds.get(i + 3);
//                String tdStr = td.text();
//                String pStr = td2.text();
//                HashMap<String, String> map = new HashMap<String, String>();
//                map.put("ItemTitle", tdStr);
//                map.put("ItemDetail", pStr);
//                rateList.add(map);
//                Log.i("td", tdStr + "=>" + pStr);
//            }
            marker = true;
        } catch (MalformedURLException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        }
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
}
