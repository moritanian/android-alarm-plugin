package example.com.fileplugin;


import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by morita on 2017/12/30.
 */

public class FileDirManager {

    private static final int REQUEST_PERMISSION = 1000;

    private static final String TAG = FileDirManager.class.toString();

    public static String getDownloadPath(String filename) {
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getPath()
                + "/" + filename;
    }

    public static void writeFileWithNotification(String filepath, String content) {
        Activity unityActivity = UnityPlayer.currentActivity;
        writeFileWithNotification(unityActivity, filepath, content);
    }

    public static void writeFileWithNotification(Context con, String filepath, String content){
        File file = _writeFile(filepath, content);
        sendDownloadNotification(con, file);
    }

    public static void writeFile(String filepath, String content) {
        _writeFile(filepath, content);
    }

    private static File _writeFile(String filepath, String content){
        File file = new File(filepath);

        try {
            file.createNewFile();
            if (file.canWrite()) {
                FileWriter fw = new FileWriter(file);

                fw.append(content);
                fw.close();
            }
        } catch (IOException e) {
            // e.printStackTrace();
        }
        return file;
    }

    public static String readFileAsText(String filepath) {
        String content = "";
        File file = new File(filepath);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String body;
            while ((body = br.readLine()) != null) {
                content += body;
                content += "\n";
            }
            br.close();
            return content;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static boolean existsFile(String filepath) {
        File file = new File(filepath);
        return file.exists();
    }

    public static void showFileSelecter(Context con, String mimeType) {

        // データを渡す為のBundleを生成し、渡すデータを内包させる
        Bundle bundle = new Bundle();
        bundle.putString(FIleDirPicker.INTENT_MIME_TYPE_KEY, mimeType);

        FIleDirPicker picker = new FIleDirPicker();
        picker.setArguments(bundle);

        FragmentTransaction transaction = ((Activity) con).getFragmentManager().beginTransaction();

        transaction.add(picker, TAG);
        transaction.commit();
    }

    // permissionの確認
    public static void checkPermission(Context con) {
        // 既に許可している
        if (ContextCompat.checkSelfPermission(con,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            setUpReadWriteExternalStorage();
        }
        // 拒否していた場合
        else {
            requestLocationPermission(con);
        }
    }

    private static void setUpReadWriteExternalStorage() {
    }

    // 許可を求める
    private static void requestLocationPermission(Context con) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) con,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions((Activity) con,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);

        } else {
            Toast toast =
                    Toast.makeText(con, "アプリ実行に許可が必要です", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions((Activity) con,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    REQUEST_PERMISSION);

        }
    }

    // 結果の受け取り
    /*
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpReadWriteExternalStorage();
            } else {
                // それでも拒否された時の対応
                Toast toast =
                        Toast.makeText(this, "何もできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
    */

    /***
     * Send local notification that notify download file.
     *
     * @param con
     * @param downloadFile downloaded file
     * @return void
     */
    private static void sendDownloadNotification(Context con, File downloadFile) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) con.getSystemService(ns);

        Resources res = con.getResources();
        int icon = res.getIdentifier("app_icon", "drawable", con.getPackageName());

        String tickerText = "Downloaded";

        //the bold font
        String contentTitle = downloadFile.getName();
        //the text that needs to change
        String contentText = "Downloaded";
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW);

        // set mimetype
        notificationIntent.setDataAndType (Uri.parse(downloadFile.getAbsolutePath()) ,getMimeType(downloadFile.getName()));
        //notificationIntent.setData(Uri.parse(downloadFile.getPath()));
        //notificationIntent.setType(downloadFile.getName());
        PendingIntent contentIntent = PendingIntent.getActivity(con, 0, notificationIntent, 0);

        //通知オブジェクトの生成
        Notification noti = new NotificationCompat.Builder(con)
                .setTicker(tickerText)
                .setContentTitle(contentTitle )
                .setContentText( contentText)
                .setSmallIcon(icon)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .build();

        int notificationId = downloadFile.getAbsolutePath().hashCode();
        notificationManager.notify(notificationId, noti);
    }

    private static String getMimeType(String filenNme) {
        int ch = filenNme.lastIndexOf('.');
        String ext = (ch >= 0) ? filenNme.substring(ch + 1) : null;

        String MIME = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase());

        if (MIME == null || MIME.equals("")) {
            MIME = "*/*";
        }

        return MIME;
    }


}
