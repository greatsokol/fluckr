package com.greatsokol.fluckr.view;

import com.greatsokol.fluckr.ImageListItem;

import java.util.ArrayList;

public interface ViewMain {
    void onImageListDownloaded(ArrayList<ImageListItem> items);
    void onNetworkError();
    void onStartLoading();
}
