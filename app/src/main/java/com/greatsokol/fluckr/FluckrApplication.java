package com.greatsokol.fluckr;

import android.app.Application;

import java.util.ArrayList;

public class FluckrApplication extends Application {
    private FlickrImageListAdapter adapter;
    FlickrImageListAdapter getAdapter(){return adapter;}

    @Override
    public void onCreate() {
        super.onCreate();
        adapter = new FlickrImageListAdapter(new ArrayList<FlickrApi.FlickrImageListItem>());
    }
}
