package com.greatsokol.fluckr.presenter;

import com.greatsokol.fluckr.view.ViewMain;

interface ImageListPresenter {
    void attachView(ViewMain view);
    void detachView();
    void loadUpperPage(String searchFor);
    void loadLowerPage(String searchFor);
    void loadInitialPage(String searchFor);
}
