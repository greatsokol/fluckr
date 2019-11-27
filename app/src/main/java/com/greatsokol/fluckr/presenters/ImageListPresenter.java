package com.greatsokol.fluckr.presenters;

import com.greatsokol.fluckr.contracts.MainContract;
import com.greatsokol.fluckr.etc.ConstsAndUtils;
import com.greatsokol.fluckr.models.api.Photos;
import com.greatsokol.fluckr.views.ImageListItem;
import com.greatsokol.fluckr.models.FlickrInterestingnessList;

import java.util.Date;

public class ImageListPresenter implements MainContract.Presenter {
    private MainContract.View mView;
    private MainContract.Model mModel;
    private boolean isLoadingNow;

    private void startLoading(){
        isLoadingNow = true;
        if(mView!=null)
            mView.onStartLoading();
    }

    private void stopLoading(){
        isLoadingNow = false;
        if(mView!=null)
            mView.onStopLoading();
    }

    private void load(final ImageListItem.ListItemPageParams pageParams,
                      final boolean addAtBottom, final boolean firstLoad){
        startLoading();
        mModel.loadPage(
                pageParams.getDate(),
                pageParams.getPage(),
                mView.getSearchPhrase(),
                new MainContract.Model.OnResponseCallback() {
                    @Override
                    public void onResponse(Photos photos) {
                        if(mView==null)return;
                        if (photos!=null)
                            mView.onImageListDownloaded(
                                    Interactor.Translate(pageParams.getDate(), photos),
                                    addAtBottom, pageParams);
                        stopLoading();
                        if(firstLoad) {
                            if (addAtBottom)
                                onScrolledUp(firstLoad, pageParams); //<---- load upper page if exists
                            else
                                onScrolledDown(pageParams); //<---- load lower page if exists
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        if(mView==null)return;
                        mView.onFailure(message);
                        stopLoading();
                    }
                });
    }

    @Override
    public void onViewCreate(MainContract.View view,
                             ImageListItem.ListItemPageParams pageParams) {
        mView = view;
        mModel = new FlickrInterestingnessList();
        load(pageParams, true, true);
    }

    @Override
    public void onViewDestroy() {
        stopLoading(); // clean progressbar after rotation
        mView = null;
        mModel = null;
    }

    @Override
    public void onScrolledDown(ImageListItem.ListItemPageParams pageParams) {
        if(mView==null || pageParams==null || isLoadingNow)return;
        pageParams.moveParamsDown();
        load(pageParams, true, false);
    }

    @Override
    public void onScrolledUp(boolean firstLoad,
                             ImageListItem.ListItemPageParams pageParams) {
        if(mView==null || pageParams==null || isLoadingNow)return;
        pageParams.moveParamsUp();
        load(pageParams, false, firstLoad);
    }
}
