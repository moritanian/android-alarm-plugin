package example.com.alarmplugin;

import android.content.Context;
import com.unity3d.player.UnityPlayer;


/**
 * Created by Moritanian on 2018/04/29.
 *
 * unity plugin class
 */

@SuppressWarnings("unused")
public class AlarmPlugin {

    private static MyAlarmManager getInstance() {
        Context con = UnityPlayer.currentActivity;
        return new MyAlarmManager(con);
    }

    public static void resetAllAlarms() {
        getInstance().resetAllAlarms();
    }

    public static void resetAllNotificatios() {
        getInstance().resetAllNotificatios();
    }

    public static void resetAll() {
        getInstance().resetAll();
    }

    public static void addAlarm(int id, int secondsFromNow) {
        getInstance().addAlarm(id, secondsFromNow);
    }

    public static void addNotification(int id, String title, String ticker, String text, int secondsFromNow) {
        getInstance().addNotification(id, title, ticker, text, secondsFromNow);
    }

    public static void resetAlarm(int id) {
        getInstance().resetAlarm(id);
    }

    public static void resetNotification(int id) {
        getInstance().resetNotification(id);
    }

    public static void clearNotification(int id) {
        getInstance().clearNotification(id);
    }

    public static void reregister() {
        getInstance().reregister();
    }

    public static int getClickedNotificationId() {
        return getInstance().getClickedNotificationId();
    }

}
