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
import android.widget.Toast;

import com.rdayala.example.newswatch.ContentActivity;
import com.rdayala.example.newswatch.R;
import com.rdayala.example.newswatch.adapter.FavoritesRealmAdapter;
import com.rdayala.example.newswatch.model.FavoriteNewsItem;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by rdayala on 8/22/2016.
 */
public class FavoritesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    FavoritesRealmAdapter mAdapter;
    RealmList<FavoriteNewsItem> mResults = null;
    String savedValue = null;
    Realm mRealm;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        Log.d("FavoriteNews", "onCreateView called!!");

        mRecyclerView = (RecyclerView)view.findViewById(R.id.favorites_news_rv);
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.favorites_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRealm = Realm.getDefaultInstance();

        if(savedInstanceState != null) {
            savedValue = savedInstanceState.getString("favoriteNewsState");
            // mItems = savedInstanceState.getParcelableArrayList("favoriteNews");
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("FavoriteNews", "onActivityCreated called!");
        if(savedValue == null || mResults == null) {
            loadFavoriteNewsFeeds();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        Log.d("FavoriteNews", "onSaveInstanceState called!");
        if(mResults != null) {
            state.putSerializable("favoriteNewsState", "yes");
            // state.putParcelableArrayList("favoriteNews", new ArrayList<>(mItems));
        }
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        ((ContentActivity)getContext()).getSupportActionBar().setTitle("Favorites");
        loadFavoriteNewsFeeds();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(savedValue != null) {
            Log.d("FavoriteNews", "onResume saved state is available!!");
            updateData();
        }
    }

    public void updateData() {
        mAdapter = new FavoritesRealmAdapter(getContext(), mRealm, mResults);
        mRecyclerView.setAdapter(mAdapter);
        ((ContentActivity)getActivity()).setFavAdapter(mAdapter);
        ((ContentActivity)getActivity()).setRealmData(mResults);
        if(mResults == null || mResults.size() == 0) {
            Toast.makeText(getContext(), "You haven't Bookmarked any item.", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getContext(), "Loaded Bookmarks : " + mResults.size(), Toast.LENGTH_LONG).show();
        }
    }

    public void loadFavoriteNewsFeeds() {
        // showing refresh animation before making http call
        mSwipeRefreshLayout.setRefreshing(true);

        RealmResults<FavoriteNewsItem> results = mRealm.where(FavoriteNewsItem.class).findAll();
        if(results != null) {
            mResults = convertResultToList(results);
        }
        if(mResults != null) {
            updateData();
        }

        // stopping swipe refresh
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public RealmList<FavoriteNewsItem> convertResultToList(RealmResults<FavoriteNewsItem> realmResultsList){

        RealmList <FavoriteNewsItem> results = new RealmList<FavoriteNewsItem>();
        for(FavoriteNewsItem favoriteNewsItem : realmResultsList){
            results.add(copy(favoriteNewsItem));
        }
        return results;
    }

    private FavoriteNewsItem copy(FavoriteNewsItem favItem){
        FavoriteNewsItem o = new FavoriteNewsItem();

        o.setMtitle(favItem.getMtitle());
        o.setMlink(favItem.getMlink());
        o.setMdescription(favItem.getMdescription());
        o.setMpubDate(favItem.getMpubDate());
        o.setmTags(favItem.getmTags());
        return o;
    }
}
