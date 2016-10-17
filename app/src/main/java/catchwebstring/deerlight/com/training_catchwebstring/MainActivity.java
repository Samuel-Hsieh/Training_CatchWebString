package catchwebstring.deerlight.com.training_catchwebstring;

import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String webUrl = "http://rate.bot.com.tw/Pages/Static/UIP003.zh-TW.htm";
    //String testWebUrl = "http://samuel-hsieh-blog.logdown.com";
    TextView textView;
    ProgressBar progressBar;
    public static final int GETSTRING = 100; //自訂一個號碼
    private Thread thread;
    String urlData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView = (TextView) findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                GetUrlData();
                Snackbar.make(view, "抓取美金買入匯率", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private String GetUrlData(){
        thread = new Thread(){
            @Override
            public void run(){
                try {
                    Log.e("success", "success");
                    String decodeString;
                    HttpURLConnection httpURLConnection;
                    //建立Url
                    URL url = new URL(webUrl);
                    //連線
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoInput(true);
                    //httpURLConnection.setDoOutput(true);
                    httpURLConnection.connect();
                    //用BufferedReader讀回來
                    InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    while ((decodeString = bufferedReader.readLine()) != null){
                        urlData += decodeString;
                    }
                    bufferedReader.close();
                    //sendMessage
                    Message m = new Message();
                    m.what = GETSTRING;
                    handler.sendMessage(m);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ERROR", e.toString());
                }
            }
        };
        //開始執行執行緒
        thread.start();
        return urlData;
    }
    //擷取BufferedReader所收到的字串資料
    private String Parser(String urlData){
        int end;
        int start;
        String temp;
        start = urlData.indexOf("<td data-table=\"本行現金買入\"");
        end = urlData.indexOf("</td>",start);
        Log.e("ggg","start "+start);
        Log.e("ggg","end"+end);
        temp = urlData.substring(start, end);
        return temp;
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GETSTRING:
                    progressBar.setVisibility(View.GONE);
                    textView.setText(Parser(urlData));
                    //textView.setText(urlData);
                    break;
            }
            super.handleMessage(msg);
        }
    };
}


