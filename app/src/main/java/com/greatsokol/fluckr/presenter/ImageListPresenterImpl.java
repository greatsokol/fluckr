package com.greatsokol.fluckr.presenter;

import com.greatsokol.fluckr.view.ViewMain;

public class ImageListPresenterImpl implements ImageListPresenter {
    private ViewMain viewMain;
    private boolean isLoadingNow;

    @Override
    public void attachView(ViewMain view) {
        viewMain = view;
    }

    @Override
    public void detachView() {
        viewMain = null;
    }

    @Override
    public void loadUpperPage(String searchFor) {
        if(isLoadingNow)return;
        isLoadingNow = true;

    }

    @Override
    public void loadLowerPage(String searchFor) {
        if(isLoadingNow)return;
        isLoadingNow = true;
    }

    @Override
    public void loadInitialPage(String searchFor) {
        if(isLoadingNow)return;
        isLoadingNow = true;

    }
}
