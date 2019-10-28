package com.greatsokol.fluckr.presenters;

import android.graphics.Bitmap;

import com.greatsokol.fluckr.contract.ViewContract;
import com.greatsokol.fluckr.models.ImageLoader;

public class ImageViewPresenter implements ViewContract.Presenter {
    private ViewContract.ViewView mView;

    @Override
    public void onViewCreate(ViewContract.ViewView view) {
        mView = view;
        String thumbnailUrl = view.getThumbnailUrl();
        ImageLoader loader = new ImageLoader();
        loader.loadImage(thumbnailUrl, view.getThumbnailSize(), new ViewContract.Model.onImageLoadedListener() {
            @Override
            public void onLoadSuccess(Bitmap image) {
                mView.onThumbLoaded(image);
            }

            @Override
            public void onLoadFailed(String message) {
                mView.onFailed(message);
            }
        });
    }

    @Override
    public void onViewDestroy() {
        mView = null;
    }
}
