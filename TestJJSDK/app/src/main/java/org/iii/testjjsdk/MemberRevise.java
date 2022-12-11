package org.iii.testjjsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MemberRevise extends AppCompatActivity {

    TextView textView;
    TextView name;
    TextView email;
    TextView phone;
    Button button,button17 ;

    String account;
    String result = "";
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);



        name = findViewById(R.id.editTextTextPersonName);
        email = findViewById(R.id.editTextTextEmailAddress);
        phone = findViewById(R.id.editTextPhone);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView6);

        Intent intent = getIntent();
        id=intent.getStringExtra("member_id");
        name.setText(intent.getStringExtra("user_name"));
        email.setText(intent.getStringExtra("email"));
        phone.setText(intent.getStringExtra("phone"));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                Thread thread = new Thread(Alter);
                thread.start(); // 開始執行

            }
        });
        button17 = (Button) findViewById(R.id.button17);
        button17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.putExtra("member_id", id);
                intent.setClass(MemberRevise.this, Member.class);
                startActivity(intent);

            }
        });
    }

    private Runnable Alter = new Runnable(){
        public void run()
        {
            try {
                String param = "key=update"+"&member_id="+id+"&name="+name.getText().toString()+"&email="+email.getText().toString()+"&phone="+phone.getText().toString();
                URL url = new URL("http://59.126.170.89/GetData.php/");
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
                        Log.e("123","777"+line);
                        result += line + "\n";
                        // 每當讀取出一列，就加到存放字串後面
                    }
                    inputStream.close(); // 關閉輸入串流
                    JSONObject j = new JSONObject(result);
                    String key = j.getString("success");
                    if(key.equals("1"))
                    {
                        result="修改成功";
                        Intent intent = new Intent();
                        intent.putExtra("member_id",id);
                        intent.setClass(MemberRevise.this, Member.class);
                        startActivity(intent);
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
                    Toast.makeText(MemberRevise.this, result, Toast.LENGTH_SHORT).show(); // 更改顯示文字
                    result="";// 更改顯示文字
                }
            });
        }
    };
}