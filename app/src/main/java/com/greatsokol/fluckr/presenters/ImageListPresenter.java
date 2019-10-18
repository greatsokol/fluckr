package com.greatsokol.fluckr.presenters;

import com.greatsokol.fluckr.contract.ContractMain;
import com.greatsokol.fluckr.etc.ConstsAndUtils;
import com.greatsokol.fluckr.models.api.Photos;
import com.greatsokol.fluckr.views.ImageListItem;
import com.greatsokol.fluckr.models.FlickrInterestingnessListModel;

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
    public void onViewCreate(ContractMain.ViewMain view, boolean firstLoad,
                             final Date date, final int page, final int itemNumber) {
        mView = view;
        mModel = new FlickrInterestingnessListModel();
        if(firstLoad) {
            isLoadingNow = true;
            view.onStartLoading(true);
            mModel.loadPage(date, page, mView.getSearchPhrase(), new ContractMain.Model.OnResponseCallback() {
                @Override
                public void onResponse(Photos photos) {
                    if(mView==null)return;
                    mView.onImageListDownloaded(
                            Interactor.Translate(date, photos),
                            true,
                            itemNumber);
                    stopLoading();
                    onScrolledUp(); //<---- load upper page if exists
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
        if(page==ConstsAndUtils.NO_PAGE || date == null || totalPages==ConstsAndUtils.NO_PAGE) {
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
                mView.onImageListDownloaded(
                        Interactor.Translate(fdate, photos),
                        true, ConstsAndUtils.NO_POSITION);
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
        if(page==ConstsAndUtils.NO_PAGE || date == null) {
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
                mView.onImageListDownloaded(
                        Interactor.Translate(fdate, photos),
                        false, ConstsAndUtils.NO_POSITION);
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
