package example.com.notificationtest;
import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
    public final String TAG = MainActivity.class.getName();

    private static final String ACTION_TEXT_UPDATE = "example.com.ACTION_TEXT_UPDATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button)findViewById(R.id.button1);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");

                //呼び出す日時を設定する
                Calendar triggerTime = Calendar.getInstance();
                triggerTime.add(Calendar.SECOND, 5);	//今から5秒後

                //設定した日時で発行するIntentを生成
                Intent intent = new Intent(MainActivity.this, Notifier.class);
                //Intent intent = new Intent();
                //intent.setAction(ACTION_TEXT_UPDATE);

                PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent,0);

                AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
                manager.set(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis(), sender);

                finish();
            }

        });
    }

    protected void onResume() {

        super.onResume();

        //Intent Filter登録

        IntentFilter filter = new IntentFilter();

        filter.addAction(ACTION_TEXT_UPDATE);

        registerReceiver(broadcastReceiver,filter);  //BroadCastreceiverに登録

    }

    protected void onPause() {

        super.onPause();

        //登録解除

        unregisterReceiver(broadcastReceiver);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){ // インナーサブクラス



        @Override
        public void onReceive(Context context, Intent intent){

            Log.i("broadcastreceiver", "onReceive");

        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
