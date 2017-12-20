package example.com.alarmplugin;

import java.util.Calendar;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class MyAlarmManager {

    public static final String ACTION_ALARM = "ACTION_ALARM";
    public static final String ACTION_NOTIFICATION = "ACTION_NOTIFICATION";

    public static Context context;

    private static AlarmManager am;
    private static PendingIntent mAlarmSender;

    private static final String TAG = MyAlarmManager.class.getSimpleName();

    public MyAlarmManager(){

    }

    public static void addAlarm(Context con, int id, int secondsFromNow) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, secondsFromNow);
        addAlarm(con, id, c);
    }

    public static void addAlarm(Context con, int id, Calendar c){
        Intent intent = new Intent(con, MyAlarmService.class);
        intent.setType("pri_id" + id);
        intent.setAction(ACTION_ALARM);
        intent.putExtra("action", ACTION_ALARM);
        setAlarmManager(con, id, intent, c);
    }

    public static void addNotification(Context con, int id, String name, String title, String label,  int secondsFromNow) {
        context = con;
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, secondsFromNow);
        addNotification(con, id, name, title, label, c);
    }

    public static void addNotification(Context con, int id, String name, String title, String label,  Calendar c){

        Intent intent = new Intent(con, MyAlarmService.class);
        intent.setType("pri_id" + id);
        intent.setAction(ACTION_NOTIFICATION);
        intent.putExtra("action", ACTION_NOTIFICATION);
        intent.putExtra("name", name);
        intent.putExtra("title", title);
        intent.putExtra("label", label);
        intent.putExtra("pri_id", id);

        setAlarmManager(con, id, intent, c);
    }

    private static void setAlarmManager(Context con, int id, Intent intent, Calendar c){
        PendingIntent pendingIntent = PendingIntent.getService(con, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager)con.getSystemService(con.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    public static void stopAlarm(Context con, int id) {
        // アラームのキャンセル
        Log.d(TAG, "stopAlarm()");
        Intent intent = new Intent(con, MyAlarmService.class);
        intent.setType("pri_id" + id);
        PendingIntent sender = PendingIntent.getBroadcast(con, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm = (AlarmManager)con.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(sender);
        sender.cancel();
    }
}