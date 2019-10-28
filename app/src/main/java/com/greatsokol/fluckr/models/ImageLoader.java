package com.greatsokol.fluckr.models;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.greatsokol.fluckr.contract.ViewContract;
import com.greatsokol.fluckr.etc.ThumbnailTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ImageLoader implements ViewContract.Model {
    @Override
    public void loadImage(String url, int thumbnailSize, final ViewContract.Model.onImageLoadedListener listener) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                listener.onLoadSuccess(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                listener.onLoadFailed(e.getMessage());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        if(thumbnailSize>0)
            Picasso.get().load(url).transform(new ThumbnailTransformation(thumbnailSize, thumbnailSize)).into(target);
        else
            Picasso.get().load(url).into(target);
    }
}
