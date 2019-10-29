package com.greatsokol.fluckr.presenters;

import com.greatsokol.fluckr.contracts.MainContract;
import com.greatsokol.fluckr.etc.ConstsAndUtils;
import com.greatsokol.fluckr.models.api.Photos;
import com.greatsokol.fluckr.views.ImageListItem;
import com.greatsokol.fluckr.models.FlickrInterestingnessListModel;

import java.util.Date;

public class ImageListPresenter implements MainContract.Presenter {
    private MainContract.MainView mView;
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

    @Override
    public void onViewCreate(MainContract.MainView view, final Date date, final int page, final int itemNumber) {
        mView = view;
        mModel = new FlickrInterestingnessListModel();

        isLoadingNow = true;
        view.onStartLoading();
        mModel.loadPage(date, page, mView.getSearchPhrase(), new MainContract.Model.OnResponseCallback() {
            @Override
            public void onResponse(Photos photos) {
                if(mView==null)return;
                mView.onImageListDownloaded(
                        Interactor.Translate(date, photos),true, itemNumber);
                stopLoading();
                onScrolledUp(); //<---- load upper page if exists
                onScrolledDown(); //<---- load lower page if exists
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
    public void onViewDestroy() {
        stopLoading(); // clean progressbar after rotation
        mView = null;
        mModel = null;
    }

    @Override
    public void onScrolledDown() {
        if(mView==null)return;
        if(isLoadingNow)return;
        ImageListItem.ListItemPageParams pageParams = mView.getLastItemPageParams();
        if(pageParams==null)return;
        int page = pageParams.getPage();
        Date date = pageParams.getDate();
        int totalPages = pageParams.getPagesTotal();
        if(page==ConstsAndUtils.NO_PAGE || date == null || totalPages==ConstsAndUtils.NO_PAGE)
            return;
        page++;
        if(page > totalPages){
            date = ConstsAndUtils.DecDate(date);
            page = 1;
        }
        startLoading();
        final Date fdate = date;
        mModel.loadPage(fdate, page, mView.getSearchPhrase(), new MainContract.Model.OnResponseCallback() {
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

        ImageListItem.ListItemPageParams pageParams = mView.getFirstItemPageParams();
        if(pageParams==null)
            return;
        int page = pageParams.getPage();
        Date date = pageParams.getDate();
        if(page==ConstsAndUtils.NO_PAGE || date == null)
            return;

        page--;
        if(page<=0){
            date = ConstsAndUtils.IncDate(date);
            if(ConstsAndUtils.IsToday(date))
                return;
            page=99999; // Max page will be returned;
        }

        startLoading();
        final Date fdate = date;
        mModel.loadPage(fdate, page, mView.getSearchPhrase(), new MainContract.Model.OnResponseCallback() {
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
