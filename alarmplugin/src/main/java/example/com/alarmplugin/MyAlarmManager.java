package example.com.alarmplugin;


import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/*
    [Prefs data structure]
    title${id} : String
    ticker${id} : String
    text${id} : text

    notification-ids-prefs-key: StringSet
    alarm-ids-prefs-key: StringSet


 */
public class MyAlarmManager {

    public static final String ACTION_ALARM = "ACTION_ALARM";
    public static final String ACTION_NOTIFICATION = "ACTION_NOTIFICATION";

    public static final  String PRIMARY_ID_KEY = "pri_key";
    public static final  String ACTION_KEY = "action";

    private static final String PREFS_NAME = "alarm-manager-prefs";
    private static final String NOTIFICATION_IDS_PREFS_KEY = "notification-ids-prefs-key";
    private static final String ALARM_IDS_PREFS_KEY = "alarm-ids-prefs-key";
    private static final String CLICKED_NOTIFICATION_ID_PREFS_KEY = "clicked-notification-id-prefs-key";
    private static final String NOTIFICATION_ICON_RESOURCEID_PREFS_KEY = "notification-icon-resourceid-prefs-key";
    private static final String ALARM_AUDIO_RESOURCEID_PREFS_KEY = "alarm-audio-resourceid-prefs-key";

    private static final String ALARM_TIME_PREFS_KEY = "alarm-time%d";
    private static final String NOTIFICATION_TIME_PREFS_KEY = "notification-time%d";

    private static final String TAG = MyAlarmManager.class.getSimpleName();

    private SharedPreferences dataStore;
    private SharedPreferences.Editor editor;

    private Context context;
    public MyAlarmManager (Context context){
        this.context = context;
        setPrefs();
    }

    public void resetAllAlarms() {
        for (int id : getAlarmIds()) {
            resetAlarm(id);
        }
    }

    public void resetAllNotificatios(){
        for (int id : getNotificationIds()) {
            resetNotification(id);
        }
    }

    public void resetAll(){
        resetAllAlarms();
        resetAllNotificatios();
    }

