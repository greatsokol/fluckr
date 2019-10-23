package com.greatsokol.fluckr.contract;

import com.greatsokol.fluckr.models.api.Photos;
import com.greatsokol.fluckr.views.ImageListItem;

import java.util.ArrayList;
import java.util.Date;

public interface MainContract {

    interface ViewMain {
        void onImageListDownloaded(ArrayList<ImageListItem> items, boolean addAtBottom, int restorePosition);
        void onFailure(String message);
        void onStartLoading(boolean addProgressbarAtBottom);
        void onStopLoading();

        String getSearchPhrase();
        ImageListItem.ListItemPageParams getLastItemPageParams();
        ImageListItem.ListItemPageParams getFirstItemPageParams();
    }

    interface ImageListPresenter {
        void onViewCreate(MainContract.ViewMain view, Date date, int page, int itemNumber);
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
