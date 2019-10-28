package com.greatsokol.fluckr.contract;

import android.graphics.Bitmap;

public interface ViewContract {

    interface ViewView {
        void onThumbnailLoaded(Bitmap image);
        void onImageLoaded(Bitmap image);
        void onLoadFailed(String message);

        void showProgressBar();
        void hideProgressBar();

        int getThumbnailSize();
        String getThumbnailUrl();
        String getHighResolutionUrl();
        String getTitleText();
        String getDescriptionText();

        void setTitleText(String title);
        void setDescriptionText(String description);
    }

    interface Presenter {
        void attachView(ViewView view);
        void loadThumbnail();
        void loadHighResolutionImage();
        void setTitle();
        void setDescription();
        void detachView();
    }

    interface Model{
        void loadThumbnail(String url, int thumbnailSize, final onImageLoadedListener listener);
        void loadImage(String url, final onImageLoadedListener listener);
        interface onImageLoadedListener{
            void onStartLoad();
            void onThumbnailLoaded(Bitmap image);
            void onHighResolutionImageLoaded(Bitmap image);
            void onLoadFailed(String message);
        }
    }

}
