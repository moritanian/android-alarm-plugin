package example.com.notificationtest;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Moritanian on 2018/01/06.
 */

public class TextViewer extends Activity {
    Intent intent;

    String TAG = TextViewer.class.toString();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_notification);

        intent = getIntent();
        Uri uri  = intent.getData();
        Log.i(TAG, uri.getPath());
        String text = FileDirManager.readFileAsText(uri.getPath());
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        this.intent = intent;
    }


}
