package example.com.notificationtest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class Notifier extends BroadcastReceiver {
    public  final String TAG = Notifier.class.getName();

    @Override
    public void onReceive(Context content, Intent intent) {

        Toast.makeText(content, "Alarm Received!", Toast.LENGTH_SHORT).show();
        Log.d("AlarmReceiver", "Alarm Received! : " + intent.getIntExtra(Intent.EXTRA_ALARM_COUNT, 0));

        //通知がクリックされた時に発行されるIntentの生成
        Intent sendIntent = new Intent(content, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(content, 0, sendIntent, 0);

        //通知オブジェクトの生成
        Notification noti = new NotificationCompat.Builder(content)
                .setTicker("お時間ですよ!")
                .setContentTitle("通知")
                .setContentText("設定した時間がきました")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(new long[]{0, 200, 100, 200, 100, 200})
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager manager = (NotificationManager)content.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, noti);

    }

}
