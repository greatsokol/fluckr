package com.greatsokol.fluckr.contract;

import android.graphics.Bitmap;

public interface ViewContract {

    interface ViewView {

        void onThumbLoaded(Bitmap image);
        void onImageLoaded(Bitmap image);
        void onFailed(String message);
        int getThumbnailSize();
        String getThumbnailUrl();
        String getHighResolutionUrl();

    }

    interface Presenter {
        void onViewCreate(ViewView view);
        void onViewDestroy();
    }

    interface Model{
        void loadImage(String url, int thumbnailSize, final onImageLoadedListener listener);
        interface onImageLoadedListener{
            void onLoadSuccess(Bitmap image);
            void onLoadFailed(String message);
        }
    }

}
