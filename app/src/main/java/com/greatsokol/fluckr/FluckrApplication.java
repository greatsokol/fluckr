package com.greatsokol.fluckr;

import android.app.Application;
import java.util.ArrayList;


public class FluckrApplication extends Application {

    private FlickrImageListAdapter mAdapter;
    FlickrImageListAdapter getAdapter(){return mAdapter;}


    @Override
    public void onCreate() {
        super.onCreate();
        mAdapter = new FlickrImageListAdapter(new ArrayList<FlickrApi.FlickrImageListItem>());
    }
}
