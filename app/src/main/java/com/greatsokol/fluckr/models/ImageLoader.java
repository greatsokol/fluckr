package com.greatsokol.fluckr.models;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.greatsokol.fluckr.contract.ViewContract;
import com.greatsokol.fluckr.etc.ThumbnailTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ImageLoader implements ViewContract.Model {
    private Target mTarget; // hard link needed

    private void implLoadImage(String url, int thumbnailSize, Target target) {
        if(thumbnailSize>0)
            Picasso.get().load(url).transform(new ThumbnailTransformation(thumbnailSize, thumbnailSize)).into(target);
        else
            Picasso.get().load(url).into(target);
    }


    @Override
    public void loadThumbnail(String url, int thumbnailSize, final onImageLoadedListener listener) {
        mTarget = new Target() {
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
        implLoadImage(url, thumbnailSize, mTarget);
    }

    @Override
    public void loadImage(String url, final onImageLoadedListener listener) {
        mTarget = new Target() {
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
        implLoadImage(url, 0, mTarget);
    }
}
