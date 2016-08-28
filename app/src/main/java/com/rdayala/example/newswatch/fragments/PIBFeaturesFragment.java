package com.rdayala.example.newswatch.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rdayala.example.newswatch.ContentActivity;
import com.rdayala.example.newswatch.R;
import com.rdayala.example.newswatch.adapter.NewsItemAdapter;
import com.rdayala.example.newswatch.model.Feed;
import com.rdayala.example.newswatch.model.FeedItem;
import com.rdayala.example.newswatch.service.FeedDataService;
import com.rdayala.example.newswatch.service.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rdayala on 8/18/2016.
 */
public class PIBFeaturesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    NewsItemAdapter mAdapter;
    List<FeedItem> mItems = null;
    String savedValue = null;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public PIBFeaturesFragment() {
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
        View view = inflater.inflate(R.layout.fragment_pib_features, container, false);

        Log.d("PIBFeaturesNews", "onCreateView called!!");

        mRecyclerView = (RecyclerView)view.findViewById(R.id.pib_features_news_rv);
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.pib_features_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(savedInstanceState != null) {
            savedValue = savedInstanceState.getString("pibFeaturesNewsState");
            mItems = savedInstanceState.getParcelableArrayList("pibFeaturesNews");
        }

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("PIBFeaturesNews", "onActivityCreated called!");
        if(savedValue == null || mItems == null) {
            loadPIBFeaturesNewsFeeds();
        }
    }

    @Override

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        Log.d("PIBFeaturesNews", "onSaveInstanceState called!");
        if(mItems != null) {
            state.putSerializable("pibFeaturesNewsState", "yes");
            state.putParcelableArrayList("pibFeaturesNews", new ArrayList<>(mItems));
        }
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        loadPIBFeaturesNewsFeeds();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(savedValue != null) {
            Log.d("PIBFeaturesNews", "onResume saved state is available!!");
            updateData();
        }
    }

    public void updateData() {
        mAdapter = new NewsItemAdapter(getContext(), mItems);
        mAdapter.setFeedTag("PIB");
        mRecyclerView.setAdapter(mAdapter);
        ((ContentActivity)getActivity()).setNewsAdapter(mAdapter);
        ((ContentActivity)getActivity()).setData(mItems);
    }

    public void loadPIBFeaturesNewsFeeds() {

        // showing refresh animation before making http call
        mSwipeRefreshLayout.setRefreshing(true);

        // Create a simple REST adapter which points to GitHub’s API
        FeedDataService service = ServiceGenerator.createService(FeedDataService.class);

        Call<Feed> call = service.getItems("nic/DdLt");

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {

                if(response.isSuccessful()) {
                    Feed respFeed = response.body();

                    // mItems = respFeed.getmChannel().getFeedItems();
                    mItems = new ArrayList<FeedItem>();

                    for(FeedItem item : respFeed.getmChannel().getFeedItems()) {

                        String title = item.getMtitle().trim();
                        String description = item.getMdescription().trim();
                        // String pubDate = item.getMpubDate().trim();

                        FeedItem trimmedItem = new FeedItem();
                        trimmedItem.setMtitle(Html.fromHtml(title).toString().trim());
                        trimmedItem.setMdescription(Html.fromHtml(description).toString().trim());
                        trimmedItem.setMpubDate(item.getMpubDate());
                        trimmedItem.setMlink(item.getMlink());

                        mItems.add(trimmedItem);
                    }

                    updateData();

                    for(FeedItem item : mItems) {
                        Log.d("PIBFeaturesNews ", "Item : " + item.getMtitle() + ", Link: " + item.getMlink());
                    }

                }
                else
                {
                    Log.e("PIBFeaturesNews : " , "Request failed - Cannot request GitHub repositories");
                }
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e("PIBFeaturesNews : ", "Error fetching repos : " + t.getMessage());
            }
        });

        // stopping swipe refresh
        mSwipeRefreshLayout.setRefreshing(false);
    }

}
