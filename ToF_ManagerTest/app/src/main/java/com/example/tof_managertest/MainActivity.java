package com.example.tof_managertest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jorjin.jjsdk.tof.TofDevicesAttachListener;
import com.jorjin.jjsdk.tof.TofDevicesStatusListener;

import com.jorjin.jjsdk.tof.TofIncomingFrameListener;
import com.jorjin.jjsdk.tof.TofManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TofIncomingFrameListener, TofDevicesAttachListener, TofDevicesStatusListener {

    private TofIncomingFrameListener tofIncommingFrameListener = this;
    private TofManager tofManager;
    private Context context = this;
    private int ToF_SigStrengthAvg = 0, Tof_SigDistanceAvg = 0;

    private int collectSigStrength(ArrayList BeenfilteredArraylist){
        ArrayList<Integer> collectSigStrengthArray = new ArrayList();
        int sumStrengthAllcollected = 0;
        int AvgStrengthOfAllcollected = 0;
        for(int i=0;i<BeenfilteredArraylist.size();i++){
            collectSigStrengthArray.add((Integer)((ArrayList)(BeenfilteredArraylist.get(i))).get(3));
        }
        if(collectSigStrengthArray.size()!=0 && collectSigStrengthArray.size() == BeenfilteredArraylist.size()) {
            for (int i = 0; i < collectSigStrengthArray.size(); i++) {
                sumStrengthAllcollected += collectSigStrengthArray.get(i);
            }
        }

        AvgStrengthOfAllcollected = Math.round(sumStrengthAllcollected/collectSigStrengthArray.size());
        return AvgStrengthOfAllcollected;
    }

    private int collectSigDistance(ArrayList BeenfilteredArraylist){
        ArrayList<Integer> collectSigDistanceArray = new ArrayList();
        int sumDistanceAllcollected = 0;
        int AvgDistanceOfAllcollected = 0;
        for(int i=0;i<BeenfilteredArraylist.size();i++){
                collectSigDistanceArray.add((Integer)((ArrayList)(BeenfilteredArraylist.get(i))).get(4));
        }
        if(collectSigDistanceArray.size()!=0 && collectSigDistanceArray.size() == BeenfilteredArraylist.size()) {
            for (int i = 0; i < collectSigDistanceArray.size(); i++) {
                sumDistanceAllcollected += collectSigDistanceArray.get(i);
            }
        }

        AvgDistanceOfAllcollected = Math.round(sumDistanceAllcollected/collectSigDistanceArray.size());
        return AvgDistanceOfAllcollected;
    }

    private TextView ToFStrenghtAvgValue,ToFDistanceAvgValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToFStrenghtAvgValue = (TextView)findViewById(R.id.ToFStrenghtAvgValue);
        ToFDistanceAvgValue = (TextView)findViewById(R.id.ToFDistanceAvgValue);

        tofManager = new TofManager(context);
        tofManager.setTofDevicesAttachListener(this);
        tofManager.setTofFrameListener(this);
        tofManager.setTofDevicesStatusListener(this);
        tofManager.open();
    }

    private void setTextView(){
        ToFDistanceAvgValue.setText(String.valueOf(Tof_SigDistanceAvg/4));
        ToFStrenghtAvgValue.setText(String.valueOf(ToF_SigStrengthAvg/2048));
    }

    @Override
    public void onTofIncomingFrame(ArrayList neededframe) {
        ArrayList BeenfilteredArraylist = new ArrayList();

        BeenfilteredArraylist = validDataFilter(neededframe);
        if(BeenfilteredArraylist.size()>0){
            ToF_SigStrengthAvg = collectSigStrength(BeenfilteredArraylist);
            Tof_SigDistanceAvg = collectSigDistance(BeenfilteredArraylist);
            refreshDataText();
        }
    }

    private ArrayList validDataFilter(ArrayList neededframe) {
        ArrayList filteredArraylist = new ArrayList();

        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                if(((Integer) ((ArrayList)((ArrayList)(neededframe.get(i))).get(j)).get(3)/2048)>200){
                    filteredArraylist.add((ArrayList)((ArrayList)(neededframe.get(i))).get(j));
                }
            }
        }
        return filteredArraylist;
    }

    private void refreshDataText() {
        runOnUiThread(() -> {
            setTextView();
        });
    }

    @Override
    public void onTofDevicesAttached(boolean b) {

    }

    @Override
    public void onTofDevicesIsPlugIn(boolean b) {

    }

    @Override
    public void onTofDevicesStatus(boolean b) {

    }
}