package com.greatsokol.fluckr;

import android.graphics.Bitmap;

class FlickrImageListItem {
    static final int VIEW_TYPE_LOADING = 0;
    static final int VIEW_TYPE_NORMAL = 1;

    private String mTitle;
    private String mDetails;
    /*private String mId;
    private String mFarm;
    private String mSecret;
    private String mServer;*/
    private Bitmap mBitmapThumbnail;

    private int mViewType = VIEW_TYPE_NORMAL;
    FlickrImageListItem(int viewtype){mViewType = viewtype;}
    FlickrImageListItem(String title, String details, /*String id, String secret,
                        String server, String farm,*/ Bitmap thumbnail) {
        mTitle = title;
        mDetails = details;
        /*mId = id;
        mSecret = secret;
        mServer = server;
        mFarm = farm;*/
        mBitmapThumbnail = thumbnail;
    }

    int getViewType(){return mViewType;}
    String getTitle() {
        return mTitle;
    }
    String getDetails() {return mDetails;}
    /*String getId(){return mId;}
    String getFarm(){return mFarm;}
    String getSecret(){return mSecret;}
    String getServer(){return mServer;}*/
    Bitmap getBitmapThumbnail(){return mBitmapThumbnail;}
}
