package example.com.notificationtest;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Timer;
import java.util.TimerTask;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


public class MyAlarmManager {

    public static final String ACTION_ALARM = "ACTION_ALARM";
    public static final String ACTION_NOTIFICATION = "ACTION_NOTIFICATION";

    public static final  String PRIMARY_ID_KEY = "pri_key";
    public static final  String ACTION_KEY = "action";

    private static final String PREFS_NAME = "alarm-manager-prefs";
    private static final String NOTIFICATION_IDS_PREFS_KEY = "notification-ids-prefs-key";
    private static final String ALARM_IDS_PREFS_KEY = "alarm-ids-prefs-key";


    private static final String TAG = MyAlarmManager.class.getSimpleName();

    private static SharedPreferences dataStore;
    private static SharedPreferences.Editor editor;

    public static void init(Context con){
        reregister(con);
    }

    public static void resetAllAlarms(Context con) {
        setPrefs(con);
        for (int id : getAlarmIds(con)) {
            resetAlarm(con, id);
        }
    }

    public static void resetAllNotificatios(Context con){
        setPrefs(con);
        for (int id : getNotificationIds(con)) {
            resetNotification(con, id);
        }
    }

    public static void resetAll(Context con){
        resetAllAlarms(con);
        resetAllNotificatios(con);
    }


