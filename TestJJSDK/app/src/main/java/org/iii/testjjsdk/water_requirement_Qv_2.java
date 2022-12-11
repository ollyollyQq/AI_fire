package org.iii.testjjsdk;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class water_requirement_Qv_2 extends AppCompatActivity {
    private TextView Text4;
    private Button btn1,btn2,button27;
    private EditText edt1;
    private EditText edt2;
    int b=0,c=1,e=0;
    double e1=0,e2=0,e3=0,e4=0,e5=0,e6=0,e7=0,z1,z2,y=0;
    private DecimalFormat decimalFormat = new DecimalFormat(".00");

    List a = new ArrayList();
    List a2 = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.water_requirement_qv2);

        edt2 =(EditText) findViewById(R.id.edt2) ;
        edt1 =(EditText) findViewById(R.id.edt1) ;
        Text4 = (TextView) findViewById(R.id.Text4);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn1.setOnClickListener(lsn);
        btn2.setOnClickListener(lsn2);
        Text4.setMovementMethod(ScrollingMovementMethod.getInstance());

        button27 = (Button) findViewById(R.id.button27);
        button27.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(water_requirement_Qv_2.this, watercalculate.class);
                startActivity(intent);

            }
        });

    }
    private Button.OnClickListener lsn=(v) ->{
        if(edt1.getText().toString().matches("") || edt2.getText().toString().matches("")){
            Toast toast = Toast.makeText(water_requirement_Qv_2.this, "欄位不能是空白!!", Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            Text4.setText(String.valueOf("目前已輸入數值" + "\n"));
            z1 = Double.parseDouble(edt1.getText().toString());
            z2 = Double.parseDouble(edt2.getText().toString());
            a.add(z1);
            a.add(z2);
            for (int b = 0; b <= (a.size() - 2); b = b + 2) {
                Text4.append(String.valueOf(c + ".長:" + a.get(b)));
                Text4.append(String.valueOf(" 寬:" + a.get((b + 1)) + "\n"));
                c = c + 1;
            }
            b = 0;
            c = 1;
            a2.add(z1 * z2 * z2);
            a2.add(z1 * z2);
            for (int b = 0; b <= (a2.size() - 2); b = b + 2) {
                e1 = e1 + (double) a2.get(b);
                e2 = e2 + (double) a2.get((b + 1));
            }
            e3 = e1 / e2;
            e7 = Math.pow(e3, 0.5);

            e4 = e7 * 5.5 * e2;
            e5 = e4 * 18 / 60;
            e6 = e5 * 0.58 * 60;
            y = (e6 / 600);
//            String e61 = decimalFormat.format(e6);
            String e31 = decimalFormat.format(e3);
            String e41 = decimalFormat.format(e4);
            String e51 = decimalFormat.format(e5);
            String e61 = decimalFormat.format(e2);
            Text4.append(String.valueOf("水量計算:\n" +"Mw:" + Math.floor((e6 * 100.0) / 100.0)+ "L/min\n"+Math.round((Math.ceil(y*1.0)/1.0))+"條(65A水帶瞄子 600L/min)"));
            b = 0;
            c = 1;
            e1 = 0;
            e2 = 0;
            e3 = 0;
        }

    };
    private Button.OnClickListener lsn2=(v) ->{
        if(a.isEmpty()||a2.isEmpty())
        {
            Text4.setText(String.valueOf("請輸入數值"));
        }
        else {
            for (int d = 1; d <= 2; d++)//假設現在動態陣列a的集合元素是這樣 -> [a,b,c,d,e]
            {
                Object obj = a.remove((a.size() - 1));
            }
            Text4.setText(String.valueOf("目前已輸入數值" + "\n"));
            for (int b = 0; b <= (a.size() - 2); b = b + 2) {
                Text4.append(String.valueOf(c + ".長:" + a.get(b)));
                Text4.append(String.valueOf(" 寬:" + a.get((b + 1)) + "\n"));
                c = c + 1;
            }
            b = 0;
            c = 1;
            for (int d = 1; d <= 2; d++) {
                Object obj2 = a2.remove((a2.size() - 1));
            }
            for (int b = 0; b <= (a2.size() - 2); b = b + 2) {
                e1 = e1 + (double) a2.get(b);
                e2 = e2 + (double) a2.get((b + 1));
            }
            e3 = e1 / e2;
            e7 = Math.pow(e3, 0.5);
            e4 = e7 * 5.5 * e2;
            e5 = e4 * 18 / 60;
            e6 = e5 * 0.58 * 60;
            y = (e6 / 600);
            String e61 = decimalFormat.format(e2);
            String e31 = decimalFormat.format(e3);
            String e41 = decimalFormat.format(e4);
            String e51 = decimalFormat.format(e5);
            Text4.append(String.valueOf("水量計算:\n" +  "Mw:" + Math.floor((e6 * 100.0) / 100.0)+ "L/min\n"+Math.round((Math.ceil(y*1.0)/1.0))+"條(65A水帶瞄子 600L/min)"));
            b = 0;
            c = 1;
            e1 = 0;
            e2 = 0;
            e3 = 0;
        }
    };
}