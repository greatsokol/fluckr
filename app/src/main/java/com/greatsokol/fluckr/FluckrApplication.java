package com.greatsokol.fluckr;

import android.app.Application;
import java.util.ArrayList;


public class FluckrApplication extends Application {

    private ImageListAdapter mAdapter;
    private ImageListAdapter mSearchAdapter;
    ImageListAdapter getAdapter(){return mAdapter;}
    ImageListAdapter getSearchAdapter(){return mSearchAdapter;}


    @Override
    public void onCreate() {
        super.onCreate();
        mAdapter = new ImageListAdapter(new ArrayList<ImageListItem>());
        mSearchAdapter = new ImageListAdapter(new ArrayList<ImageListItem>());
    }
}
