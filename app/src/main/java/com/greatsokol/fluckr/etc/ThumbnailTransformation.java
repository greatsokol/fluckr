package com.greatsokol.fluckr.etc;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;

import com.squareup.picasso.Transformation;

public class ThumbnailTransformation implements Transformation {
    private int width, height;

    public ThumbnailTransformation(int width, int height){
        this.width = width;
        this.height = height;
    }
    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap result = ThumbnailUtils.extractThumbnail(source, width, height);
        if(source!=result)
            source.recycle();
        return result;
    }

    @Override
    public String key() {
        return "ThumbnailTransform()";
    }
}
