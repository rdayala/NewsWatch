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
import android.widget.Toast;

import com.rdayala.example.newswatch.ContentActivity;
import com.rdayala.example.newswatch.R;
import com.rdayala.example.newswatch.adapter.FavoritesRealmAdapter;
import com.rdayala.example.newswatch.model.FavoriteNewsItem;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by rdayala on 8/22/2016.
 */
public class FavoritesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "FavoritesFragment";

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    FavoritesRealmAdapter mAdapter;
    List<FavoriteNewsItem> mItems = null;
    String savedValue = null;
    SwipeRefreshLayout mSwipeRefreshLayout;
    boolean mDoRefresh = false;
    private ProgressBar pbar;

    public FavoritesFragment() {
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
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        Log.d(TAG, "onCreateView called!!");

        pbar = (ProgressBar)view.findViewById(R.id.favorites_progressbar);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.favorites_news_rv);
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.favorites_swipe_refresh_layout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeColors(R.color.orange, R.color.green, R.color.blue);
        }
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(savedInstanceState != null) {
            savedValue = savedInstanceState.getString("favoriteNewsState");
            mItems = savedInstanceState.getParcelableArrayList("favoriteNews");
        }

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated called!");
        if(savedValue == null || mItems == null) {
            loadFavoriteNewsFeeds();
        }
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        mDoRefresh = true;
        loadFavoriteNewsFeeds();
    }

    public void refresh() {
        loadFavoriteNewsFeeds();
    }

    @Override

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        Log.d(TAG, "onSaveInstanceState called!");
        if(mItems != null) {
            state.putSerializable("favoriteNewsState", "yes");
            state.putParcelableArrayList("favoriteNews", new ArrayList<>(mItems));
        }

    }

    @Override
    public void onResume() {

        super.onResume();

        if(savedValue != null) {
            Log.d(TAG, "onResume saved state is available!!");
            updateData();
        }
    }

    public void updateData() {

        if(savedValue == null || mItems == null || mDoRefresh) {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<FavoriteNewsItem> results = realm.where(FavoriteNewsItem.class).equalTo("isAddedFavorite", true).findAll();
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

        if (mDoRefresh) {
            // stopping swipe refresh
            mSwipeRefreshLayout.setRefreshing(false);
            mDoRefresh = false;
        }
        else {
            pbar.setVisibility(View.GONE);
        }

        if(mItems != null) {

            mAdapter = new FavoritesRealmAdapter(getContext(), mItems);
            mRecyclerView.setAdapter(mAdapter);
            ((ContentActivity)getContext()).setRealmData(mItems);
            ((ContentActivity)getContext()).setFavAdapter(mAdapter);
        } else
        {
            Toast.makeText(getContext(), "You haven't added any Bookmarks!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadFavoriteNewsFeeds() {

        if (mDoRefresh) {
            // showing refresh animation before making http call
            mSwipeRefreshLayout.setRefreshing(true);
        }

        updateData();
    }
}
