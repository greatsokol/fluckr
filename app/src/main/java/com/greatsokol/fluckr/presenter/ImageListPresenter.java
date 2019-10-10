package com.greatsokol.fluckr.presenter;

import com.greatsokol.fluckr.contract.ContractMain;
import com.greatsokol.fluckr.view.ImageListItem;
import com.greatsokol.fluckr.model.ModelMain;

import java.util.ArrayList;
import java.util.Date;

public class ImageListPresenter implements ContractMain.ImageListPresenter {
    private ContractMain.ViewMain mView;
    private ContractMain.Model mModel;
    private boolean isLoadingNow;

    @Override
    public void onViewCreate(ContractMain.ViewMain view, boolean loadInitial, Date savedDate, int savedPage, int savedItemNumber, String searchForThis) {
        mView = view;
        mModel = new ModelMain();
        if(loadInitial) {
            isLoadingNow = true;
            view.onStartLoading(true);
            mModel.loadInitialPage(savedDate, savedPage, savedItemNumber, searchForThis, new ContractMain.Model.OnResponseCallback() {
                @Override
                public void onResponse(ArrayList<ImageListItem> items) {
                    mView.onImageListDownloaded(items);
                    mView.onStopLoading();
                    isLoadingNow = false;
                }

                @Override
                public void onFailure(String message) {
                    mView.onStopLoading();
                    mView.onFailure(message);
                    isLoadingNow = false;
                }
            });
        }
    }

    @Override
    public void onViewDestroy() {
        mView = null;
        mModel = null;
    }

    @Override
    public void onScrolledDown() {
        if(isLoadingNow)return;
        isLoadingNow = true;
    }

    @Override
    public void onScrolledUp() {
        if(isLoadingNow)return;
        isLoadingNow = true;
    }
}
