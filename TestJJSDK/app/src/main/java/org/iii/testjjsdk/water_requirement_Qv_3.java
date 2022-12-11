package org.iii.testjjsdk;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class water_requirement_Qv_3 extends AppCompatActivity {
    private Spinner spnPrefer;
    private TextView Text4;
    private Button btn1,btn2,button28;
    private EditText edt1,edt3,edt4,edt5;
    private EditText edt2;
    int b=0,c=1,e=0,a3=0;
    double e1=0,e2=0,e3=0,e4=0,e5=0,e6=0,e7=0,z1,z2,j,q,k,Qfo=0,At=0;
    double E=0,Kc=0.07,te=0,w=0,x=0,Qe=0,y=0,g=0,f=0;
    private DecimalFormat decimalFormat = new DecimalFormat(".00");
    String[] type1={"480","400","480","960","480","480","2240","240","960","1500","400","400","400","440","560","560","480","960","240","720"};
    List a = new ArrayList();
    List a2 = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.water_requirement_qv3);
        edt3 =(EditText)findViewById(R.id.edt3);
        edt4 =(EditText)findViewById(R.id.edt4);
        edt5 =(EditText)findViewById(R.id.edt5);
        edt2 =(EditText) findViewById(R.id.edt2) ;
        edt1 =(EditText) findViewById(R.id.edt1) ;
        Text4 = (TextView) findViewById(R.id.Text4);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn1.setOnClickListener(lsn);
        btn2.setOnClickListener(lsn2);
        Text4.setMovementMethod(ScrollingMovementMethod.getInstance());
        spnPrefer = (Spinner) findViewById(R.id.spnPrefer);

        spnPrefer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                String result = parent.getItemAtPosition(position).toString();
                a3 = Integer.parseInt(type1[position]);
            }
            @Override
            public void onNothingSelected(AdapterView parent) {
            }
        });
        button28 = (Button) findViewById(R.id.button28);
        button28.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(water_requirement_Qv_3.this, watercalculate.class);
                startActivity(intent);

            }
        });
    }
    private Button.OnClickListener lsn=(v) ->{
        if(edt1.getText().toString().matches("") || edt2.getText().toString().matches("") || edt3.getText().toString().matches("") || edt4.getText().toString().matches("") || edt5.getText().toString().matches("")){
            Toast toast = Toast.makeText(water_requirement_Qv_3.this, "欄位不能是空白!!", Toast.LENGTH_LONG);
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
            //j=長,q=寬,k=高
            //e3==H
            j = Double.parseDouble(edt3.getText().toString());
            q = Double.parseDouble(edt4.getText().toString());
            k = Double.parseDouble(edt5.getText().toString());
            E = a3 * (j * q);
            At = (((j * q) + (q * k) + (j * k)) * 2 - e2);
            x = Math.pow((e7 * At * e2), 0.5);
            w = (j * q) / x;
            te = a3 * Kc * w;
            Qe = E / (te * 60);
            g = (double) ( Math.ceil(Qe * 10.0) / 10.0);
            e6 = 0.58 * g * 60;
            f=(double)(Math.ceil(e6*1.0)/1.0);
            y = (e6 / 600);
            String e61 = decimalFormat.format(e6);
            String e51 = decimalFormat.format(Qe);
            String e41 = decimalFormat.format(At);
            String tee = decimalFormat.format(te);
            String Avv = decimalFormat.format(e2);
            String e33 = decimalFormat.format(e3);
            String ww = decimalFormat.format(w);
            Text4.append(String.valueOf("水量計算:\n" +  "Mw:" + f + "L/min\n"+Math.round((Math.ceil(y*1.0)/1.0))+"條(65A水帶瞄子 600L/min)"));
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
            //假設現在動態陣列a的集合元素是這樣 -> [a,b,c,d,e]
            for (int d = 1; d <= 2; d++)
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
            //j=長,q=寬,k=高
            //e3==H
            j=Double.parseDouble(edt3.getText().toString());
            q=Double.parseDouble(edt4.getText().toString());
            k=Double.parseDouble(edt5.getText().toString());
//            if (Double.isNaN(e3)) {
//                e3=0;
//            }
//            if(Double.isNaN(Qfo))
//            {
//                Qfo=0;
//            }
//            if(Double.isNaN(e6))
//            {
//                e6=0;
//            }
//            if(Double.isNaN(e2))
//            {
//                e2=0;
//            }
            E = a3*(j*q);
            At =(((j*q)+(q*k)+(j*k))*2-e2);
            x=Math.pow((e7*At*e2),0.5);
            w = (j*q)/x;
            te = a3*Kc*w;
            Qe = E/(te*60);
            g = (double) ( Math.ceil(Qe * 10.0) / 10.0);
            e6 = 0.58*g*60;
            f=(double)(Math.ceil(e6*1.0)/1.0);
            if(e2==0)
            {
                At=0;
            }
            else{
                At = (((j*q)+(q*k)+(j*k))*2-e2);
            }
            y = (e6 / 600);
            String e61 = decimalFormat.format(e6);
            String e51 = decimalFormat.format(Qe);
            String e41 = decimalFormat.format(At);
            String tee = decimalFormat.format(te);
            String Avv = decimalFormat.format(e2);
            String e33 = decimalFormat.format(e3);
            String ww = decimalFormat.format(w);
            Text4.append(String.valueOf("水量計算:\n" +  "Mw:" + f + "L/min\n"+Math.round((Math.ceil(y*1.0)/1.0))+"條(65A水帶瞄子 600L/min)"));
            b = 0;
            c = 1;
            e1 = 0;
            e2 = 0;
            e3 = 0;
        }
    };
}