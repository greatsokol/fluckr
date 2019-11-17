package com.greatsokol.fluckr.models;

import com.greatsokol.fluckr.contracts.MainContract;
import com.greatsokol.fluckr.etc.ConstsAndUtils;
import com.greatsokol.fluckr.models.api.FlickrImageList;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FlickrInterestingnessList implements MainContract.Model {
    private Retrofit retrofit
            = new Retrofit.Builder()
                .baseUrl("https://www.flickr.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    private FlickrRequestsApi.Interestingness
            interestingness = retrofit.create(FlickrRequestsApi.Interestingness.class);

    private FlickrRequestsApi.Search
            search = retrofit.create(FlickrRequestsApi.Search.class);

    private void __loadPage(final String searchFor,
                            final Date date,
                            final int page,
                            final OnResponseCallback onResponseCallback){
        final String APIKEY = "dcfa7bcdfe436387cefa172c2d3dc2ae";
        final String EXTRAS = "description,url_t,url_m,url_n,url_b,url_k,url_h";
        final String JSON = "json";
        final int PERPAGE = 45;
        final int NOJSONCALLBACK = 1;
        if(searchFor.isEmpty())
            interestingness.getList(APIKEY, ConstsAndUtils.DateToStr_yyyy_mm_dd(date),
                                    PERPAGE, page, EXTRAS, JSON, NOJSONCALLBACK).enqueue(
                        new Callback<FlickrImageList>() {
                            @Override
                            public void onResponse(Call<FlickrImageList> call, Response<FlickrImageList> response) {
                                assert response.body() != null;
                                onResponseCallback.onResponse(response.body().getPhotos());
                            }

                            @Override
                            public void onFailure(Call<FlickrImageList> call, Throwable t) {
                                t.printStackTrace();
                                onResponseCallback.onFailure(t.getMessage());
                            }
                });
        else
            search.getList(APIKEY, searchFor, PERPAGE, page, EXTRAS, JSON, NOJSONCALLBACK).enqueue(
                        new Callback<FlickrImageList>() {
                            @Override
                            public void onResponse(Call<FlickrImageList> call, Response<FlickrImageList> response) {
                                assert response.body() != null;
                                onResponseCallback.onResponse(response.body().getPhotos());
                            }

                            @Override
                            public void onFailure(Call<FlickrImageList> call, Throwable t) {
                                t.printStackTrace();
                                onResponseCallback.onFailure(t.getMessage());
                            }
                        });
    }

    @Override
    public void loadPage(Date date, int page, String searchForThis, OnResponseCallback onResponseCallback) {
        __loadPage(searchForThis, date, page, onResponseCallback);
    }
}
