package example.com.notificationtest;
import java.util.Calendar;


import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
    public final String TAG = MainActivity.class.getName();

    private static final String ACTION_TEXT_UPDATE = "example.com.ACTION_TEXT_UPDATE";

    private int aid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn1 = (Button)findViewById(R.id.button1);
        btn1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick1");
                MyAlarmManager.addNotification(MainActivity.this, aid, "通知名", "通知タイトル", "通知ラベル", 5);
                aid ++;
            }

        });

        Button btn2 = (Button)findViewById(R.id.button2);
        btn2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick2");
                MyAlarmManager.addAlarm(MainActivity.this, aid, 5);
                aid ++;

            }

        });
    }

}
