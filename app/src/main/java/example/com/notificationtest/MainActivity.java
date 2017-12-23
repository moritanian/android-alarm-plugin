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
import android.widget.EditText;

public class MainActivity extends Activity {
    public final String TAG = MainActivity.class.getName();

    private static final String ACTION_TEXT_UPDATE = "example.com.ACTION_TEXT_UPDATE";

    private int aid = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn1 = (Button)findViewById(R.id.button1);
        btn1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick1");
                MyAlarmManager.addNotification(MainActivity.this, aid, "通知名" + aid, "通知タイトル" + aid, "通知ラベル" + aid, getTimeSec());
                aid ++;
                setCurrentId(aid);
            }

        });

        Button btn2 = (Button)findViewById(R.id.button2);
        btn2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick2");
                MyAlarmManager.addAlarm(MainActivity.this, aid, getTimeSec());
                aid ++;
                setCurrentId(aid);
            }

        });

        Button btn3 = (Button)findViewById(R.id.button3);
        btn3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick3");
                MyAlarmManager.resetAll(MainActivity.this);
                MyAlarmManager.clearNotification(MainActivity.this, getCurrentId());
            }

        });

        Button btn4 = (Button)findViewById(R.id.button4);
        btn4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick4");
                MyAlarmManager.addNotification(MainActivity.this, aid-1, "name2", "title2", "label2", getTimeSec());
            }

        });

        Button btn5 = (Button)findViewById(R.id.button5);
        btn5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick5");
                android.os.Process.killProcess(android.os.Process.myPid());

            }

        });

        Button btn6 = (Button)findViewById(R.id.button6);
        btn6.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick6");
                MyAlarmManager.reregisterAlarm(MainActivity.this);

            }

        });
        Button btn7 = (Button)findViewById(R.id.button7);
        btn7.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick7");
                /*
                for (int i=0;i<3; i++) {
                    MyAlarmManager.addNotification(MainActivity.this, i+100, "multi" + i , "multi" + i , "multi" + i, getTimeSec());
                }*/
                for (int i=10;i<15; i+=1) {
                    MyAlarmManager.addNotification(MainActivity.this, i+100, "multi" + i , "multi" + i , "multi" + i, getTimeSec() + i - 9);
                }

                for (int i=15;i<20; i+=1) {
                    MyAlarmManager.addNotification(MainActivity.this, i+100, "multi" + i , "multi" + i , "multi" + i, getTimeSec() + (i-12)*3 );
                }

                MyAlarmManager.reregisterAlarm(MainActivity.this);
            }

        });
    }

    private int getTimeSec(){
        final EditText timeSec = (EditText)findViewById(R.id.time_sec);
        return Integer.parseInt(timeSec.getText().toString());
    }

    private int getCurrentId(){
        final EditText currentIdText = (EditText)findViewById(R.id.current_id);
        return Integer.parseInt(currentIdText.getText().toString());
    }

    private void setCurrentId(int id){
        final EditText currentIdText = (EditText)findViewById(R.id.current_id);
        currentIdText.setText(Integer.toString( id));
    }

}
