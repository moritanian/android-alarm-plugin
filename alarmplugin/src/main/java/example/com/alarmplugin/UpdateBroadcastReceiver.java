package example.com.alarmplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpdateBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Log.i("UpdateBroadcastReceiver", "onReceive");

        String action = intent.getAction();
        String packagePath = intent.getDataString(); // package:app.package.name

        MyAlarmManager.addAlarm(context, 0, 1);
    }
}