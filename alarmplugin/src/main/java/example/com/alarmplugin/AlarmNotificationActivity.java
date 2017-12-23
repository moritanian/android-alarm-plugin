package example.com.alarmplugin;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Created by Moritanian on 2017/12/20.
 */

public class AlarmNotificationActivity extends Activity {
    private MediaPlayer mp;
    private  Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // スクリーンロックを解除する
        // 権限が必要
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    public void onStart() {
        super.onStart();
        // 音を鳴らす
        if (mp == null)
            // resのrawディレクトリにtest.mp3を置いてある
            mp = MediaPlayer.create(this, R.raw.test);
        mp.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAndRelaese();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        this.intent = intent;
    }

    private void stopAndRelaese() {
        if (mp != null) {
            mp.stop();
            mp.release();
        }
    }
}