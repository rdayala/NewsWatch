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
 * Created by rdayala on 8/22/2016.
 */
public class NewsAnalysisFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    NewsItemAdapter mAdapter;
    List<FeedItem> mItems = null;
    String savedValue = null;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public NewsAnalysisFragment() {
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
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);

        Log.d("NewsAnalysisFragment", "onCreateView called!!");

        mRecyclerView = (RecyclerView)view.findViewById(R.id.news_analysis_rv);
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.analysis_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(savedInstanceState != null) {
            savedValue = savedInstanceState.getString("analysisNewsState");
            mItems = savedInstanceState.getParcelableArrayList("analysisNews");
        }

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("NewsAnalysisFragment", "onActivityCreated called!");
        if(savedValue == null || mItems == null) {
            loadNewsAnalysisFeeds();
        }
    }

    @Override

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        Log.d("NewsAnalysisFragment", "onSaveInstanceState called!");
        if(mItems != null) {
            state.putSerializable("analysisNewsState", "yes");
            state.putParcelableArrayList("analysisNews", new ArrayList<>(mItems));
        }
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        loadNewsAnalysisFeeds();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(savedValue != null) {
            Log.d("NewsAnalysisFragment", "onResume saved state is available!!");
            updateData();
        }
    }

    public void updateData() {
        mAdapter = new NewsItemAdapter(getContext(), mItems);
        mAdapter.setFeedTag("Analysis");
        mRecyclerView.setAdapter(mAdapter);
        ((ContentActivity)getActivity()).setNewsAdapter(mAdapter);
        ((ContentActivity)getActivity()).setData(mItems);
    }

    public void loadNewsAnalysisFeeds() {

        // showing refresh animation before making http call
        mSwipeRefreshLayout.setRefreshing(true);

        // Create a simple REST adapter
        FeedDataService service = ServiceGenerator.createService(FeedDataService.class);

        Call<Feed> call = service.getItems("AnalysisNews");

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
                        String pubDate = item.getMpubDate().trim();

                        FeedItem trimmedItem = new FeedItem();
                        trimmedItem.setMtitle(Html.fromHtml(title).toString().trim());
                        trimmedItem.setMdescription(Html.fromHtml(description).toString().trim());
                        trimmedItem.setMpubDate(pubDate);
                        trimmedItem.setMlink(item.getMlink());

                        mItems.add(trimmedItem);
                    }

                    updateData();

                    for(FeedItem item : mItems) {
                        Log.d("NewsAnalysisFragment ", "Item : " + item.getMtitle() + ", Link: " + item.getMlink());
                    }

                }
                else
                {
                    Log.e("NewsAnalysisFragment : " , "Request failed - Cannot request GitHub repositories");
                }
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e("NewsAnalysisFragment : ", "Error fetching repos : " + t.getMessage());
            }
        });

        // stopping swipe refresh
        mSwipeRefreshLayout.setRefreshing(false);
    }

}
