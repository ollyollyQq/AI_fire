package org.iii.testjjsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Member extends AppCompatActivity {

    String result="";
    String account;
    String user_name;
    String email;
    String phone;
    String id;
    Button button,button4,button15;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;
    int a=1;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);




        textView2 = findViewById(R.id.textView2);
        button = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button15 = findViewById(R.id.button15);

        Intent intent = getIntent();
        id=intent.getStringExtra("member_id");
        Thread thread = new Thread(search);
        thread.start(); // 開始執行

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("member_id",id);
                intent.putExtra("user_name",user_name);
                intent.putExtra("email",email);
                intent.putExtra("phone",phone);
                intent.setClass(Member.this, MemberRevise.class);
                startActivity(intent);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                Intent intent = new Intent();

                intent.setClass(Member.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        button15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.putExtra("member_id", id);
                intent.setClass(Member.this, MainActivity.class);
                startActivity(intent);

            }
        });


    }


    private Runnable search = new Runnable(){
        public void run()
        {
            try {

                textView2 = findViewById(R.id.textView2);
                textView3 = findViewById(R.id.textView3);
                textView4 = findViewById(R.id.textView4);
                textView5 = findViewById(R.id.textView5);
                String param = "key=search"+"&member_id="+id;
                URL url = new URL("http://59.126.170.89/GetData.php/");
                Log.e("123","777");

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
                        result += line + "\n";
                        // 每當讀取出一列，就加到存放字串後面
                    }
                    inputStream.close(); // 關閉輸入串流
                    JSONObject j = new JSONObject(result);
                    String key = j.getString("success");
                    if(key.equals("1"))
                    {
                        textView2.setText("帳戶 : "+j.getString("account"));
                        textView3.setText("姓名 : "+j.getString("user_name"));
                        textView4.setText("email : "+j.getString("email"));
                        textView5.setText("電話號碼 : "+j.getString("phone"));
                        user_name = j.getString("user_name");
                        email = j.getString("email");
                        phone = j.getString("phone");
                    }

                    else
                    {
                        textView2.setText("讀取失敗");
                        textView3.setText("讀取失敗");
                        textView4.setText("讀取失敗");
                        textView5.setText("讀取失敗");
                    }
                }
                // 讀取輸入串流並存到字串的部分
                // 取得資料後想用不同的格式
                // 例如 Json 等等，都是在這一段做處理

            } catch(Exception e) {
                result = e.toString(); // 如果出事，回傳錯誤訊息
            }

            // 當這個執行緒完全跑完後執行

        }
    };
}