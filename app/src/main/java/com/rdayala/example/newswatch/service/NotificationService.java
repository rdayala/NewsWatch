package com.rdayala.example.newswatch.service;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.rdayala.example.newswatch.MainActivity;
import com.rdayala.example.newswatch.R;
import com.rdayala.example.newswatch.WebViewActivity;
import com.rdayala.example.newswatch.model.FavoriteNewsItem;
import com.rdayala.example.newswatch.model.Feed;
import com.rdayala.example.newswatch.model.FeedItem;
import com.rdayala.example.newswatch.utils.Feed2DBConversion;

import java.util.ArrayList;
import java.util.Random;

import br.com.goncalves.pugnotification.notification.PugNotification;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmException;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
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
    private boolean nationalNotification = false;
    private boolean pibNotification = false;
    private boolean worldNotification = false;
    private boolean economyNotification = false;


    public NotificationService() {
        super("NotificationService");
    }

    public int generateRandom(){
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        nationalNotification = false;
        pibNotification = false;
        worldNotification = false;
        economyNotification = false;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean autoRefresh = sharedPreferences.getBoolean("pref_automatic_refresh", true);
        final boolean sendNotifications = sharedPreferences.getBoolean("pref_send_notification", true);

        if (autoRefresh) {

            mContext = this;

            // NationalNotification Block

            if(!nationalNotification){
                // Create a simple REST adapter which points to GitHub’s API
                FeedDataService service = ServiceGenerator.createService(FeedDataService.class);

                Call<Feed> call = service.getItems("thehindu/FFST"); // National News dnaindia/Jajk

                call.enqueue(new Callback<Feed>() {
                    @Override
                    public void onResponse(Call<Feed> call, Response<Feed> response) {

                        if (response.isSuccessful()) {

                            FavoriteNewsItem latestDBItem = null;

                            Feed respFeed = response.body();

                            Realm realm = null;
                            try {
                                realm = Realm.getDefaultInstance();
                                RealmResults<FavoriteNewsItem> results =
                                        realm.where(FavoriteNewsItem.class).equalTo("mCategory", "National").findAll();
                                results = results.sort("mDateAdded", Sort.DESCENDING);
                                if (results.size() > 0) {
                                    latestDBItem = new FavoriteNewsItem();
                                    latestDBItem.setMtitle(results.first().getMtitle());
                                    latestDBItem.setMpubDate(results.first().getMpubDate());
                                    latestDBItem.setmDateAdded(results.first().getmDateAdded());
                                }
                                realm.close();

                                if (latestDBItem != null) {

                                    ArrayList<String> inboxStyleLinesList = new ArrayList<>();
                                    int newFeedsCounter = 0;

                                    for (FeedItem item : respFeed.getmChannel().getFeedItems()) {

                                        FavoriteNewsItem favoriteNewsItem = Feed2DBConversion.convertFeedToDBModelObject(item, "National");

                                        if (favoriteNewsItem.getmDateAdded().after(latestDBItem.getmDateAdded())) {

                                            Log.d(TAG, "National (old) : " + latestDBItem.getMpubDate() + ",  " + latestDBItem.getMtitle());
                                            Log.d(TAG, "National (new) : " + favoriteNewsItem.getMpubDate() + ",  " + favoriteNewsItem.getMtitle());

                                            realm = Realm.getDefaultInstance();
                                            try {
                                                realm.beginTransaction();
                                                FavoriteNewsItem dbObject = realm.copyToRealm(favoriteNewsItem);
                                                if(dbObject != null) {
                                                    Log.d(TAG, "dbObjected : " + dbObject.getMtitle());
                                                }
                                                realm.commitTransaction();
                                                if(sendNotifications) {

                                                    inboxStyleLinesList.add(favoriteNewsItem.getMtitle());
                                                    newFeedsCounter++;

//                                                    Bundle bundle = new Bundle();
//                                                    bundle.putString("title", favoriteNewsItem.getMtitle());
//                                                    bundle.putString("url", favoriteNewsItem.getMlink());
//                                                    bundle.putString("defaultTag", "National");
//                                                    bundle.putParcelable("feedItem", favoriteNewsItem);
//
//                                                    int not_nu = generateRandom();
//
//                                                    PugNotification.with(mContext)
//                                                            .load()
//                                                            .identifier(not_nu)
//                                                            .title("News Diary - " + favoriteNewsItem.getmCategory() + " News")
//                                                            .message(favoriteNewsItem.getMtitle())
//                                                            .smallIcon(R.drawable.ic_pib_articles)
//                                                            .flags(Notification.DEFAULT_ALL)
//                                                            .autoCancel(true)
//                                                            .color(R.color.colorPrimary)
//                                                            .click(WebViewActivity.class, bundle)
//                                                            .simple()
//                                                            .build();
                                                }
                                            } catch (RealmPrimaryKeyConstraintException ex) {

                                                realm.cancelTransaction();
                                                // get an existing object and update it with current details
                                                FavoriteNewsItem dbItem =
                                                        realm.where(FavoriteNewsItem.class).equalTo("mlink", favoriteNewsItem.getMlink()).findFirst();
                                                favoriteNewsItem.setmTags(dbItem.getmTags());
                                                favoriteNewsItem.setAddedFavorite(dbItem.isAddedFavorite());

                                                realm.beginTransaction();
                                                FavoriteNewsItem dbObject = realm.copyToRealmOrUpdate(favoriteNewsItem);
                                                if(dbObject != null) {
                                                    Log.d(TAG, "dbObjected : " + dbObject.getMtitle());
                                                }
                                                realm.commitTransaction();

                                            }
                                            finally {
                                                if(realm != null) {
                                                    realm.close();
                                                }
                                            }
                                        }
                                    }

                                    if(sendNotifications && newFeedsCounter > 0) {
                                        int not_nu = generateRandom();

                                        String summaryStr;

                                        if(newFeedsCounter == 1) {
                                            summaryStr = "You have " + newFeedsCounter + " story to follow. Touch here to open App!!";
                                        } else {
                                            summaryStr = "You have " + newFeedsCounter + " stories to follow. Touch here to open App!!";
                                        }

                                        Bundle bundle = new Bundle();
                                        bundle.putInt("tabPositionToOpen", 1);

                                        PugNotification.with(mContext)
                                                .load()
                                                .identifier(not_nu)
                                                .title("News Diary - National News")
                                                .message(summaryStr)
                                                .inboxStyle(inboxStyleLinesList.toArray(new String[0]), "News Diary - National News", summaryStr)
                                                .smallIcon(R.drawable.ic_pib_articles)
                                                .flags(Notification.DEFAULT_ALL)
                                                .autoCancel(true)
                                                .color(R.color.colorPrimary)
                                                .click(MainActivity.class, bundle)
                                                .simple()
                                                .build();
                                    }

                                } else {

                                    // there are no items in database
                                    // add all feeds to DB
                                    for (FeedItem item : respFeed.getmChannel().getFeedItems()) {
                                        FavoriteNewsItem favoriteNewsItem = Feed2DBConversion.convertFeedToDBModelObject(item, "National");
                                        realm = Realm.getDefaultInstance();
                                        try {
                                            realm.beginTransaction();
                                            FavoriteNewsItem dbObject = realm.copyToRealm(favoriteNewsItem);
                                            if(dbObject != null) {
                                                Log.d(TAG, "dbObjected : " + dbObject.getMtitle());
                                            }
                                            realm.commitTransaction();
                                        } catch (RealmPrimaryKeyConstraintException ex) {

                                            realm.cancelTransaction();
                                            // get an existing object and update it with current details
                                            FavoriteNewsItem dbItem =
                                                    realm.where(FavoriteNewsItem.class).equalTo("mlink", favoriteNewsItem.getMlink()).findFirst();
                                            favoriteNewsItem.setmTags(dbItem.getmTags());
                                            favoriteNewsItem.setAddedFavorite(dbItem.isAddedFavorite());

                                            realm.beginTransaction();
                                            FavoriteNewsItem dbObject = realm.copyToRealmOrUpdate(favoriteNewsItem);
                                            if(dbObject != null) {
                                                Log.d(TAG, "dbObjected : " + dbObject.getMtitle());
                                            }
                                            realm.commitTransaction();

                                        }
                                        finally {
                                            if(realm != null) {
                                                realm.close();
                                            }
                                        }
                                    }
                                }

                            } catch (RealmException ex) {

                            } finally {
                                if (realm != null) {
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

                nationalNotification = false;
            }

            if (!pibNotification) {
                // check for PIB News
                // Create a simple REST adapter which points to GitHub’s API
                FeedDataService service = ServiceGenerator.createService(FeedDataService.class);

                Call<Feed> call = service.getItems("nic/blAY"); // PIB News

                call.enqueue(new Callback<Feed>() {
                    @Override
                    public void onResponse(Call<Feed> call, Response<Feed> response) {

                        if (response.isSuccessful()) {

                            FavoriteNewsItem latestDBItem = null;

                            Feed respFeed = response.body();

                            Realm realm = null;
                            try {
                                realm = Realm.getDefaultInstance();
                                RealmResults<FavoriteNewsItem> results =
                                        realm.where(FavoriteNewsItem.class).equalTo("mCategory", "PIBNews").findAll();
                                results = results.sort("mDateAdded", Sort.DESCENDING);
                                if (results.size() > 0) {
                                    latestDBItem = new FavoriteNewsItem();
                                    latestDBItem.setMtitle(results.first().getMtitle());
                                    latestDBItem.setMpubDate(results.first().getMpubDate());
                                    latestDBItem.setmDateAdded(results.first().getmDateAdded());
                                }
                                realm.close();

                                if (latestDBItem != null) {
                                    for (FeedItem item : respFeed.getmChannel().getFeedItems()) {

                                        FavoriteNewsItem favoriteNewsItem = Feed2DBConversion.convertFeedToDBModelObject(item, "PIBNews");

                                        if (favoriteNewsItem.getmDateAdded().after(latestDBItem.getmDateAdded())) {

                                            Log.d(TAG, "PIB (old) : " + latestDBItem.getMpubDate() + ",  " + latestDBItem.getMtitle());
                                            Log.d(TAG, "PIB (new) : " + favoriteNewsItem.getMpubDate() + ",  " + favoriteNewsItem.getMtitle());

                                            realm = Realm.getDefaultInstance();
                                            try {
                                                realm.beginTransaction();
                                                FavoriteNewsItem dbObject = realm.copyToRealm(favoriteNewsItem);
                                                if(dbObject != null) {
                                                    Log.d(TAG, "dbObjected : " + dbObject.getMtitle());
                                                }
                                                realm.commitTransaction();
                                                if(sendNotifications) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("title", favoriteNewsItem.getMtitle());
                                                    bundle.putString("url", favoriteNewsItem.getMlink());
                                                    bundle.putString("defaultTag", "PIBNews");
                                                    bundle.putParcelable("feedItem", favoriteNewsItem);

                                                    int not_nu = generateRandom();

                                                    PugNotification.with(mContext)
                                                            .load()
                                                            .identifier(not_nu)
                                                            .title("News Diary - " + favoriteNewsItem.getmCategory() + " News")
                                                            .message(favoriteNewsItem.getMtitle())
                                                            .smallIcon(R.drawable.ic_pib_articles)
                                                            .flags(Notification.DEFAULT_ALL)
                                                            .autoCancel(true)
                                                            .color(R.color.colorPrimary)
                                                            .click(WebViewActivity.class, bundle)
                                                            .simple()
                                                            .build();

                                                    pibNotification = true;
                                                }
                                            } catch (RealmPrimaryKeyConstraintException ex) {

                                                realm.cancelTransaction();
                                                // get an existing object and update it with current details
                                                FavoriteNewsItem dbItem =
                                                        realm.where(FavoriteNewsItem.class).equalTo("mlink", favoriteNewsItem.getMlink()).findFirst();
                                                favoriteNewsItem.setmTags(dbItem.getmTags());
                                                favoriteNewsItem.setAddedFavorite(dbItem.isAddedFavorite());

                                                realm.beginTransaction();
                                                FavoriteNewsItem dbObject = realm.copyToRealmOrUpdate(favoriteNewsItem);
                                                if(dbObject != null) {
                                                    Log.d(TAG, "dbObjected : " + dbObject.getMtitle());
                                                }
                                                realm.commitTransaction();

                                            }
                                            finally {
                                                if(realm != null) {
                                                    realm.close();
                                                }
                                            }
                                        }
                                    }
                                } else {

                                    // there are no items in database
                                    // add all feeds to DB
                                    for (FeedItem item : respFeed.getmChannel().getFeedItems()) {
                                        FavoriteNewsItem favoriteNewsItem = Feed2DBConversion.convertFeedToDBModelObject(item, "PIBNews");
                                        realm = Realm.getDefaultInstance();
                                        try {
                                            realm.beginTransaction();
                                            FavoriteNewsItem dbObject = realm.copyToRealm(favoriteNewsItem);
                                            if(dbObject != null) {
                                                Log.d(TAG, "dbObjected : " + dbObject.getMtitle());
                                            }
                                            realm.commitTransaction();
                                        } catch (RealmPrimaryKeyConstraintException ex) {

                                            realm.cancelTransaction();
                                            // get an existing object and update it with current details
                                            FavoriteNewsItem dbItem =
                                                    realm.where(FavoriteNewsItem.class).equalTo("mlink", favoriteNewsItem.getMlink()).findFirst();
                                            favoriteNewsItem.setmTags(dbItem.getmTags());
                                            favoriteNewsItem.setAddedFavorite(dbItem.isAddedFavorite());

                                            realm.beginTransaction();
                                            FavoriteNewsItem dbObject = realm.copyToRealmOrUpdate(favoriteNewsItem);
                                            if(dbObject != null) {
                                                Log.d(TAG, "dbObjected : " + dbObject.getMtitle());
                                            }
                                            realm.commitTransaction();

                                        }
                                        finally {
                                            if(realm != null) {
                                                realm.close();
                                            }
                                        }
                                    }
                                }

                            } catch (RealmException ex) {

                            } finally {
                                if (realm != null) {
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

                pibNotification = false;
            }

            if (!economyNotification) {

                // check for Business News
                // Create a simple REST adapter which points to GitHub’s API
                FeedDataService service = ServiceGenerator.createService(FeedDataService.class);

                Call<Feed> call = service.getItems("business-standard/odSy"); // Business News

                call.enqueue(new Callback<Feed>() {
                    @Override
                    public void onResponse(Call<Feed> call, Response<Feed> response) {

                        if (response.isSuccessful()) {

                            FavoriteNewsItem latestDBItem = null;

                            Feed respFeed = response.body();

                            Realm realm = null;
                            try {
                                realm = Realm.getDefaultInstance();
                                RealmResults<FavoriteNewsItem> results =
                                        realm.where(FavoriteNewsItem.class).equalTo("mCategory", "Economy").findAll();
                                results = results.sort("mDateAdded", Sort.DESCENDING);
                                if (results.size() > 0) {
                                    latestDBItem = new FavoriteNewsItem();
                                    latestDBItem.setMtitle(results.first().getMtitle());
                                    latestDBItem.setMpubDate(results.first().getMpubDate());
                                    latestDBItem.setmDateAdded(results.first().getmDateAdded());
                                }
                                realm.close();

                                if (latestDBItem != null) {

                                    ArrayList<String> inboxStyleLinesList = new ArrayList<>();
                                    int newFeedsCounter = 0;

                                    for (FeedItem item : respFeed.getmChannel().getFeedItems()) {

                                        FavoriteNewsItem favoriteNewsItem = Feed2DBConversion.convertFeedToDBModelObject(item, "Economy");

                                        if (favoriteNewsItem.getmDateAdded().after(latestDBItem.getmDateAdded())) {

                                            Log.d(TAG, "Economy (old) : " + latestDBItem.getMpubDate() + ",  " + latestDBItem.getMtitle());
                                            Log.d(TAG, "Economy (new) : " + favoriteNewsItem.getMpubDate() + ",  " + favoriteNewsItem.getMtitle());

                                            realm = Realm.getDefaultInstance();
                                            try {
                                                realm.beginTransaction();
                                                FavoriteNewsItem dbObject = realm.copyToRealm(favoriteNewsItem);
                                                if(dbObject != null) {
                                                    Log.d(TAG, "dbObjected : " + dbObject.getMtitle());
                                                }
                                                realm.commitTransaction();
                                                if(sendNotifications) {

                                                    inboxStyleLinesList.add(favoriteNewsItem.getMtitle());
                                                    newFeedsCounter++;

//                                                    Bundle bundle = new Bundle();
//                                                    bundle.putString("title", favoriteNewsItem.getMtitle());
//                                                    bundle.putString("url", favoriteNewsItem.getMlink());
//                                                    bundle.putString("defaultTag", "Economy");
//                                                    bundle.putParcelable("feedItem", favoriteNewsItem);
//
//                                                    int not_nu = generateRandom();
//
//                                                    PugNotification.with(mContext)
//                                                            .load()
//                                                            .identifier(not_nu)
//                                                            .title("News Diary - Business News")
//                                                            .message(favoriteNewsItem.getMtitle())
//                                                            .smallIcon(R.drawable.ic_pib_articles)
//                                                            .flags(Notification.DEFAULT_ALL)
//                                                            .autoCancel(true)
//                                                            .color(R.color.colorPrimary)
//                                                            .click(WebViewActivity.class, bundle)
//                                                            .simple()
//                                                            .build();
//
//                                                    economyNotification = true;
                                                }
                                            } catch (RealmPrimaryKeyConstraintException ex) {

                                                realm.cancelTransaction();
                                                // get an existing object and update it with current details
                                                FavoriteNewsItem dbItem =
                                                        realm.where(FavoriteNewsItem.class).equalTo("mlink", favoriteNewsItem.getMlink()).findFirst();
                                                favoriteNewsItem.setmTags(dbItem.getmTags());
                                                favoriteNewsItem.setAddedFavorite(dbItem.isAddedFavorite());

                                                realm.beginTransaction();
                                                FavoriteNewsItem dbObject = realm.copyToRealmOrUpdate(favoriteNewsItem);
                                                if(dbObject != null) {
                                                    Log.d(TAG, "dbObjected : " + dbObject.getMtitle());
                                                }
                                                realm.commitTransaction();

                                            }
                                            finally {
                                                if(realm != null) {
                                                    realm.close();
                                                }
                                            }
                                        }
                                    }

                                    if(sendNotifications && newFeedsCounter > 0) {
                                        int not_nu = generateRandom();

                                        String summaryStr;

                                        if(newFeedsCounter == 1) {
                                            summaryStr = "You have " + newFeedsCounter + " story to follow. Touch to open App!!";
                                        } else {
                                            summaryStr = "You have " + newFeedsCounter + " stories to follow. Touch to open App!!";
                                        }

                                        Bundle bundle = new Bundle();
                                        bundle.putInt("tabPositionToOpen", 3);

                                        PugNotification.with(mContext)
                                                .load()
                                                .identifier(not_nu)
                                                .title("News Diary - Business News")
                                                .message(summaryStr)
                                                .inboxStyle(inboxStyleLinesList.toArray(new String[0]), "News Diary - Business News", summaryStr)
                                                .smallIcon(R.drawable.ic_pib_articles)
                                                .flags(Notification.DEFAULT_ALL)
                                                .autoCancel(true)
                                                .color(R.color.colorPrimary)
                                                .click(MainActivity.class)
                                                .simple()
                                                .build();
                                    }

                                } else {

                                    // there are no items in database
                                    // add all feeds to DB
                                    for (FeedItem item : respFeed.getmChannel().getFeedItems()) {
                                        FavoriteNewsItem favoriteNewsItem = Feed2DBConversion.convertFeedToDBModelObject(item, "Economy");
                                        realm = Realm.getDefaultInstance();
                                        try {
                                            realm.beginTransaction();
                                            FavoriteNewsItem dbObject = realm.copyToRealm(favoriteNewsItem);
                                            if(dbObject != null) {
                                                Log.d(TAG, "dbObjected : " + dbObject.getMtitle());
                                            }
                                            realm.commitTransaction();
                                        } catch (RealmPrimaryKeyConstraintException ex) {

                                            realm.cancelTransaction();
                                            // get an existing object and update it with current details
                                            FavoriteNewsItem dbItem =
                                                    realm.where(FavoriteNewsItem.class).equalTo("mlink", favoriteNewsItem.getMlink()).findFirst();
                                            favoriteNewsItem.setmTags(dbItem.getmTags());
                                            favoriteNewsItem.setAddedFavorite(dbItem.isAddedFavorite());

                                            realm.beginTransaction();
                                            FavoriteNewsItem dbObject = realm.copyToRealmOrUpdate(favoriteNewsItem);
                                            if(dbObject != null) {
                                                Log.d(TAG, "dbObjected : " + dbObject.getMtitle());
                                            }
                                            realm.commitTransaction();

                                        }
                                        finally {
                                            if(realm != null) {
                                                realm.close();
                                            }
                                        }
                                    }
                                }

                            } catch (RealmException ex) {

                            } finally {
                                if (realm != null) {
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

                economyNotification = false;
            }

            if(!worldNotification){

                // check for world news
                FeedDataService service = ServiceGenerator.createService(FeedDataService.class);

                Call<Feed> call = service.getItems("thehindu/NEFY"); // World News

                call.enqueue(new Callback<Feed>() {
                    @Override
                    public void onResponse(Call<Feed> call, Response<Feed> response) {

                        if (response.isSuccessful()) {

                            FavoriteNewsItem latestDBItem = null;

                            Feed respFeed = response.body();

                            Realm realm = null;
                            try {
                                realm = Realm.getDefaultInstance();
                                RealmResults<FavoriteNewsItem> results =
                                        realm.where(FavoriteNewsItem.class).equalTo("mCategory", "World").findAll();
                                results = results.sort("mDateAdded", Sort.DESCENDING);
                                if (results.size() > 0) {
                                    latestDBItem = new FavoriteNewsItem();
                                    latestDBItem.setMtitle(results.first().getMtitle());
                                    latestDBItem.setMpubDate(results.first().getMpubDate());
                                    latestDBItem.setmDateAdded(results.first().getmDateAdded());
                                }
                                realm.close();

                                if (latestDBItem != null) {

                                    ArrayList<String> inboxStyleLinesList = new ArrayList<>();
                                    int newFeedsCounter = 0;

                                    for (FeedItem item : respFeed.getmChannel().getFeedItems()) {

                                        FavoriteNewsItem favoriteNewsItem = Feed2DBConversion.convertFeedToDBModelObject(item, "World");
                                        if (favoriteNewsItem.getmDateAdded().after(latestDBItem.getmDateAdded())) {

                                            Log.d(TAG, "World (old) : " + latestDBItem.getMpubDate() + ",  " + latestDBItem.getMtitle());
                                            Log.d(TAG, "World (new) : " + favoriteNewsItem.getMpubDate() + ",  " + favoriteNewsItem.getMtitle());

                                            realm = Realm.getDefaultInstance();
                                            try {
                                                realm.beginTransaction();
                                                FavoriteNewsItem dbObject = realm.copyToRealm(favoriteNewsItem);
                                                if(dbObject != null) {
                                                    Log.d(TAG, "dbObjected : " + dbObject.getMtitle());
                                                }
                                                realm.commitTransaction();
                                                if(sendNotifications) {

                                                    inboxStyleLinesList.add(favoriteNewsItem.getMtitle());
                                                    newFeedsCounter++;

//                                                    Bundle bundle = new Bundle();
//                                                    bundle.putString("title", favoriteNewsItem.getMtitle());
//                                                    bundle.putString("url", favoriteNewsItem.getMlink());
//                                                    bundle.putString("defaultTag", "World");
//                                                    bundle.putParcelable("feedItem", favoriteNewsItem);
//
//                                                    int not_nu = generateRandom();
//
//                                                    PugNotification.with(mContext)
//                                                            .load()
//                                                            .identifier(not_nu)
//                                                            .title("News Diary - " + favoriteNewsItem.getmCategory() + " News")
//                                                            .message(favoriteNewsItem.getMtitle())
//                                                            .smallIcon(R.drawable.ic_pib_articles)
//                                                            .flags(Notification.DEFAULT_ALL)
//                                                            .autoCancel(true)
//                                                            .color(R.color.colorPrimary)
//                                                            .click(WebViewActivity.class, bundle)
//                                                            .simple()
//                                                            .build();
//
//                                                    worldNotification = true;
                                                }
                                            } catch (RealmPrimaryKeyConstraintException ex) {

                                                realm.cancelTransaction();
                                                // get an existing object and update it with current details
                                                FavoriteNewsItem dbItem =
                                                        realm.where(FavoriteNewsItem.class).equalTo("mlink", favoriteNewsItem.getMlink()).findFirst();
                                                favoriteNewsItem.setmTags(dbItem.getmTags());
                                                favoriteNewsItem.setAddedFavorite(dbItem.isAddedFavorite());

                                                realm.beginTransaction();
                                                FavoriteNewsItem dbObject = realm.copyToRealmOrUpdate(favoriteNewsItem);
                                                if(dbObject != null) {
                                                    Log.d(TAG, "dbObjected : " + dbObject.getMtitle());
                                                }
                                                realm.commitTransaction();

                                            }
                                            finally {
                                                if(realm != null) {
                                                    realm.close();
                                                }
                                            }
                                        }
                                    }

                                    if(sendNotifications && newFeedsCounter > 0) {
                                        int not_nu = generateRandom();

                                        String summaryStr;

                                        if(newFeedsCounter == 1) {
                                            summaryStr = "You have " + newFeedsCounter + " story to follow. Touch to open App!!";
                                        } else {
                                            summaryStr = "You have " + newFeedsCounter + " stories to follow. Touch to open App!!";
                                        }

                                        Bundle bundle = new Bundle();
                                        bundle.putInt("tabPositionToOpen", 2);

                                        PugNotification.with(mContext)
                                                .load()
                                                .identifier(not_nu)
                                                .title("News Diary - World News")
                                                .message(summaryStr)
                                                .inboxStyle(inboxStyleLinesList.toArray(new String[0]), "News Diary - World News", summaryStr)
                                                .smallIcon(R.drawable.ic_pib_articles)
                                                .flags(Notification.DEFAULT_ALL)
                                                .autoCancel(true)
                                                .color(R.color.colorPrimary)
                                                .click(MainActivity.class)
                                                .simple()
                                                .build();
                                    }


                                } else {

                                    // there are no items in database
                                    // add all feeds to DB
                                    for (FeedItem item : respFeed.getmChannel().getFeedItems()) {
                                        FavoriteNewsItem favoriteNewsItem = Feed2DBConversion.convertFeedToDBModelObject(item, "World");
                                        realm = Realm.getDefaultInstance();
                                        try {
                                            realm.beginTransaction();
                                            FavoriteNewsItem dbObject = realm.copyToRealm(favoriteNewsItem);
                                            if(dbObject != null) {
                                                Log.d(TAG, "dbObjected : " + dbObject.getMtitle());
                                            }
                                            realm.commitTransaction();
                                        } catch (RealmPrimaryKeyConstraintException ex) {

                                            realm.cancelTransaction();
                                            // get an existing object and update it with current details
                                            FavoriteNewsItem dbItem =
                                                    realm.where(FavoriteNewsItem.class).equalTo("mlink", favoriteNewsItem.getMlink()).findFirst();
                                            favoriteNewsItem.setmTags(dbItem.getmTags());
                                            favoriteNewsItem.setAddedFavorite(dbItem.isAddedFavorite());

                                            realm.beginTransaction();
                                            FavoriteNewsItem dbObject = realm.copyToRealmOrUpdate(favoriteNewsItem);
                                            if(dbObject != null) {
                                                Log.d(TAG, "dbObjected : " + dbObject.getMtitle());
                                            }
                                            realm.commitTransaction();

                                        }
                                        finally {
                                            if(realm != null) {
                                                realm.close();
                                            }
                                        }
                                    }
                                }

                            } catch (RealmException ex) {

                            } finally {
                                if (realm != null) {
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

                worldNotification = false;
            }
        }
    }
}