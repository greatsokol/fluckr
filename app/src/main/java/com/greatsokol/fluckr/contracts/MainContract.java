package com.greatsokol.fluckr.contracts;

import com.greatsokol.fluckr.models.api.Photos;
import com.greatsokol.fluckr.views.ImageListItem;

import java.util.ArrayList;
import java.util.Date;

public interface MainContract {

    interface View {
        void onImageListDownloaded(ArrayList<ImageListItem> items, boolean addAtBottom, int restorePosition);
        void onFailure(String message);
        void onStartLoading();
        void onStopLoading();
        String getSearchPhrase();
    }

    interface Presenter {
        void onViewCreate(View view, final ImageListItem.ListItemPageParams pageParams);
        void onViewDestroy();
        void onScrolledDown(ImageListItem.ListItemPageParams params);
        void onScrolledUp(ImageListItem.ListItemPageParams params);
    }

    interface Model{
        void loadPage(Date savedDate, int savedPage, String searchForThis, OnResponseCallback callback);
        interface OnResponseCallback{
            void onResponse(Photos flickrPhotos);
            void onFailure(String message);
        }
    }



}
