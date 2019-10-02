package com.greatsokol.fluckr;

import android.app.Application;

import java.util.ArrayList;

public class FluckrApp extends Application {
    private ImageListAdapter mAdapter;
    private ImageListAdapter mSearchAdapter;

    ImageListAdapter getTodayListAdapter(){return mAdapter;}
    ImageListAdapter getSearchAdapter(){return mSearchAdapter;}

    @Override
    public void onCreate() {
        super.onCreate();
        mAdapter = new ImageListAdapter(new ArrayList<ImageListItem>());
        mSearchAdapter = new ImageListAdapter(new ArrayList<ImageListItem>());
    }
}
