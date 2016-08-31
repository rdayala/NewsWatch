package com.rdayala.example.newswatch.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.rdayala.example.newswatch.model.FavoriteNewsItem;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class DeleteOldDataService extends IntentService {

    public static final String TAG = "DeleteOldDataService";

    public DeleteOldDataService() {
        super("DeleteOldDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            int offlineCacheDays = Integer.parseInt(sharedPreferences.getString("offlineCacheDays", "7"));
            offlineCacheDays = 1;

            Date dateToCompareWith = new Date(System.currentTimeMillis()-(offlineCacheDays*24*60*60*1000));

            final RealmResults<FavoriteNewsItem> results =
                    realm.where(FavoriteNewsItem.class).lessThan("mDateAdded", dateToCompareWith).findAll();

            if(results.size() > 0) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // Delete all matches
                        results.deleteAllFromRealm();
                    }
                });
            }
        }
        catch(RealmException ex) {

        }
        finally {
            if(realm != null) {
                realm.close();
            }
        }
    }
}
