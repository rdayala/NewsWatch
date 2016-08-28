package com.rdayala.example.newswatch.service;


import com.rdayala.example.newswatch.model.Feed;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by rdayala on 8/4/2016.
 */
public interface FeedDataService {

    @GET("{category}")
    Call<Feed> getItems(@Path("category") String category);

}