    public static void addAlarm(Context con, int id, int secondsFromNow) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, secondsFromNow);
        addAlarm(con, id, c);
    }

    public static void addAlarm(Context con, int id, Calendar c) {
        Intent intent = new Intent(con, MyAlarmService.class);
        intent.setType(getAlarmIntentType(id));
        intent.setAction(ACTION_ALARM);
        intent.putExtra(ACTION_KEY, ACTION_ALARM);
        intent.putExtra(PRIMARY_ID_KEY, id);
        setAlarmManager(con, id, intent, c);

        setPrefs(con);
        editor.putLong("alarm-time" + id, c.getTimeInMillis());
        HashSet<String> alarmIds = (HashSet<String>) dataStore.getStringSet(ALARM_IDS_PREFS_KEY, new HashSet<String>());
        alarmIds.add(Integer.toString(id));
        editor.putStringSet(ALARM_IDS_PREFS_KEY, alarmIds);
        editor.apply();
    }

    public static void addNotification(Context con, int id, String title, String ticker, String text, int secondsFromNow) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, secondsFromNow);
        addNotification(con, id, title, ticker, text, c);
    }

    public static void addNotification(Context con, int id, String title, String ticker, String text, Calendar c) {

        Intent intent = new Intent(con, MyAlarmService.class);
        String type =  getNotificationIntentType(id);
        intent.setType(type);
        intent.setAction(ACTION_NOTIFICATION);
        intent.putExtra(ACTION_KEY, ACTION_NOTIFICATION);
        intent.putExtra("title", title);
        intent.putExtra("ticker", ticker);
        intent.putExtra("text", text);
        intent.putExtra(PRIMARY_ID_KEY, id);

        setAlarmManager(con, id, intent, c);


        setPrefs(con);
        editor.putString("title" + id, title);
        editor.putString("ticker" + id, ticker);
        editor.putString("text" + id, text);
        editor.putLong("notification-time" + id, c.getTimeInMillis());
        HashSet<String> notificationIds = (HashSet<String>) dataStore.getStringSet(NOTIFICATION_IDS_PREFS_KEY, new HashSet<String>());
        notificationIds.add(Integer.toString(id));
        editor.putStringSet(NOTIFICATION_IDS_PREFS_KEY, notificationIds);
        editor.apply();

    }

    private static void setAlarmManager(Context con, int id, Intent intent, Calendar c) {
        Log.i(TAG, "setAlarmManager " + Integer.toString(id) + " " + c.getTimeInMillis());
        PendingIntent pendingIntent = PendingIntent.getService(con, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) con.getSystemService(con.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    public static void resetAlarm(Context con, int id) {
        // アラームのキャンセル
        Log.d(TAG, "resetAlarm");
        Intent intent = new Intent(con, MyAlarmService.class);
        intent.setType(getAlarmIntentType(id));
        cancelAmarmManager(con, id, intent);
        deleteAlarmFromPrefs(con, id);
    }

    public static void resetNotification(Context con, int id) {
        // アラームのキャンセル
        Log.d(TAG, "resetNotification()");
        Intent intent = new Intent(con, MyAlarmService.class);
        intent.setType(getNotificationIntentType(id));
        intent.setAction(ACTION_NOTIFICATION);
        cancelAmarmManager(con, id, intent);
        deleteNotificationAlarmFromPrefs(con, id);
    }

    static void cancelAmarmManager(Context con, int id, Intent intent) {

        PendingIntent sender = PendingIntent.getService(con, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        sender.cancel();
    }

    // でている通知削除
    public static void clearNotification(Context con, int id){
        NotificationManager notificationManager = (NotificationManager)con.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    // 登録していたアラームを再度登録
    public static void reregister(Context con){

        Log.i("reregister", con.toString());
        setPrefs(con);

        final Iterator<Integer> alarmIdsIter = getAlarmIds(con).iterator();
        if(alarmIdsIter.hasNext()) {
            alarmTimerFunc(con, alarmIdsIter);
        }


        final Iterator<Integer> notificationIdsIter = getNotificationIds(con).iterator();
        if(notificationIdsIter.hasNext()) {
          notificationTimerFunc(con, notificationIdsIter);
        }

    }

    private static void alarmTimerFunc(final Context con, final Iterator<Integer> alarmIdsIter){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                reregisterAlarm(con, alarmIdsIter.next(), 1);
                if(alarmIdsIter.hasNext()){
                    alarmTimerFunc(con, alarmIdsIter);
                }
            }
        }, 100);
    }

    private static void reregisterAlarm(Context con, int id, int instantTime){
        String idStr = Integer.toString(id);
        long time=  dataStore.getLong("alarm-time" + id, -1);

        Calendar c = Calendar.getInstance();
        Long current = c.getTimeInMillis();

        // 過去の
        if(time < current){
            time = current + instantTime * 1000; // すぐ発火
        }

        c.setTimeInMillis(time);

        addAlarm(con, id, c);
        Log.i("reregister-alarm", idStr);
        Log.i("reregister-time", Long.toString(time - current));

    }

    private static void notificationTimerFunc(final Context con, final Iterator<Integer> notificationIdsIter){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                reregisterNotification(con, notificationIdsIter.next(), 1);
                if(notificationIdsIter.hasNext()){
                    notificationTimerFunc(con, notificationIdsIter);
                }
            }
        }, 1000);
    }

    private static String getAlarmIntentType(int id){
        return "alarm_id" + id;
    }

    private static String getNotificationIntentType(int id){
        return "notification_id" + id;
    }

    private static void reregisterNotification(Context con, int id, int instantTime){
        String title = dataStore.getString("title" + id, "");
        String ticker = dataStore.getString("ticker" + id, "");
        String text = dataStore.getString("text" + id, "");
        long time=  dataStore.getLong("notification-time" + id, -1);

        Calendar c = Calendar.getInstance();
        Long current = c.getTimeInMillis();
        // 過去の
        if(time < current){
            time = current + instantTime * 1000; // すぐ発火
        }
        c.setTimeInMillis(time);

        addNotification(con, id, title, ticker, text, c);
        Log.i("reregister-notification", Integer.toString(id));
        Log.i("reregister-time", Long.toString(time - current));
    }

    public static void deleteNotificationAlarmFromPrefs(Context con, int id) {
        deleteAlarmManagerFromPrefs(con, id, NOTIFICATION_IDS_PREFS_KEY);
    }

    public static void deleteAlarmFromPrefs(Context con, int id){
        deleteAlarmManagerFromPrefs(con, id, ALARM_IDS_PREFS_KEY);
    }

    private static void deleteAlarmManagerFromPrefs(Context con, int id, String prefsKey){

        Log.i(TAG, "-- deleteAlarmManagerFromPrefs--- " + prefsKey + id);

        setPrefs(con);
        HashSet<String> ids = (HashSet<String>) dataStore.getStringSet(prefsKey, new HashSet<String>());
        String idstr = Integer.toString(id);
        ids.remove(idstr);

        /*
         androidのバグなのか一度消去しないとxmlが更新されnない。
         キャッシュされているためか一見は正しく動くが、
         アプリを再起動したときに前のデータのままになる
        */
        editor.remove(prefsKey);
        editor.apply();

        editor.putStringSet(prefsKey, ids);
        editor.apply();
    }

    private static void setPrefs(Context con) {
        if (dataStore == null || editor == null) {
            dataStore = con.getSharedPreferences(PREFS_NAME, con.MODE_PRIVATE);
            editor = dataStore.edit();
        }
    }

    private static HashSet<Integer>  getAlarmIds(Context con){
        HashSet<Integer> alarmIds = new HashSet<>();
        HashSet<String> alarmIdsStr = (HashSet<String>) dataStore.getStringSet(ALARM_IDS_PREFS_KEY, new HashSet<String>());
        for (String idStr : alarmIdsStr) {
            alarmIds.add(Integer.parseInt(idStr));
        }
        return alarmIds;
    }

    private static HashSet<Integer>  getNotificationIds(Context con){
        HashSet<Integer> notificationIds = new HashSet<>();
        HashSet<String> notificationIdsStr = (HashSet<String>) dataStore.getStringSet(NOTIFICATION_IDS_PREFS_KEY, new HashSet<String>());
        for (String idStr : notificationIdsStr) {
            notificationIds.add(Integer.parseInt(idStr));
        }
        return notificationIds;
    }

    // push通知タップ
    public static int getClickedNotificationId(Context con){
        Intent intent = ((Activity)con).getIntent();
        int pri_id = intent.getIntExtra(MyAlarmManager.PRIMARY_ID_KEY, 0);
        intent.putExtra(MyAlarmManager.PRIMARY_ID_KEY, 0);
        return pri_id;
    }
}