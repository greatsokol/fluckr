package com.greatsokol.fluckr.presenter;

import com.greatsokol.fluckr.contract.ContractMain;
import com.greatsokol.fluckr.etc.ConstsAndUtils;
import com.greatsokol.fluckr.model.api.Photos;
import com.greatsokol.fluckr.view.ImageListItem;
import com.greatsokol.fluckr.model.FlickrInterestingnessListModel;

import java.util.Date;

public class ImageListPresenter implements ContractMain.ImageListPresenter {
    private ContractMain.ViewMain mView;
    private ContractMain.Model mModel;
    private boolean isLoadingNow;

    private void startLoading(boolean addProgressbarAtBottom){
        isLoadingNow = true;
        if(mView==null)return;
        mView.onStartLoading(addProgressbarAtBottom);
    }

    private void stopLoading(){
        isLoadingNow = false;
        if(mView==null)return;
        mView.onStopLoading();
    }

    @Override
    public void onViewCreate(ContractMain.ViewMain view, boolean loadInitial,
                             Date savedDate, int savedPage, int savedItemNumber) {
        mView = view;
        mModel = new FlickrInterestingnessListModel();
        if(loadInitial) {
            isLoadingNow = true;
            view.onStartLoading(true);
            final Date fdate = savedDate;
            mModel.loadPage(fdate, savedPage, mView.getSearchPhrase(), new ContractMain.Model.OnResponseCallback() {
                @Override
                public void onResponse(Photos photos) {
                    if(mView==null)return;
                    mView.onImageListDownloaded(Interactor.Translate(fdate, photos), true);
                    stopLoading();
                    onScrolledUp(); // load upper page if exists
                }

                @Override
                public void onFailure(String message) {
                    if(mView==null)return;
                    mView.onFailure(message);
                    stopLoading();
                }
            });
        }
    }

    @Override
    public void onViewDestroy() {
        stopLoading(); // clean progressbar after rotation
        mView = null;
        mModel = null;
    }

    @Override
    public void onScrolledDown() {
        if(mView==null)return;
        if(isLoadingNow)return;
        startLoading(true);

        ImageListItem.ListItemPageParams pageParams = mView.getLastItemPageParams();
        if(pageParams==null) {
            stopLoading();
            return;
        }
        int page = pageParams.getPage();
        Date date = pageParams.getDate();
        int totalPages = pageParams.getPagesTotal();
        if(page==-1 || date == null || totalPages==-1) {
            stopLoading();
            return;
        }

        page++;
        if(page > totalPages){
            date = ConstsAndUtils.DecDate(date);
            page = 1;
        }

        final Date fdate = date;
        mModel.loadPage(fdate, page, mView.getSearchPhrase(), new ContractMain.Model.OnResponseCallback() {
            @Override
            public void onResponse(Photos photos) {
                if(mView==null)return;
                mView.onImageListDownloaded(Interactor.Translate(fdate, photos), true);
                stopLoading();
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
    public void onScrolledUp() {
        if(mView==null)return;
        if(isLoadingNow)return;
        startLoading(false);
        ImageListItem.ListItemPageParams pageParams = mView.getFirstItemPageParams();
        if(pageParams==null) {
            stopLoading();
            return;
        }
        int page = pageParams.getPage();
        Date date = pageParams.getDate();
        if(page==-1 || date == null) {
            stopLoading();
            return;
        }

        page--;
        if(page<=0){
            date = ConstsAndUtils.IncDate(date);
            if(ConstsAndUtils.IsToday(date)) {
                stopLoading();
                return;
            }
            page=99999; // Max page will be returned;
        }

        final Date fdate = date;
        mModel.loadPage(fdate, page, mView.getSearchPhrase(), new ContractMain.Model.OnResponseCallback() {
            @Override
            public void onResponse(Photos photos) {
                if(mView==null)return;
                mView.onImageListDownloaded(Interactor.Translate(fdate, photos), false);
                stopLoading();
            }

            @Override
            public void onFailure(String message) {
                if(mView==null)return;
                mView.onFailure(message);
                stopLoading();
            }
        });
    }
}
