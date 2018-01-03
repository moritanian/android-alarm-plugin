package example.com.notificationtest;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by morita on 2017/12/30.
 */

public class FIleDirPicker extends Fragment {

    private final int REQUEST_PICK_PICTURE = 1;
    private final int REQUEST_PIC_TEXT = 2;

    public static final String INTENT_MIME_TYPE_KEY = "mimeType";

    private static String TAG = FIleDirPicker.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        String mimeType = bundle.getString(INTENT_MIME_TYPE_KEY);
        //String mimeType = intent.getStringExtra(INTENT_MIME_TYPE_KEY);

        // set request
        int request = REQUEST_PIC_TEXT;
        if (mimeType.indexOf("image") >= 0) {
            request = REQUEST_PICK_PICTURE;
        }

        try {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType(mimeType);
            startActivityForResult(i, request);

        } catch (ActivityNotFoundException e) {
            Toast toast = Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            Log.i(TAG, "requestCode error");
            return;
        }

        Log.i(TAG, "requestCode :" + requestCode);

        switch (requestCode) {
            case (REQUEST_PICK_PICTURE):
                Uri uri = data.getData();
                String uriStr = uri.toString();

                Log.i(TAG, "uri str = " + uriStr);


                /** Uriを処理... */
                break;

            case REQUEST_PIC_TEXT:
                String text = data.getStringExtra(Intent.EXTRA_TEXT);

                Log.i(TAG, "result text = " + text);
                break;
        }

    }


}
