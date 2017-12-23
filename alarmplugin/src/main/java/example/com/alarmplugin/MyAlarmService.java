package example.com.alarmplugin;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyAlarmService extends Service {
    private static final String TAG = MyAlarmService.class.getSimpleName();

    private Intent amIntent;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        amIntent = intent;

        Thread thr = new Thread(null, mTask, "MyAlarmServiceThread");
        thr.start();

        return START_STICKY_COMPATIBILITY;
    }

    // アラーム用サービス
    Runnable mTask = new Runnable() {
        public void run() {
            // アラームを受け取るActivityを指定
            Intent alarmBroadcast = new Intent();

            // ここでActionをセットする(Manifestに書いたものと同じであれば何でもよい)
            String action = amIntent.getAction();
            alarmBroadcast.setAction(action);

            // put extra
            alarmBroadcast.putExtras(amIntent);


            String type = amIntent.getType();
            Log.i(TAG, type);
            //alarmBroadcast.setType(type);


            // レシーバーへ渡す
            sendBroadcast(alarmBroadcast);

            // 役目を終えたサービスを止める
            MyAlarmService.this.stopSelf();
        }
    };
}
