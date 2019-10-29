package com.greatsokol.fluckr.contracts;

import android.graphics.Bitmap;

public interface ViewContract {

    interface View {
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
        void attachView(View view);
        void loadThumbnail();
        void loadHighResolutionImage();
        void setTitle();
        void setDescription();
        void detachView();
    }

    interface Model{
        void loadThumbnail(String url, int thumbnailSize);
        void loadImage(String url);
        interface onImageLoadedListener{
            void onStartLoad();
            void onThumbnailLoaded(Bitmap image);
            void onHighResolutionImageLoaded(Bitmap image);
            void onLoadFailed(String message);
        }
    }

}
