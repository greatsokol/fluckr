package com.greatsokol.fluckr.views;

import com.greatsokol.fluckr.etc.ConstsAndUtils;
import com.greatsokol.fluckr.models.api.Photo;

import java.util.Date;

public class ImageListItem {

    static final int VIEW_TYPE_UNKNOWN = -2;
    static final int VIEW_TYPE_PLACEHOLDER = -1;
    static final int VIEW_TYPE_IMAGE = 1;
    public static final int VIEW_TYPE_DATE = 2;

    private ListItemPageParams pageParams;
    private String mTitle;
    private String mDetails;
    private String mThumbnailUrl;
    private String mFullsizeUrl;

    public class ListItemPageParams {
        private int mNumberOnPage;
        private int mPage;
        private int mPagesTotal;
        private Date mDate;
        ListItemPageParams(int numberOnPage, Date date, int page, int pagesTotal){
            mNumberOnPage = numberOnPage;
            mDate = date;
            mPage = page;
            mPagesTotal = pagesTotal;
        }
        public Date getDate(){return mDate;}
        public int getPagesTotal(){return mPagesTotal;}
        public int getPage(){return mPage;}
        public int getNumberOnPage(){return mNumberOnPage;}
    }

    private int mViewType = VIEW_TYPE_IMAGE;
    ImageListItem(int viewtype) {
        mViewType = viewtype;
        pageParams = new ListItemPageParams(
                ConstsAndUtils.NO_POSITION,
                null,
                ConstsAndUtils.NO_PAGE,
                ConstsAndUtils.NO_PAGE);
    }
    public ImageListItem(Date DateOfList, int totalPages, int PageNumber, int NumberOnPage, Photo photo) {
        mTitle = photo.getTitle();
        mDetails = photo.getDescription().getContent();
        String thumbnailUrl = photo.getThumbnailUrl();
        String fullsizeUrl = photo.getFullsizeUrl();
        assert thumbnailUrl != null;
        mThumbnailUrl = thumbnailUrl;
        mFullsizeUrl = fullsizeUrl;
        pageParams = new ListItemPageParams(NumberOnPage, DateOfList, PageNumber, totalPages);
    }
    ImageListItem(int viewtype, Date date, int totalPages, int page){
        mViewType = viewtype;
        pageParams = new ListItemPageParams(
                ConstsAndUtils.NO_POSITION,
                date, page,
                totalPages);
    }
    public ImageListItem(Date date, int page){
        mViewType = VIEW_TYPE_DATE;
        pageParams = new ListItemPageParams(
                ConstsAndUtils.NO_POSITION,
                date, page,
                ConstsAndUtils.NO_PAGE);
    }


    int getViewType(){return mViewType;}
    String getTitle() {
        return mTitle;
    }
    String getDetails() {return mDetails;}
    String getThumbnailUrl(){return mThumbnailUrl;}
    String getFullsizeUrl(){return mFullsizeUrl;}
    public ListItemPageParams getPageParams(){return pageParams;}
}
