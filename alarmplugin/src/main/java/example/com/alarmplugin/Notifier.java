package example.com.alarmplugin;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerNativeActivity;

public class Notifier extends BroadcastReceiver {
    public  final String TAG = Notifier.class.getName();

    @Override
    public void onReceive(Context content, Intent intent) {

        Log.d("AlarmReceiver", "Alarm Received! : " + intent.getIntExtra(Intent.EXTRA_ALARM_COUNT, 0) + " " + intent.getType());

        //通知がクリックされた時に発行されるIntentの生成
        Intent sendIntent = new Intent(content, UnityPlayerNativeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(content, 0, sendIntent, 0);

        Integer priId = intent.getIntExtra("pri_id" ,0);

        //通知オブジェクトの生成
        Notification noti = new NotificationCompat.Builder(content)
                .setTicker(intent.getStringExtra("ticker"))
                .setContentTitle( intent.getStringExtra("title"))
                .setContentText( intent.getStringExtra("text"))
                .setSmallIcon(R.drawable.icon)
                .setVibrate(new long[]{0, 200, 100, 200, 100, 200})
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager manager = (NotificationManager)content.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(priId, noti);

        // 実行されたので削除
        MyAlarmManager.deleteNotificationAlarmFromPrefs(content, priId);
    }

}

