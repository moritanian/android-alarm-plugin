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

        Integer priId = intent.getIntExtra(MyAlarmManager.PRIMARY_ID_KEY ,0);

        //通知がクリックされた時に発行されるIntentの生成
        Intent sendIntent = new Intent(content, getAppClass());
        sendIntent.putExtra(MyAlarmManager.PRIMARY_ID_KEY, priId);
        /**
         * OnCLickActivity経由でたちあげたアプリでアプリを切らずに再度
         * push通知させてクリックしたときの問題
         * 下のを有効にするとなにも立ち上がらない
         * コメントアウトすると立ち上がるがOnClickActivityのonCreateがよばれずunityが起動
         *
         * ちなみに Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS はバックグラウンドにいくいと消えるので注意
         */
        sendIntent.setFlags(
             //   Intent.FLAG_ACTIVITY_CLEAR_TOP  // 起動中のアプリがあってもこちらを優先する
                    Intent.FLAG_ACTIVITY_NEW_TASK
           //             | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED  // 起動中のアプリがあってもこちらを優先する
                    //    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS  // 「最近利用したアプリ」に表示させない
        );
        PendingIntent pendingIntent = PendingIntent.getActivity(content, priId, sendIntent
                , PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        MyAlarmManager myAlarmManager = new MyAlarmManager(content);

        //通知オブジェクトの生成
        Notification noti = new NotificationCompat.Builder(content)
                .setTicker(intent.getStringExtra("ticker"))
                .setContentTitle( intent.getStringExtra("title"))
                .setContentText( intent.getStringExtra("text"))
                .setSmallIcon(myAlarmManager.getNotificationIconResourceId())
                .setVibrate(new long[]{0, 200, 100, 200, 100, 200})
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setNumber(1)
                .setContentIntent(pendingIntent)

                .build();

        NotificationManager manager = (NotificationManager)content.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(priId, noti);

        // 実行されたので削除
        myAlarmManager.deleteNotificationAlarmFromPrefs(priId);

    }

    // please override if need.
    protected Class<?> getAppClass(){
        return  NotificationOnClickActivity.class;
    }

}

