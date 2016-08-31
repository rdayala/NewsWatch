package com.rdayala.example.newswatch.service;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.rdayala.example.newswatch.MainActivity;
import com.rdayala.example.newswatch.R;
import com.rdayala.example.newswatch.model.FavoriteNewsItem;
import com.rdayala.example.newswatch.model.Feed;
import com.rdayala.example.newswatch.model.FeedItem;
import com.rdayala.example.newswatch.utils.Feed2DBConversion;

import br.com.goncalves.pugnotification.notification.PugNotification;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class NotificationService extends IntentService {

    public static final String TAG = "NotificationService";
    private Context mContext;


    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean autoRefresh = sharedPreferences.getBoolean("pref_automatic_refresh", true);

        if (autoRefresh) {

            mContext = this;

            // Create a simple REST adapter which points to GitHub’s API
            FeedDataService service = ServiceGenerator.createService(FeedDataService.class);

            Call<Feed> call = service.getItems("thehindu/FFST"); // dnaindia/Jajk

            call.enqueue(new Callback<Feed>() {
                @Override
                public void onResponse(Call<Feed> call, Response<Feed> response) {

                    if (response.isSuccessful()) {
                        Feed respFeed = response.body();

                        FeedItem item = respFeed.getmChannel().getFeedItems().get(0);

                        FavoriteNewsItem favoriteNewsItem = Feed2DBConversion.convertFeedToDBModelObject(item, "National");

                        Realm realm = null;
                        try {
                            realm = Realm.getDefaultInstance();
                            RealmResults<FavoriteNewsItem> results =
                                    realm.where(FavoriteNewsItem.class).equalTo("mCategory", "National").findAll();
                            results = results.sort("mDateAdded", Sort.DESCENDING);
                            if (results.size() > 0) {
                                FavoriteNewsItem firstItem = results.first();
                                if (favoriteNewsItem.getmDateAdded().after(firstItem.getmDateAdded())) {
                                    PugNotification.with(mContext)
                                            .load()
                                            .title("News Diary")
                                            .message(favoriteNewsItem.getMtitle())
                                            .bigTextStyle(favoriteNewsItem.getMdescription())
                                            .smallIcon(R.drawable.ic_pib_articles)
                                            .flags(Notification.DEFAULT_ALL)
                                            .autoCancel(true)
                                            .color(R.color.notificationBackground)
                                            .click(MainActivity.class)
                                            .simple()
                                            .build();
                                }
                            }
                        }
                        catch(RealmException ex) {

                        }
                        finally {
                            if(realm != null) {
                                realm.close();
                            }
                        }

                    } else {
                        Log.e(TAG, "Request failed - Cannot request GitHub repositories");
                    }
                }

                @Override
                public void onFailure(Call<Feed> call, Throwable t) {
                    Log.e(TAG, "Error fetching repos : " + t.getMessage());
                }
            });
        }
    }
}