package org.iii.testjjsdk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivityfire extends AppCompatActivity {
    private Button button20, button5, button7, button8, button13, button6,button1,button2;
    private static final Object TAG = "PushNotification";
    private static final String CHANNEL_ID = "101";
    String id;
    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activityfire);
        createNotificationChannel();
        subscribeToTopic();
        Intent intent = getIntent();
        id = intent.getStringExtra("member_id");


        button20 = (Button) findViewById(R.id.button20);
        button20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("member_id", id);
                intent.setClass(MainActivityfire.this, Member.class);
                startActivity(intent);
            }
        });


        button7 = (Button) findViewById(R.id.button7);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.putExtra("member_id", id);
                intent.setClass(MainActivityfire.this, Opinion.class);
                startActivity(intent);

            }
        });
        button8 = (Button) findViewById(R.id.button8);
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.putExtra("member_id", id);
                intent.putExtra("check", "glass");
                intent.setClass(MainActivityfire.this, message_group.class);
                startActivity(intent);


            }
        });


        button13 = (Button) findViewById(R.id.button13);
        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("member_id", id);

                intent.setClass(MainActivityfire.this, DetectorActivity.class);
                startActivity(intent);


            }
        });

        button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();

                intent.setClass(MainActivityfire.this, GPS.class);
                startActivity(intent);


            }
        });
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();

                intent.setClass( MainActivityfire.this,watercalculate.class);
                startActivity(intent);



            }
        });

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.putExtra("member_id", id);
                intent.putExtra("check", "people");
                intent.setClass( MainActivityfire.this,message_group.class);
                startActivity(intent);



            }
        });

    }
    private void subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic("123")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivityfire.this, "subscribed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "firebaseNotifChannel";
            String description = "Receive firebase notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}