package example.com.alarmplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Moritanian on 2017/12/20.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // アラームを受け取って起動するActivityを指定、起動
        Log.i("AlarmReceiver", "onReceive");
        Intent notification = new Intent(context, AlarmNotificationActivity.class);
        notification.putExtras(intent);

        // 画面起動に必要
        notification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(notification);

        // 実行したので削除
        Integer priId = intent.getIntExtra("pri_id" ,0);
        MyAlarmManager.deleteAlarmFromPrefs(context, priId);
    }
}
