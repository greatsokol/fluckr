package com.greatsokol.fluckr;

class FlickrImageListItem {
    static final int VIEW_TYPE_LOADING = 0;
    static final int VIEW_TYPE_NORMAL = 1;

    private String mTitle;
    private int mViewType = VIEW_TYPE_NORMAL;
    FlickrImageListItem(int viewtype){mViewType = viewtype;}
    FlickrImageListItem(String title){setTitle(title);}
    private void setTitle(String title) {
        mTitle = title;
    }
    String getTitle() {
        return mTitle;
    }
    int getViewType(){return mViewType;}
}
