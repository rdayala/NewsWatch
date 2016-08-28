package com.rdayala.example.newswatch.service;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by rdayala on 8/4/2016.
 */

public class ServiceGenerator {

    public static String apiBaseUrl =  "http://feeds.feedburner.com/"; // "http://www.livemint.com/rss/";

    private static Retrofit retrofit;

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .baseUrl(apiBaseUrl);

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static void changeApiBaseUrl(String newApiBaseUrl) {
        apiBaseUrl = newApiBaseUrl;

        builder = new Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(new OkHttpClient())
                .baseUrl(apiBaseUrl);
    }

    public static <S> S createService(Class<S> serviceClass) {

        builder.client(httpClient.build());
        retrofit = builder.build();

        return retrofit.create(serviceClass);
    }

    public static Retrofit retrofit() {
        return retrofit;
    }
}