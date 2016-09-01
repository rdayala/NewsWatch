package com.rdayala.example.newswatch.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rdayala.example.newswatch.MainActivity;
import com.rdayala.example.newswatch.R;
import com.rdayala.example.newswatch.adapter.NewsItemAdapter;
import com.rdayala.example.newswatch.model.FavoriteNewsItem;
import com.rdayala.example.newswatch.model.Feed;
import com.rdayala.example.newswatch.model.FeedItem;
import com.rdayala.example.newswatch.service.FeedDataService;
import com.rdayala.example.newswatch.service.ServiceGenerator;
import com.rdayala.example.newswatch.utils.Feed2DBConversion;
import com.rdayala.example.newswatch.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rdayala on 8/4/2016.
 */
public class BusinessFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "BusinessFragment";

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    NewsItemAdapter mAdapter;
    List<FavoriteNewsItem> mItems = null;
    String savedValue = null;
    SwipeRefreshLayout mSwipeRefreshLayout;
    boolean mDoRefresh = false;
    boolean isConnectedToInternet = false;
    private ProgressBar pbar;
    TextView connectionStatusText;

    public BusinessFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business, container, false);

        Log.d(TAG, "onCreateView called!!");

        pbar = (ProgressBar)view.findViewById(R.id.business_progressbar);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.business_news_rv);
        connectionStatusText = (TextView)view.findViewById(R.id.business_connection_status);
        connectionStatusText.setVisibility(View.GONE);
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.business_swipe_refresh_layout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeColors(R.color.orange, R.color.green, R.color.blue);
        }
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(savedInstanceState != null) {
            savedValue = savedInstanceState.getString("businessNewsState");
            mItems = savedInstanceState.getParcelableArrayList("businessNews");
        }

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated called!");
        isConnectedToInternet = NetworkUtils.haveNetworkConnection(getContext());
        if((savedValue == null || mItems == null )) {
            loadBusinessNewsFeeds();
        }
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        Log.d(TAG, "setUserVisibleHint called! " + isFragmentLoaded);
//        if (isVisibleToUser && !isFragmentLoaded) {
//            isFragmentLoaded = true;
//        }
//    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        mDoRefresh = true;
        isConnectedToInternet = NetworkUtils.haveNetworkConnection(getContext());
        loadBusinessNewsFeeds();
    }

    public void refresh() {
        loadBusinessNewsFeeds();
    }

    @Override

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        Log.d(TAG, "onSaveInstanceState called!");
        if(mItems != null) {
            state.putSerializable("businessNewsState", "yes");
            state.putParcelableArrayList("businessNews", new ArrayList<>(mItems));
        }

    }

    @Override
    public void onResume() {

        super.onResume();
        if(savedValue != null) {
            Log.d(TAG, "onResume saved state is available!!");
            if(!NetworkUtils.haveNetworkConnection(getContext())) {
                connectionStatusText.setVisibility(View.VISIBLE);
            }
            updateData();
        }
    }

    public void setSearchFilterData() {
        ((MainActivity)getContext()).setAdapter(mAdapter);
        ((MainActivity)getContext()).setData(mItems);
    }

    public void updateData() {

        if(savedValue == null || mItems == null || mDoRefresh) {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<FavoriteNewsItem> results = realm.where(FavoriteNewsItem.class).equalTo("mCategory", "Economy").findAll();
            results = results.sort("mDateAdded", Sort.DESCENDING);
            if (results.size() > 0) {
                mItems = new ArrayList<FavoriteNewsItem>();
                for (FavoriteNewsItem item : results) {
                    FavoriteNewsItem favoriteNewsItem = new FavoriteNewsItem();
                    favoriteNewsItem.setMtitle(item.getMtitle());
                    favoriteNewsItem.setMlink(item.getMlink());
                    favoriteNewsItem.setMdescription(item.getMdescription());
                    favoriteNewsItem.setMpubDate(item.getMpubDate());
                    favoriteNewsItem.setmTags(item.getmTags());
                    favoriteNewsItem.setmCategory(item.getmCategory());
                    favoriteNewsItem.setmDateAdded(item.getmDateAdded());
                    favoriteNewsItem.setAddedFavorite(item.isAddedFavorite());

                    mItems.add(favoriteNewsItem);
                }
            }
            realm.close();
        }

        mAdapter = new NewsItemAdapter(getContext(), mItems);
        mAdapter.setDefaultTag("Economy");
        if (mDoRefresh) {
            // stopping swipe refresh
            mSwipeRefreshLayout.setRefreshing(false);
            mDoRefresh = false;
        }
        else {
            pbar.setVisibility(View.GONE);
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    public void loadBusinessNewsFeeds() {

        if (isConnectedToInternet) {

            if (!mDoRefresh) {
                Realm realm = Realm.getDefaultInstance();
                RealmResults<FavoriteNewsItem> results = realm.where(FavoriteNewsItem.class).equalTo("mCategory", "Economy").findAll();
                int numberOfItems = results.size();
                realm.close();
                if (numberOfItems > 0) {
                    updateData();
                    return;
                }
            }

            if (mDoRefresh) {
                // showing refresh animation before making http call
                mSwipeRefreshLayout.setRefreshing(true);
            }

            // Create a simple REST adapter which points to GitHub’s API
            FeedDataService service = ServiceGenerator.createService(FeedDataService.class);

            Call<Feed> call = service.getItems("business-standard/odSy");

            call.enqueue(new Callback<Feed>() {
                @Override
                public void onResponse(Call<Feed> call, Response<Feed> response) {

                    if (response.isSuccessful()) {
                        Feed respFeed = response.body();

                        for (FeedItem item : respFeed.getmChannel().getFeedItems()) {

                            FavoriteNewsItem favoriteNewsItem = Feed2DBConversion.convertFeedToDBModelObject(item, "Economy");

                            Realm realm = Realm.getDefaultInstance();
                            try {
                                realm.beginTransaction();
                                realm.copyToRealm(favoriteNewsItem);
                                realm.commitTransaction();
                            } catch (RealmPrimaryKeyConstraintException ex) {

                                realm.cancelTransaction();
                                // get an existing object and update it with current details
                                FavoriteNewsItem dbItem =
                                        realm.where(FavoriteNewsItem.class).equalTo("mlink", favoriteNewsItem.getMlink()).findFirst();
                                favoriteNewsItem.setmTags(dbItem.getmTags());
                                favoriteNewsItem.setAddedFavorite(dbItem.isAddedFavorite());

                                realm.beginTransaction();
                                realm.copyToRealmOrUpdate(favoriteNewsItem);
                                realm.commitTransaction();

                            }
                            finally {
                                if(realm != null) {
                                    realm.close();
                                }
                            }

                            Log.d(TAG, "Item : " + favoriteNewsItem.getMtitle() + ", Link: " + favoriteNewsItem.getMlink());
                        }

                        updateData();
                    } else {
                        Log.e(TAG, "Request failed - Cannot request GitHub repositories");
                    }
                }

                @Override
                public void onFailure(Call<Feed> call, Throwable t) {
                    Log.e(TAG, "Error fetching repos : " + t.getMessage());
                    if (mDoRefresh) {
                        // stopping swipe refresh
                        mSwipeRefreshLayout.setRefreshing(false);
                        mDoRefresh = false;
                    } else {
                        pbar.setVisibility(View.GONE);
                    }
                    Toast.makeText(getContext(), "Error while fetching feeds. Please make sure Internet connection is available. " +
                            "Try again!!", Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            connectionStatusText.setVisibility(View.VISIBLE);
            updateData();
        }
    }
}
