package com.greatsokol.fluckr.contract;

import com.greatsokol.fluckr.view.ImageListItem;

import java.util.ArrayList;
import java.util.Date;

public interface ContractMain {
    public interface ViewMain {
        void onImageListDownloaded(ArrayList<ImageListItem> items);
        void onFailure(String message);
        void onStartLoading(boolean bAddProgressbarAtBottom);
        void onStopLoading();
    }

    interface ImageListPresenter {
        void onViewCreate(ContractMain.ViewMain view, boolean loadInitial, Date savedDate, int savedPage, int savedItemNumber, String searchForThis);
        void onViewDestroy();
        void onScrolledDown();
        void onScrolledUp();
    }

    interface Model{
        void loadUpperPage(String searchFor);
        void loadLowerPage(String searchFor);
        void loadInitialPage(Date savedDate, int savedPage, int savedItemNumber, String searchForThis, OnResponseCallback callback);
        interface OnResponseCallback{
            void onResponse(ArrayList<ImageListItem> items);
            void onFailure(String message);
        }
    }



}
