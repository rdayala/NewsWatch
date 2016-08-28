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

import com.rdayala.example.newswatch.MainActivity;
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
 * Created by rdayala on 8/4/2016.
 */
public class EditorialsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    NewsItemAdapter mAdapter;
    List<FeedItem> mItems = null;
    String savedValue = null;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public EditorialsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_editorials, container, false);

        Log.d("EditorialNews", "onCreateView called!!");

        mRecyclerView = (RecyclerView)view.findViewById(R.id.editorials_news_rv);
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.editorials_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(savedInstanceState != null) {
            savedValue = savedInstanceState.getString("editorialsNewsState");
            mItems = savedInstanceState.getParcelableArrayList("editorialsNews");
        }

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("EditorialNews", "onActivityCreated called!");
        if(savedValue == null || mItems == null) {
            loadEditorialsNewsFeeds();
        }
    }

    @Override

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        Log.d("EditorialNews", "onSaveInstanceState called!");
        if(mItems != null) {
            state.putSerializable("editorialsNewsState", "yes");
            state.putParcelableArrayList("editorialsNews", new ArrayList<>(mItems));
        }

    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        loadEditorialsNewsFeeds();
    }

    public void refresh() {
        loadEditorialsNewsFeeds();
    }

    @Override
    public void onResume() {

        super.onResume();

        if(savedValue != null) {
            Log.d("EditorialNews", "onResume saved state is available!!");
            updateData();
        }
    }

    public void setSearchFilterData() {
        ((MainActivity)getActivity()).setAdapter(mAdapter);
        ((MainActivity)getActivity()).setData(mItems);
    }

    public void updateData() {
        mAdapter = new NewsItemAdapter(getContext(), mItems);
        mAdapter.setFeedTag("Editorial");
        mRecyclerView.setAdapter(mAdapter);
    }

    public void loadEditorialsNewsFeeds() {

        // showing refresh animation before making http call
        mSwipeRefreshLayout.setRefreshing(true);

        // Create a simple REST adapter which points to GitHub’s API
        FeedDataService service = ServiceGenerator.createService(FeedDataService.class);

        Call<Feed> call = service.getItems("thehindu/WDvB");

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {

                if(response.isSuccessful()) {
                    Feed respFeed = response.body();

                    // mItems = respFeed.getmChannel().getFeedItems();
                    mItems = new ArrayList<FeedItem>();

                    for(FeedItem item : respFeed.getmChannel().getFeedItems()) {

                        String title = Html.fromHtml(item.getMtitle().trim()).toString();
                        String description = Html.fromHtml(item.getMdescription().trim()).toString();
                        String pubDate = item.getMpubDate().trim();

                        FeedItem trimmedItem = new FeedItem();
                        trimmedItem.setMtitle(title);
                        trimmedItem.setMdescription(description);
                        trimmedItem.setMpubDate(pubDate);
                        trimmedItem.setMlink(item.getMlink());

                        mItems.add(trimmedItem);
                    }

                    updateData();

                    for(FeedItem item : mItems) {
                        Log.d("EditorialNews-Item : " , item.getMtitle() + ", Link: " + item.getMlink());
                    }
                }
                else
                {
                    Log.e("EditorialNews : " , "Request failed - Cannot request GitHub repositories");
                }

            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e("EditorialNews : ", "Error fetching repos : " + t.getMessage());
            }
        });

        // stopping swipe refresh
        mSwipeRefreshLayout.setRefreshing(false);
    }

}
