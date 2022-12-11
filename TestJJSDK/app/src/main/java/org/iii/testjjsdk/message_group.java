package org.iii.testjjsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class message_group extends AppCompatActivity {

    LinearLayout mainLinerLayout;
    Button button11;
    int a=0,c = 0;
    int width;
    String id;
    String result="";
    int count=-1;
    int k = 0;
    String check = "";
    int key=0;
    String asd="";
    String kaa;
    String[] b;
    String[] address;
    private Handler handler = new Handler();
    private Runnable rateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_group);

        Intent intent = getIntent();
        id = intent.getStringExtra("member_id");
        check = intent.getStringExtra("check");
        if (check.equals("glass"))
        {
            key = 1;
        }
        mainLinerLayout = (LinearLayout) this.findViewById(R.id.table);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        Thread thread = new Thread(firecount);
        thread.start(); //1 開始執行
        while(c != 1)
        {
            if(count>=0)
            {

                createButton();
                c = 1;
            }
        }
    }

    private void createButton(){
        if(count > 0)
        {
            Button[] but = new Button[count];
            for(int i = 0 ; i < count ; i++)
            {
                but[i]=new Button(this);
                but[i].setId(k);
                Log.d("da","id is" +  but[i].getId());
                but[i].setWidth(width);
//              btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                but[i].setText("地址:" + address[i]);
                String asa =  b[k];
                k++;
                but[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (key == 1)
                        {
                            Intent intent = new Intent();
                            intent.putExtra("member_id", id);
                            intent.putExtra("fire_id", asa);
                            intent.setClass( message_group.this,GlassesCamera.class);
                            startActivity(intent);

                        }
                        else
                        {
                            Intent intent = new Intent();
                            intent.putExtra("member_id", id);
                            intent.putExtra("fire_id", asa);
                            intent.setClass( message_group.this,MainActivitygb.class);
                            startActivity(intent);
                        }




                    }
                });




                mainLinerLayout.addView(but[i]);
            }

        }


    }




    private final Runnable firecount = new Runnable(){
        public void run()
        {
            try {

                TextView account;
                TextView password;
                account = findViewById(R.id.editTextAccount);
                password = findViewById(R.id.editTextPassword);
                URL url = new URL("http://59.126.170.89/FIRE.php/");
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

                        result= j.getString("fire");
                        Log.e("da",result);
                        result = result.replace("[","");
                        result = result.replace("]","");
                        result = result.replace("\"","");
                        b = result.split(",");

                        result= j.getString("count");


                        count = Integer.parseInt(result);
                        result = j.getString("address");
                        result = result.replace("[","");
                        result = result.replace("]","");
                        result = result.replace("\"","");
                        address = result.split(",");
                        Log.e("789",address[5]);


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
                    result="";
                }
            });
        }
    };




}