package com.greatsokol.fluckr.presenter;

import android.view.View;

import com.greatsokol.fluckr.contract.ContractMain;
import com.greatsokol.fluckr.etc.ConstsAndUtils;
import com.greatsokol.fluckr.model.api.Photos;
import com.greatsokol.fluckr.view.ImageListAdapter;
import com.greatsokol.fluckr.view.ImageListItem;
import com.greatsokol.fluckr.model.FlickrInterestingnessListModel;

import java.util.ArrayList;
import java.util.Date;

public class ImageListPresenter implements ContractMain.ImageListPresenter, View.OnClickListener {
    private ImageListAdapter mAdapter;
    private ImageListAdapter mSearchAdapter;

    private ContractMain.ViewMain mView;
    private ContractMain.Model mModel;
    private boolean isLoadingNow;
    private String mSearchFor = "";

    public ImageListAdapter getAdapter(){ return
            mSearchFor == null ||
                    mSearchFor.equals("") ? mAdapter : mSearchAdapter;}


    private void startLoading(boolean addProgressbarAtBottom){
        isLoadingNow = true;
        if(mView==null)return;
        getAdapter().startLoading(addProgressbarAtBottom);
    }

    private void stopLoading(){
        isLoadingNow = false;
        if(mView==null)return;
        getAdapter().stopLoading();
    }

    @Override
    public void onViewCreate(ContractMain.ViewMain view,
                             final Date date, final int page, final int itemNumber) {

        mAdapter = new ImageListAdapter(new ArrayList<ImageListItem>());
        mSearchAdapter = new ImageListAdapter(new ArrayList<ImageListItem>());
        mAdapter.setOnItemClickListener(this);
        mSearchAdapter.setOnItemClickListener(this);

        mView = view;
        mModel = new FlickrInterestingnessListModel();

            startLoading(true);
            mModel.loadPage(date, page, mSearchFor, new ContractMain.Model.OnResponseCallback() {
                @Override
                public void onResponse(Photos photos) {
                    if(mView==null)return;
                    onImageListDownloaded(
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

    private void onImageListDownloaded(ArrayList<ImageListItem> items, boolean addAtBottom, int restorePosition) {
        if(addAtBottom)
            getAdapter().addItemsAtBottom(items, restorePosition);
        else
            getAdapter().addItemsUpper(items);
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

        ImageListItem.ListItemPageParams pageParams = getAdapter().getLastItemPageParams();
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
        mModel.loadPage(fdate, page, mSearchFor, new ContractMain.Model.OnResponseCallback() {
            @Override
            public void onResponse(Photos photos) {
                if(mView==null)return;
                onImageListDownloaded(
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
        ImageListItem.ListItemPageParams pageParams = getAdapter().getFirstItemPageParams();
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
        mModel.loadPage(fdate, page, mSearchFor, new ContractMain.Model.OnResponseCallback() {
            @Override
            public void onResponse(Photos photos) {
                onImageListDownloaded(
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


    @Override
    public void setSearchPhrase(String searchPhrase) {
        mSearchFor = searchPhrase;
    }


    @Override
    public void onClick(View v) {
        if(mView != null)
            mView.onItemClick(v);
    }
}
