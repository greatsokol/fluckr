package com.greatsokol.fluckr;

import android.app.Application;

import com.greatsokol.fluckr.view.ImageListAdapter;
import com.greatsokol.fluckr.view.ImageListItem;

import java.util.ArrayList;

public class FluckrApp extends Application {
    private ImageListAdapter mAdapter;
    private ImageListAdapter mSearchAdapter;

    public ImageListAdapter getTodayListAdapter(){return mAdapter;}
    public ImageListAdapter getSearchAdapter(){return mSearchAdapter;}


    @Override
    public void onCreate() {
        super.onCreate();
        mAdapter = new ImageListAdapter(new ArrayList<ImageListItem>());
        mSearchAdapter = new ImageListAdapter(new ArrayList<ImageListItem>());
    }
}
