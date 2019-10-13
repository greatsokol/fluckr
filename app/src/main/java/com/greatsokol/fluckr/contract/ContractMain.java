package com.greatsokol.fluckr.contract;

import com.greatsokol.fluckr.model.api.Photos;
import com.greatsokol.fluckr.view.ImageListItem;

import java.util.ArrayList;
import java.util.Date;

public interface ContractMain {

    interface ViewMain {
        void onImageListDownloaded(ArrayList<ImageListItem> items, boolean addAtBottom);
        void onFailure(String message);
        void onStartLoading(boolean addProgressbarAtBottom);
        void onStopLoading();

        String getSearchPhrase();
        ImageListItem.ListItemPageParams getLastItemPageParams();
        ImageListItem.ListItemPageParams getFirstItemPageParams();
    }

    interface ImageListPresenter {
        void onViewCreate(ContractMain.ViewMain view, boolean loadInitial, Date savedDate, int savedPage, int savedItemNumber);
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
