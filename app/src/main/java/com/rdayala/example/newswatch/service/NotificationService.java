package com.rdayala.example.newswatch.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import io.realm.Realm;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class NotificationService extends IntentService {

    public static final String TAG = "NewsWatch";


    public NotificationService() {
        super("NotificationService");
        Log.d(TAG, "NotificationService : ");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent : ");
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();


        }
        finally {
             if(realm != null) {
                 realm.close();
             }
        }

    }

}
