package example.com.notificationtest;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by morita on 2017/12/30.
 */

public class FileDirManager {

    public interface FileSelecterCallback {
        public void success(Object obj);

        public void failed(Object obj);
    }


    private static final int REQUEST_PERMISSION = 1000;

    private static final String TAG = FileDirManager.class.toString();

    public static String getDownloadPath(String filename) {
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getPath()
                + "/" + filename;
    }


    public static void saveFile(String filepath, String content) {
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

    }

    public static String readFileAsText(String filepath) {
        try {
            FileInputStream fileInputStream;
            fileInputStream = new FileInputStream(filepath);
            byte[] readBytes = new byte[fileInputStream.available()];
            fileInputStream.read(readBytes);
            String readString = new String(readBytes);
            fileInputStream.close();
            return readString;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        return "";
    }

    public static boolean existFile(String filepath) {
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

    public String readFile(String filepath) {
        String content = "";
        File file = new File(filepath);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String body;
            while ((body = br.readLine()) != null) {
                content += body;
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
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


}