    public void addAlarm(int id, int secondsFromNow) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, secondsFromNow);
        addAlarm(id, c);
    }

    public void addAlarm(int id, Calendar c) {

        if( !validateTime(c)){
            c.add(Calendar.SECOND, 1);
            addAlarm(id, c);
            return;
        }

        Intent intent = new Intent(context, MyAlarmService.class);
        intent.setType(getAlarmIntentType(id));
        intent.setAction(ACTION_ALARM);
        intent.putExtra(ACTION_KEY, ACTION_ALARM);
        intent.putExtra(PRIMARY_ID_KEY, id);
        setAlarmManager(id, intent, c);

        editor.putLong(String.format(ALARM_TIME_PREFS_KEY, id), c.getTimeInMillis());
        HashSet<String> alarmIds = (HashSet<String>) dataStore.getStringSet(ALARM_IDS_PREFS_KEY, new HashSet<String>());
        alarmIds.add(Integer.toString(id));
        editor.putStringSet(ALARM_IDS_PREFS_KEY, alarmIds);
        editor.apply();
    }

    public void addNotification(int id, String title, String ticker, String text, int secondsFromNow) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, secondsFromNow);
        addNotification(id, title, ticker, text, c);
    }

    public void addNotification(int id, String title, String ticker, String text, Calendar c) {

        if( !validateTime(c)){
            c.add(Calendar.SECOND, 1);
            addNotification(id, title, ticker, text, c);
            return;
        }

        Intent intent = new Intent(context, MyAlarmService.class);
        String type =  getNotificationIntentType(id);
        intent.setType(type);
        intent.setAction(ACTION_NOTIFICATION);
        intent.putExtra(ACTION_KEY, ACTION_NOTIFICATION);
        intent.putExtra("title", title);
        intent.putExtra("ticker", ticker);
        intent.putExtra("text", text);
        intent.putExtra(PRIMARY_ID_KEY, id);
        setAlarmManager(id, intent, c);

        editor.putString("title" + id, title);
        editor.putString("ticker" + id, ticker);
        editor.putString("text" + id, text);
        editor.putLong( String.format( NOTIFICATION_TIME_PREFS_KEY, id), c.getTimeInMillis());
        HashSet<String> notificationIds = (HashSet<String>) dataStore.getStringSet(NOTIFICATION_IDS_PREFS_KEY, new HashSet<String>());
        notificationIds.add(Integer.toString(id));
        editor.putStringSet(NOTIFICATION_IDS_PREFS_KEY, notificationIds);
        editor.apply();

    }

    private void setAlarmManager(int id, Intent intent, Calendar c) {
        Log.i(TAG, "setAlarmManager " + Integer.toString(id) + " " + c.getTimeInMillis());
        PendingIntent pendingIntent = PendingIntent.getService(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    public void resetAlarm(int id) {
        // アラームのキャンセル
        Log.d(TAG, "resetAlarm");
        Intent intent = new Intent(context, MyAlarmService.class);
        intent.setType(getAlarmIntentType(id));
        cancelAmarmManager(id, intent);
        deleteAlarmFromPrefs(id);
    }

    public void resetNotification(int id) {
        // アラームのキャンセル
        Log.d(TAG, "resetNotification()");
        Intent intent = new Intent(context, MyAlarmService.class);
        intent.setType(getNotificationIntentType(id));
        intent.setAction(ACTION_NOTIFICATION);
        cancelAmarmManager(id, intent);
        deleteNotificationAlarmFromPrefs(id);
    }

    void cancelAmarmManager(int id, Intent intent) {

        PendingIntent sender = PendingIntent.getService(context ,id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        sender.cancel();
    }

    // でている通知削除
    public void clearNotification(int id){
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    // 登録していたアラームを再度登録
    public void reregister(){

        Log.i("reregister", context.toString());

        final Iterator<Integer> alarmIdsIter = getAlarmIds().iterator();
        if(alarmIdsIter.hasNext()) {
            alarmTimerFunc(alarmIdsIter);
        }


        final Iterator<Integer> notificationIdsIter = getNotificationIds().iterator();
        if(notificationIdsIter.hasNext()) {
            notificationTimerFunc(notificationIdsIter);
        }

    }

    private void alarmTimerFunc(final Iterator<Integer> alarmIdsIter){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                reregisterAlarm(alarmIdsIter.next(), 1);
                if(alarmIdsIter.hasNext()){
                    alarmTimerFunc(alarmIdsIter);
                }
            }
        }, 100);
    }


    private void reregisterAlarm(int id, int instantTime){
        String idStr = Integer.toString(id);
        long time = getRegisteredAlarmTime(id);

        Calendar c = Calendar.getInstance();
        Long current = c.getTimeInMillis();

        // 過去の
        if(time < current){
            time = current + instantTime * 1000; // すぐ発火
        }

        c.setTimeInMillis(time);

        addAlarm(id, c);
        Log.i("reregister-alarm", idStr);
        Log.i("reregister-time", Long.toString(time - current));

    }

    private void notificationTimerFunc(final Iterator<Integer> notificationIdsIter){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                reregisterNotification(notificationIdsIter.next(), 1);
                if(notificationIdsIter.hasNext()){
                    notificationTimerFunc(notificationIdsIter);
                }
            }
        }, 1000);
    }
    private void reregisterNotification(int id, int instantTime){
        String title = dataStore.getString("title" + id, "");
        String ticker = dataStore.getString("ticker" + id, "");
        String text = dataStore.getString("text" + id, "");
        long time=  getRegisteredNotificationTime(id);

        Calendar c = Calendar.getInstance();
        Long current = c.getTimeInMillis();
        // 過去の
        if(time < current){
            time = current + instantTime * 1000; // すぐ発火
        }
        c.setTimeInMillis(time);

        addNotification(id, title, ticker, text, c);
        Log.i("reregister-notification", Integer.toString(id));
        Log.i("reregister-time", Long.toString(time - current));
    }

    private String getAlarmIntentType(int id) {
        return String.format("alarm_id%d", id);
    }

    private String getNotificationIntentType(int id) {
        return String.format("notification_id%d", id);
    }

    private long getRegisteredAlarmTime(int id){
        return dataStore.getLong(String.format(ALARM_TIME_PREFS_KEY, id), -1);
    }

    private long getRegisteredNotificationTime(int id){
        return dataStore.getLong(String.format(NOTIFICATION_TIME_PREFS_KEY, id), -1);
    }

    public void deleteNotificationAlarmFromPrefs(int id) {
        deleteAlarmManagerFromPrefs(id, NOTIFICATION_IDS_PREFS_KEY);
    }

    public void deleteAlarmFromPrefs(int id){
        deleteAlarmManagerFromPrefs(id, ALARM_IDS_PREFS_KEY);
    }

    private void deleteAlarmManagerFromPrefs(int id, String prefsKey){

        Log.i(TAG, "-- deleteAlarmManagerFromPrefs--- " + prefsKey + id);

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

    private void setPrefs(){
        if (dataStore == null || editor == null) {
            dataStore = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
            editor = dataStore.edit();
        }
    }

    private HashSet<Integer>  getAlarmIds(){
        HashSet<Integer> alarmIds = new HashSet<>();
        HashSet<String> alarmIdsStr = (HashSet<String>) dataStore.getStringSet(ALARM_IDS_PREFS_KEY, new HashSet<String>());
        for (String idStr : alarmIdsStr) {
            alarmIds.add(Integer.parseInt(idStr));
        }
        return alarmIds;
    }

    private HashSet<Integer>  getNotificationIds(){
        HashSet<Integer> notificationIds = new HashSet<>();
        HashSet<String> notificationIdsStr = (HashSet<String>) dataStore.getStringSet(NOTIFICATION_IDS_PREFS_KEY, new HashSet<String>());
        for (String idStr : notificationIdsStr) {
            notificationIds.add(Integer.parseInt(idStr));
        }
        return notificationIds;
    }

    private boolean validateTime(Calendar c){

        long time = c.getTimeInMillis();

        HashSet<Integer> alarmIds = getAlarmIds();
        HashSet<Integer> notificationIds = getNotificationIds();

        for(int id : alarmIds){
            long registeredTime = getRegisteredAlarmTime(id);
            if( Math.abs( registeredTime - time) < 1000 ){
                return false;
            }
        }

        for(int id : notificationIds){
            long registeredTime = getRegisteredNotificationTime(id);
            if( Math.abs( registeredTime - time) < 1000 ){
                return false;
            }
        }

        return true;

    }

    // push通知タップ
    public int getClickedNotificationId(){
        int id=  dataStore.getInt(CLICKED_NOTIFICATION_ID_PREFS_KEY, 0);
        setClickedNotificationId(0);
        return id;
    }

    public void setClickedNotificationId(int id){
        editor.putInt(CLICKED_NOTIFICATION_ID_PREFS_KEY, id);
        editor.apply();
    }

    /**
     * configuration
     */
    public void setNotificationIconResourceId(int id){
        editor.putInt(NOTIFICATION_ICON_RESOURCEID_PREFS_KEY, id);
        editor.apply();
    }

    public int getNotificationIconResourceId(){
        int defaultId = context.getApplicationInfo().icon;
        return dataStore.getInt(NOTIFICATION_ICON_RESOURCEID_PREFS_KEY, defaultId);
    }

    public void setAudioResourceId(int id){
        editor.putInt(ALARM_AUDIO_RESOURCEID_PREFS_KEY, id);
        editor.apply();
    }

    public int getAudioResourceId(){
        return dataStore.getInt(ALARM_AUDIO_RESOURCEID_PREFS_KEY, 0);
    }


}
