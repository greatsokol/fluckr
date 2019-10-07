package com.greatsokol.fluckr.view;

import android.app.Application;

import com.greatsokol.fluckr.service.FlickrInterestingnessImageListApi;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FluckrApp extends Application {
    private ImageListAdapter mAdapter;
    private ImageListAdapter mSearchAdapter;
    private static String mCacheDir;

    ImageListAdapter getTodayListAdapter(){return mAdapter;}
    ImageListAdapter getSearchAdapter(){return mSearchAdapter;}

    private static FlickrInterestingnessImageListApi mFlickrInterestingnessImageListApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        mAdapter = new ImageListAdapter(new ArrayList<ImageListItem>());
        mSearchAdapter = new ImageListAdapter(new ArrayList<ImageListItem>());

        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.flickr.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mFlickrInterestingnessImageListApi = retrofit.create(FlickrInterestingnessImageListApi.class);

        mCacheDir = getCacheDir().getAbsolutePath();
    }

    public static FlickrInterestingnessImageListApi getApi(){return mFlickrInterestingnessImageListApi;}
    public static String getCacheDirectory(){return mCacheDir;}
}
