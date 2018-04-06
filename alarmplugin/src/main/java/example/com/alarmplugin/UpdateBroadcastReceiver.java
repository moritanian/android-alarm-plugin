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

        switch(action) {
            case Intent.ACTION_BOOT_COMPLETED:
                Log.i("UpdateBroadcastReceiver", "BOOT_COMPLETED");

                break;
            case Intent.ACTION_MY_PACKAGE_REPLACED:
                Log.i("UpdateBroadcastReceiver", "MyPackageReplaced");
            default:
                break;
        }

        Log.i("UpdateBroadcastReceiver", action.toString());

        MyAlarmManager.reregister(context);
    }
}
