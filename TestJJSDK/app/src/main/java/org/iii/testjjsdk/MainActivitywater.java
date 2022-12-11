package org.iii.testjjsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class MainActivitywater extends AppCompatActivity {
    private Spinner sp,sp2;

    int a1=0,a2,a3,a4;

    String[] hr1={"3","2","2","3","2","2","1","2"};
    String[] hr2={"1","2","3","4","5","6","7","8","9","10"};

    private Button bt1,button20;
    private TextView t7;
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.water);

        bt1 = (Button) findViewById(R.id.bt1);
        sp = (Spinner) findViewById(R.id.sp);
        sp2 = (Spinner) findViewById(R.id.sp2);
        t7 = (TextView) findViewById(R.id.t7);
        et =(EditText) findViewById(R.id.et) ;
        bt1.setOnClickListener(lsn);

        button20 = (Button) findViewById(R.id.button7);
        button20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(MainActivitywater.this, MainActivity.class);
                startActivity(intent);

            }
        });

        // 設定 sp 元件 ItemSelected 事件的 listener
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                String result = parent.getItemAtPosition(position).toString();
                a1 = Integer.parseInt(hr1[position]);
                a3=a1+a2;
                t7.setText(String.valueOf("計算公式V=Q+S\n"+"需要時間"+ a3 +"hr\n"+"總計水量:請輸入水流並計算") );



            }

            @Override
            public void onNothingSelected(AdapterView parent) {

            }
        });
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                String result = parent.getItemAtPosition(position).toString();
                a2 = Integer.parseInt(hr2[position]);
                a3=a1+a2;
                t7.setText(String.valueOf("計算公式V=Q+S\n"+"需要時間"+ a3 +"hr\n"+"總計水量:請輸入水流並計算") );
            }

            @Override
            public void onNothingSelected(AdapterView parent) {

            }
        });


    }
    private Button.OnClickListener lsn=(v) ->{
        a4= Integer.parseInt(et.getText().toString());


        t7.setText(String.valueOf("計算公式V=Q+S\n"+"需要時間"+ a3 +"hr\n"+"總計水量"+ a4*360*a3+" L 總計水量\n") );



    };








}
