package org.iii.testjjsdk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    TextView textView; // 把視圖的元件宣告成全域變數
    Button button;
    String result=""; // 儲存資料用的字串

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);

        // 宣告按鈕的監聽器監聽按鈕是否被按下
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                // 按下之後會執行的程式碼
                // 宣告執行緒
                Thread thread = new Thread(sign_in);
                thread.start(); //1 開始執行
               
            }
        });
        Button button2;
        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                // 按下之後會執行的程式碼
                startActivity(intent); // 開始執行
            }
        });


    }

    /* ======================================== */

    // 建立一個執行緒執行的事件取得網路資料
    // Android 有規定，連線網際網路的動作都不能再主線程做執行
    // 畢竟如果使用者連上網路結果等太久整個系統流程就卡死了
    private Runnable sign_in = new Runnable(){
        public void run()
        {
            try {
                Intent intent1 = new Intent();
                TextView account;
                TextView password;
                account = findViewById(R.id.editTextAccount);
                password = findViewById(R.id.editTextPassword);
                String param = "key=sign_in"+"&account="+account.getText().toString()+"&password="+password.getText().toString();
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
                    String fire = j.getString("fire_id");
                    if(key.equals("1"))
                    {
                        result=key+"\n"+"登入成功"+j.getString("member_id");
                        Intent intent = new Intent();
                        try {
                            intent.putExtra("fire_id",j.getString("fire_id"));
                            intent.putExtra("member_id",j.getString("member_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (fire.equals("1"))
                            intent.setClass(LoginActivity.this,MainActivityfire.class);
                        else
                            intent.setClass(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
                // 讀取輸入串流並存到字串的部分
                // 取得資料後想用不同的格式10
                // 例如 Json 等等，都是在這一段做處理

           } catch(Exception e) {
                result = e.toString(); // 如果出事，回傳錯誤訊息
            }

            // 當這個執行緒完全跑完後執行
            runOnUiThread(new Runnable() {
                public void run() {
                    textView.setText(result);
                    result="";
                }
            });
        }
    };
}