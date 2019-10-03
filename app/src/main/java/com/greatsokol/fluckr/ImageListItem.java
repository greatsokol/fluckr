package com.greatsokol.fluckr;

import android.graphics.Bitmap;

import java.util.Date;

class ImageListItem {

    static final int VIEW_TYPE_UNKNOWN = -2;
    static final int VIEW_TYPE_PLACEHOLDER = -1;
    static final int VIEW_TYPE_LOADING = 0;
    static final int VIEW_TYPE_IMAGE = 1;
    static final int VIEW_TYPE_DATE = 2;

    private int mNumberOnPage;
    private int mPage;
    private int mPagesTotal;
    private Date mDate;
    private String mTitle;
    private String mDetails;
    private Bitmap mBitmapThumbnail;
    private String mThumbnailUrl;
    private String mFullsizeUrl;


    private int mViewType = VIEW_TYPE_IMAGE;
    ImageListItem(int viewtype) {
        mViewType = viewtype;
        mNumberOnPage = -1;
        mPagesTotal = -1;
        mPage = -1;
    }
    ImageListItem(Date date, int pagesTotal, int page, int numberOnPage, String title, String details, Bitmap thumbnail,
                  String thumbnailUrl, String fullsizeUrl) {
        mTitle = title;
        mPage = page;
        mPagesTotal = pagesTotal;
        mNumberOnPage = numberOnPage;
        mDetails = details;
        mBitmapThumbnail = thumbnail;
        mThumbnailUrl = thumbnailUrl;
        mFullsizeUrl = fullsizeUrl;
        mDate = date;
    }
    ImageListItem(int viewtype, Date date, int pagesTotal, int page){
        mNumberOnPage = -1;
        mViewType = viewtype;
        mPage = page;
        mPagesTotal = pagesTotal;
        mDate = date;
    }
    ImageListItem(Date date, int page){
        mNumberOnPage = 1;
        mViewType = VIEW_TYPE_DATE;
        mPage = page;
        mDate = date;
    }

    Date getDate(){return mDate;}
    int getPagesTotal(){return mPagesTotal;}
    int getPage(){return mPage;}
    int getNumberOnPage(){return mNumberOnPage;}
    int getViewType(){return mViewType;}
    String getTitle() {
        return mTitle;
    }
    String getDetails() {return mDetails;}
    Bitmap getBitmapThumbnail(){return mBitmapThumbnail;}
    String getThumbnailUrl(){return mThumbnailUrl;}
    String getFullsizeUrl(){return mFullsizeUrl;}
}
