package org.iii.testjjsdk;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivitygb extends AppCompatActivity {
    private Button bt1;
    private TextView t7;
    private EditText et;
    String content= "";
    String id = "";
    String result = "";
    String inf = "";
    String fire_id = "";
    private Handler handler = new Handler();
    private Runnable rateRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giveback);

        bt1 = (Button) findViewById(R.id.bt1);

        t7 = (TextView) findViewById(R.id.t7);
        et =(EditText) findViewById(R.id.et) ;
        Intent intent = getIntent();
        id=intent.getStringExtra("member_id");
        fire_id=intent.getStringExtra("fire_id");
        Thread thread1 = new Thread(getinformation);
        thread1.start(); //1 開始執行
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str=t7.getText().toString();
                content=et.getText().toString();

                if (("".equals(et.getText().toString())))
                {


                    new AlertDialog.Builder(MainActivitygb.this)

                            .setTitle("請輸入訊息!!")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();

                }

                else {

                    et.setText("");
                    Thread thread = new Thread(uploadsug);
                    thread.start(); // 開始執行
                    Thread thread1 = new Thread(getinformation);
                    thread1.start(); //1 開始執行
                }


            }
        });
        initTimer();
    }



    private Runnable uploadsug = new Runnable(){
        public void run()
        {
            try {
                String param = "content="+content+"&member="+id+"&fire_id="+fire_id;
                URL url = new URL("http://59.126.170.89/im_information.php/");
                // 開始宣告 HTTP 連線需要的物件，這邊通常都是一綑的
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // 建立 Google 比較挺的 HttpURLConnection 物件
                connection.setRequestMethod("POST");
                // 設定連線方式為 POST
                connection.setDoOutput(true); // 允許輸出
                connection.setDoInput(true); // 允許讀入
                connection.setUseCaches(false); // 不使用快取
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                connection.setRequestProperty("Charset", "UTF-8");
                connection.connect(); // 開始連線
                OutputStream out = connection.getOutputStream();
                out.write(param.getBytes());
                out.flush();
                out.close();
                int responseCode =
                        connection.getResponseCode();
                // 建立取得回應的物件
                if(responseCode == HttpURLConnection.HTTP_OK){
                    // 如果 HTTP 回傳狀態是 OK ，而不是 Error
                    InputStream inputStream = connection.getInputStream();
                    // 取得輸入串流
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                    // 讀取輸入串流的資料
                    String box = ""; // 宣告存放用字串
                    String line = null; // 宣告讀取用的字串
                    while((line = bufReader.readLine()) != null) {
                        Log.e("123",fire_id);
                        result += line + "\n";
                        // 每當讀取出一列，就加到存放字串後面
                    }
                    inputStream.close(); // 關閉輸入串流
                    JSONObject j = new JSONObject(result);
                    String key = j.getString("success");
                    if(key.equals("1"))
                    {


                    }
                }
                // 讀取輸入串流並存到字串的部分
                // 取得資料後想用不同的格式
                // 例如 Json 等等，都是在這一段做處理

            } catch(Exception e) {
                result = e.toString(); // 如果出事，回傳錯誤訊息
            }

            // 當這個執行緒完全跑完後執行
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivitygb.this, result, Toast.LENGTH_SHORT).show(); // 更改顯示文字
                    result = "";
                }
            });
        }
    };

    private void initTimer() {
        rateRunnable = () -> {
            Thread thread = new Thread(getinformation);
            thread.start(); //1 開始執行
            handler.postDelayed(rateRunnable, 2000);
        };
        handler.postDelayed(rateRunnable, 2000);
    }

    private Runnable getinformation = new Runnable(){
        public void run()
        {
            try {
                String param = "&fire_id="+fire_id;
                URL url = new URL("http://59.126.170.89/getinformation.php/");
                // 開始宣告 HTTP 連線需要的物件，這邊通常都是一綑的
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // 建立 Google 比較挺的 HttpURLConnection 物件
                connection.setRequestMethod("POST");
                // 設定連線方式為 POST
                connection.setDoOutput(true); // 允許輸出
                connection.setDoInput(true); // 允許讀入
                connection.setUseCaches(false); // 不使用快取
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                connection.setRequestProperty("Charset", "UTF-8");
                connection.connect(); // 開始連線
                OutputStream out = connection.getOutputStream();
                out.write(param.getBytes());
                out.flush();
                out.close();
                int responseCode =
                        connection.getResponseCode();
                // 建立取得回應的物件
                if(responseCode == HttpURLConnection.HTTP_OK){
                    // 如果 HTTP 回傳狀態是 OK ，而不是 Error
                    InputStream inputStream = connection.getInputStream();
                    // 取得輸入串流
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                    // 讀取輸入串流的資料
                    String box = ""; // 宣告存放用字串
                    String line = null; // 宣告讀取用的字串
                    while((line = bufReader.readLine()) != null) {
                        Log.e("123","777");
                        result += line + "\n";
                        // 每當讀取出一列，就加到存放字串後面
                    }
                    inputStream.close(); // 關閉輸入串流
                    JSONObject j = new JSONObject(result);
                    String key = j.getString("success");

                    if(key.equals("1"))
                    {
                        inf = j.getString("in");
                        inf = inf.replace("[","");
                        inf = inf.replace("]","");
                        String[] a = inf.split(",");
                        String kaa = "";

                        t7 = findViewById(R.id.t7);
                        for(int i = a.length-1;i >= 0; i--)
                        {
                            kaa += a[i] + "\n";

                        }

                        t7.setText(kaa);
                        result = "回報成功";
                    }
                }
                // 讀取輸入串流並存到字串的部分
                // 取得資料後想用不同的格式
                // 例如 Json 等等，都是在這一段做處理

            } catch(Exception e) {
                result = e.toString(); // 如果出事，回傳錯誤訊息
            }

            // 當這個執行緒完全跑完後執行
            runOnUiThread(new Runnable() {
                public void run() {
                    result = "";
                }
            });
        }
    };
}
