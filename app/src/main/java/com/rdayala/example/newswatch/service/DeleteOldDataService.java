package com.rdayala.example.newswatch.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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
            int offlineCacheDays = Integer.parseInt(sharedPreferences.getString("offlineCacheDays", "2"));

            Date dateToCompareWith = new Date(System.currentTimeMillis()-(offlineCacheDays*24*60*60*1000));

            Log.d(TAG, "Date to compare with : " + dateToCompareWith.toString());

            RealmResults<FavoriteNewsItem> results =
                    realm.where(FavoriteNewsItem.class).lessThan("mDateAdded", dateToCompareWith)
                            .findAll().where().notEqualTo("isAddedFavorite", true).findAll();

            final RealmResults<FavoriteNewsItem> nationalResults =
                    results.where().equalTo("mCategory", "National").findAll();

            if(results.size() > 0) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // Delete all matches
                        nationalResults.deleteAllFromRealm();
                    }
                });
            }

            final RealmResults<FavoriteNewsItem> businessResults =
                    results.where().equalTo("mCategory", "Economy").findAll();

            if(results.size() > 0) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // Delete all matches
                        businessResults.deleteAllFromRealm();
                    }
                });
            }

            final RealmResults<FavoriteNewsItem> worldResults =
                    results.where().equalTo("mCategory", "World").findAll();

            if(results.size() > 0) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // Delete all matches
                        worldResults.deleteAllFromRealm();
                    }
                });
            }

            final RealmResults<FavoriteNewsItem> sportsResults =
                    results.where().equalTo("mCategory", "Sports").findAll();

            if(results.size() > 0) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // Delete all matches
                        sportsResults.deleteAllFromRealm();
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
