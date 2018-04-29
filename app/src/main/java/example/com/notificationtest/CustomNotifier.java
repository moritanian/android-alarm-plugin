package example.com.notificationtest;

import example.com.alarmplugin.Notifier;

/**
 * Created by Moritanian on 2018/04/29.
 */

public class CustomNotifier extends Notifier {

    @Override
    protected Class<?> getAppClass(){
        return example.com.notificationtest.MainActivity.class;
    }
}
