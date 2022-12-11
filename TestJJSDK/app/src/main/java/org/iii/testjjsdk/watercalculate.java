package org.iii.testjjsdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class watercalculate extends AppCompatActivity {
    private Spinner spnPrefer3,spnPrefer5;
    private Button btn_ok3,button25;
    private TextView textView10;
    ArrayAdapter<String> adapter;
    String result = "";
    String suggest="";
    int count = 0;
    String[] fire;
    String[] b;
    String[] fire_result;
    Context context = this;
    int rid = 0;
    String itemname = "";
    String temp_result = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watercalculate);
        Thread thread = new Thread(firecount);
        thread.start();

        spnPrefer3 = (Spinner) findViewById(R.id.spnPrefer3);
        spnPrefer5 = (Spinner) findViewById(R.id.spnPrefer5);
        btn_ok3 = (Button) findViewById(R.id.btn_ok3);
        textView10 = (TextView)findViewById(R.id.textView10);
        btn_ok3.setOnClickListener(listener);
        while(fire_result==null)
        {
            textView10.setText("讀取中");
        }
        spnPrefer5.setOnItemSelectedListener(spnOnItemSelected);

        button25 = (Button) findViewById(R.id.button25);
        button25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(watercalculate.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }
    private Button.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch(spnPrefer3.getSelectedItemPosition()) {
                case 0:
                    intent.setClass(watercalculate.this, water_requirement_Qv_1.class);
                    startActivity(intent);
                    break;
                case 1:
                    intent.setClass(watercalculate.this, water_requirement_Qv_2.class);
                    startActivity(intent);
                    break;
                case 2:
                    intent.setClass(watercalculate.this, water_requirement_Qv_3.class);
                    startActivity(intent);
                    break;
            }
        }
    };
    private AdapterView.OnItemSelectedListener spnOnItemSelected
            = new AdapterView.OnItemSelectedListener() {
        @SuppressLint("SetTextI18n")
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            String sPos=String.valueOf(pos);
            String sInfo=parent.getItemAtPosition(pos).toString();
            itemname = sInfo;
            rid = pos;
            textView10.setText("");
            cal();
        }
        public void onNothingSelected(AdapterView<?> parent) {
            //
        }
    };
    private void cal(){
        switch (fire_result[rid]){
            case "fire_early":
                suggest = "\n推薦水量公式:閃燃前燃燒";
                break;
            case "fire_growth_period":
                suggest = "\n推薦水量公式:最盛期通風狀態下穩態燃燒";
                break;
            case "fire_peak_period":
                suggest = "\n推薦水量公式:最盛期通風及熱傳下燃燒";
                break;
            case "fire_decline_period":
                suggest = "\n推薦水量公式:閃燃前燃燒";
                break;
        }
        textView10.setText("地點:"+itemname+"\n火焰週期:"+fire_result[rid]+suggest);

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
                        temp_result = j.getString("address");
                        temp_result = temp_result.replace("[","");
                        temp_result = temp_result.replace("]","");
                        temp_result = temp_result.replace("\"","");
                        fire = temp_result.split(",");
                        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, fire);
                        spnPrefer5.setAdapter(adapter);
                        result = j.getString("result");
                        result = result.replace("[","");
                        result = result.replace("]","");
                        result = result.replace("\"","");
                        fire_result = result.split(",");

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