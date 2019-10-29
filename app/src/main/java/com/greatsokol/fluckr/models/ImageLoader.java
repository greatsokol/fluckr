package com.greatsokol.fluckr.models;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.greatsokol.fluckr.contracts.ViewContract;
import com.greatsokol.fluckr.etc.ThumbnailTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ImageLoader implements ViewContract.Model {
    private Target mThumbnailTarget; // hard reference needed
    private Target mHighResolutionTarget;

    public ImageLoader(final onImageLoadedListener listener){
        mThumbnailTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                listener.onThumbnailLoaded(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                listener.onLoadFailed(e.getMessage());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                listener.onStartLoad();
            }
        };

        mHighResolutionTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                listener.onHighResolutionImageLoaded(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                listener.onLoadFailed(e.getMessage());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                listener.onStartLoad();
            }
        };
    }

    private void implLoadImage(String url, int thumbnailSize, Target target) {
        if(thumbnailSize>0)
            Picasso.get().
                    load(url).
                    transform(new ThumbnailTransformation(thumbnailSize, thumbnailSize)).
                    into(target);
        else
            Picasso.get().
                    load(url).
                    into(target);
    }


    @Override
    public void loadThumbnail(String url, int thumbnailSize) {
        implLoadImage(url, thumbnailSize, mThumbnailTarget);
    }

    @Override
    public void loadImage(String url) {
        implLoadImage(url, 0, mHighResolutionTarget);
    }
}
