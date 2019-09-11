package com.greatsokol.fluckr;

import android.app.Application;
import java.util.ArrayList;


public class FluckrApplication extends Application {

    private FlickrImageListAdapter mAdapter;
    private FlickrImageListAdapter mSearchAdapter;
    FlickrImageListAdapter getAdapter(){return mAdapter;}
    FlickrImageListAdapter getSearchAdapter(){return mSearchAdapter;}


    @Override
    public void onCreate() {
        super.onCreate();
        mAdapter = new FlickrImageListAdapter(new ArrayList<FlickrImageListItem>());
        mSearchAdapter = new FlickrImageListAdapter(new ArrayList<FlickrImageListItem>());
    }
}
