package com.greatsokol.fluckr;

import android.graphics.Bitmap;

import java.util.Date;

class ImageListItem {

    static final int VIEW_TYPE_UNKNOWN = -1;
    static final int VIEW_TYPE_LOADING = 0;
    static final int VIEW_TYPE_NORMAL = 1;

    private Date mDate;
    private String mTitle;
    private String mDetails;
    private Bitmap mBitmapThumbnail;
    private String mThumbnailUrl;
    private String mFullsizeUrl;


    private int mViewType = VIEW_TYPE_NORMAL;
    ImageListItem(int viewtype){mViewType = viewtype;}
    ImageListItem(Date date, String title, String details, Bitmap thumbnail,
                  String thumbnailUrl, String fullsizeUrl) {
        mTitle = title;
        mDetails = details;
        mBitmapThumbnail = thumbnail;
        mThumbnailUrl = thumbnailUrl;
        mFullsizeUrl = fullsizeUrl;
        mDate = date;
    }

    Date getDate(){return mDate;}
    int getViewType(){return mViewType;}
    String getTitle() {
        return mTitle;
    }
    String getDetails() {return mDetails;}
    Bitmap getBitmapThumbnail(){return mBitmapThumbnail;}
    String getThumbnailUrl(){return mThumbnailUrl;}
    String getFullsizeUrl(){return mFullsizeUrl;}
}
