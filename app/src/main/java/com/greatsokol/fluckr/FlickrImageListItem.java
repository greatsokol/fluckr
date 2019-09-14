package com.greatsokol.fluckr;

import android.graphics.Bitmap;

class FlickrImageListItem {

    static final int VIEW_TYPE_UNKNOWN = -1;
    static final int VIEW_TYPE_LOADING = 0;
    static final int VIEW_TYPE_NORMAL = 1;

    private String mTitle;
    private String mDetails;
    private Bitmap mBitmapThumbnail;
    private String mCacheFilePath;

    private int mViewType = VIEW_TYPE_NORMAL;
    FlickrImageListItem(int viewtype){mViewType = viewtype;}
    FlickrImageListItem(String title, String details, Bitmap thumbnail, String cacheFilePath) {
        mTitle = title;
        mDetails = details;
        mBitmapThumbnail = thumbnail;
        mCacheFilePath = cacheFilePath;
    }

    int getViewType(){return mViewType;}
    String getTitle() {
        return mTitle;
    }
    String getDetails() {return mDetails;}
    Bitmap getBitmapThumbnail(){return mBitmapThumbnail;}
    String getCacheFilePath(){return mCacheFilePath;}
}
