package com.greatsokol.fluckr;

import android.app.Application;

import java.util.ArrayList;


public class FluckrApplication extends Application {
    private int mCurrentPage = 1;
    private FlickrImageListAdapter mAdapter;
    FlickrImageListAdapter getAdapter(){return mAdapter;}
    int getCurrentPage(){return mCurrentPage;}
    void setCurrentPage(int number){mCurrentPage = number;};

    @Override
    public void onCreate() {
        super.onCreate();
        mAdapter = new FlickrImageListAdapter(new ArrayList<FlickrApi.FlickrImageListItem>());
    }
}
