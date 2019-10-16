package com.greatsokol.fluckr.contract;

import android.view.View;

import com.greatsokol.fluckr.model.api.Photos;
import com.greatsokol.fluckr.view.ImageListAdapter;

import java.util.Date;

public interface ContractMain {

    interface ViewMain {
        void onItemClick(View view);
        void onFailure(String message);
    }

    interface ImageListPresenter {
        ImageListAdapter getAdapter();
        void onViewCreate(ContractMain.ViewMain view, Date date, int page, int itemNumber);
        void onViewDestroy();
        void onScrolledDown();
        void onScrolledUp();
        void setSearchPhrase(String searchPhrase);
    }

    interface Model{
        void loadPage(Date savedDate, int savedPage, String searchForThis, OnResponseCallback callback);
        interface OnResponseCallback{
            void onResponse(Photos flickrPhotos);
            void onFailure(String message);
        }
    }



}
