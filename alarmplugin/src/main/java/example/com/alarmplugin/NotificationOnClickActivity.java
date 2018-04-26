package example.com.alarmplugin;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.unity3d.player.UnityPlayerNativeActivity;

/**
 * Created by Moritanian on 2018/04/26.
 *
 * push通知をクリックした際にいったんこのactivityを経由してunityActivityをたてる
 * アプリがバックグラウンドにいる状態でpush通知クリックから復帰するとonCreateがよばれず
 * onNewIntentがよばれる。おそらくここで setIntentするとよいとおもわれるがintentを書き換えることによる
 * unityへの影響を考慮してクリックしたpush通知の情報をintentではなくplayerprefsにいれることにする
 * なお、test用のandroid projectではバックグラウンドからpush通知クリックによるintentからの復帰は
 * onCreateがよばれていた。unity側でなんらかの設定がなされているようだが詳細不明
 *
 */

public class NotificationOnClickActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = getIntent();
        int priId = intent.getIntExtra(MyAlarmManager.PRIMARY_ID_KEY, 0);
        MyAlarmManager.setClickedNotificationId(priId);

        Intent uIntent = new Intent(this, UnityPlayerNativeActivity.class);
        startActivity(uIntent);
    }

}
