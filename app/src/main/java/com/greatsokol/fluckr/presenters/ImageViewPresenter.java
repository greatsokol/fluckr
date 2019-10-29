package com.greatsokol.fluckr.presenters;

import android.graphics.Bitmap;

import com.greatsokol.fluckr.contracts.ViewContract;
import com.greatsokol.fluckr.models.ImageLoader;

public class ImageViewPresenter implements ViewContract.Presenter, ViewContract.Model.onImageLoadedListener {
    private ViewContract.View mView;
    private ImageLoader mImageLoader;

    public ImageViewPresenter(){
        mImageLoader = new ImageLoader(this);
    }


    @Override
    public void attachView(ViewContract.View view) {
        mView = view;
    }

    @Override
    public void loadThumbnail() {
        if(mView != null)
            mImageLoader.loadThumbnail(mView.getThumbnailUrl(), mView.getThumbnailSize());
    }

    @Override
    public void loadHighResolutionImage() {
        if(mView != null)
            mImageLoader.loadImage(mView.getHighResolutionUrl());
    }

    @Override
    public void setTitle() {
        if(mView != null)
            mView.setTitleText(mView.getTitleText());
    }

    @Override
    public void setDescription() {
        if(mView != null)
            mView.setDescriptionText(mView.getDescriptionText());
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onStartLoad() {
        if(mView != null)
            mView.showProgressBar();
    }

    @Override
    public void onThumbnailLoaded(Bitmap image) {
        if(mView != null) {
            mView.hideProgressBar();
            mView.onThumbnailLoaded(image);
        }
    }

    @Override
    public void onHighResolutionImageLoaded(Bitmap image) {
        if(mView != null){
            mView.hideProgressBar();
            mView.onImageLoaded(image);
        }

    }

    @Override
    public void onLoadFailed(String message) {
        if(mView != null) {
            mView.hideProgressBar();
            mView.onLoadFailed(message);
        }
    }
}
