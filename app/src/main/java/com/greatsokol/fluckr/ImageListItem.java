package com.greatsokol.fluckr;

import android.graphics.Bitmap;

import java.util.Date;

class ImageListItem {

    static final int VIEW_TYPE_UNKNOWN = -2;
    static final int VIEW_TYPE_PLACEHOLDER = -1;
    static final int VIEW_TYPE_LOADING = 0;
    static final int VIEW_TYPE_IMAGE = 1;
    static final int VIEW_TYPE_DATE = 2;

    private int mPage;
    private Date mDate;
    private String mTitle;
    private String mDetails;
    private Bitmap mBitmapThumbnail;
    private String mThumbnailUrl;
    private String mFullsizeUrl;


    private int mViewType = VIEW_TYPE_IMAGE;
    ImageListItem(int viewtype){mViewType = viewtype;}
    ImageListItem(Date date, int page, String title, String details, Bitmap thumbnail,
                  String thumbnailUrl, String fullsizeUrl) {
        mTitle = title;
        mPage = page;
        mDetails = details;
        mBitmapThumbnail = thumbnail;
        mThumbnailUrl = thumbnailUrl;
        mFullsizeUrl = fullsizeUrl;
        mDate = date;
    }
    ImageListItem(int viewtype, Date date, int page){
        mViewType = viewtype;
        mPage = page;
        mDate = date;
    }
    ImageListItem(Date date, int page){
        mViewType = VIEW_TYPE_DATE;
        mPage = page;
        mDate = date;
    }

    Date getDate(){return mDate;}
    int getPage(){return mPage;}
    int getViewType(){return mViewType;}
    String getTitle() {
        return mTitle;
    }
    String getDetails() {return mDetails;}
    Bitmap getBitmapThumbnail(){return mBitmapThumbnail;}
    String getThumbnailUrl(){return mThumbnailUrl;}
    String getFullsizeUrl(){return mFullsizeUrl;}
}
