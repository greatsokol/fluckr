package com.greatsokol.fluckr;

import android.graphics.Bitmap;

import com.greatsokol.fluckr.etc.ImageLoader;
import com.greatsokol.fluckr.model.Photo;

import java.util.Date;

public class ImageListItem {

    static final int VIEW_TYPE_UNKNOWN = -2;
    static final int VIEW_TYPE_PLACEHOLDER = -1;
    public static final int VIEW_TYPE_LOADING = 0;
    static final int VIEW_TYPE_IMAGE = 1;
    public static final int VIEW_TYPE_DATE = 2;

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
    public ImageListItem(Date date, int pagesTotal, int page, int numberOnPage, String title, String details, Bitmap thumbnail,
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
    public ImageListItem(Date DateOfList, int TotalPages, int PageNumber, int NumberOnPage, Photo photo) throws Exception {
        mTitle = photo.getTitle();
        mPage = PageNumber;
        mPagesTotal = TotalPages;
        mNumberOnPage = NumberOnPage;
        mDetails = photo.getDescription().getContent();
        String thumbnailUrl = photo.getThumbnailUrl();
        String fullsizeUrl = photo.getFullsizeUrl();

        assert thumbnailUrl != null;
        mBitmapThumbnail = ImageLoader.loadPicture(thumbnailUrl, FluckrApp.getCacheDirectory(), ImageLoader.THUMB_SIZE);;
        mThumbnailUrl = thumbnailUrl;
        mFullsizeUrl = fullsizeUrl;
        mDate = DateOfList;
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
