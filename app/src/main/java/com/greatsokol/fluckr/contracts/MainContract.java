package com.greatsokol.fluckr.contracts;

import com.greatsokol.fluckr.models.api.Photos;
import com.greatsokol.fluckr.views.ImageListItem;

import java.util.ArrayList;
import java.util.Date;

public interface MainContract {

    interface MainView {
        void onImageListDownloaded(ArrayList<ImageListItem> items, boolean addAtBottom, int restorePosition);
        void onFailure(String message);
        void onStartLoading();
        void onStopLoading();

        String getSearchPhrase();
        ImageListItem.ListItemPageParams getLastItemPageParams();
        ImageListItem.ListItemPageParams getFirstItemPageParams();
    }

    interface Presenter {
        void onViewCreate(MainView view, Date date, int page, int itemNumber);
        void onViewDestroy();
        void onScrolledDown();
        void onScrolledUp();
    }

    interface Model{
        void loadPage(Date savedDate, int savedPage, String searchForThis, OnResponseCallback callback);
        interface OnResponseCallback{
            void onResponse(Photos flickrPhotos);
            void onFailure(String message);
        }
    }



}
