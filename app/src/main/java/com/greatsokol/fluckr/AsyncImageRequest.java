package com.greatsokol.fluckr;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class AsyncImageRequest extends AsyncTask<Void, Void, Bitmap> {



    public abstract static class OnAnswerListener{
        public abstract void OnAnswerReady(Bitmap bitmap);
    }

    private OnAnswerListener mListener;
    private String mUrl;
    private String mCacheDir;
    private int mResize;

    AsyncImageRequest(final AsyncImageRequest.OnAnswerListener listener,
                      final String url, final String cacheDir, int resize){
        mListener = listener;
        mUrl = url;
        mCacheDir = cacheDir;
        mResize = resize;
    }

    AsyncImageRequest(final AsyncImageRequest.OnAnswerListener listener,
                      final String url, final String cacheDir){
        mListener = listener;
        mUrl = url;
        mCacheDir = cacheDir;
        mResize = ImageLoader.NORESIZE;
    }


    @Override
    protected Bitmap doInBackground(Void... voids) {
        try {
            return ImageLoader.loadPicture(mUrl, mCacheDir, mResize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        mListener.OnAnswerReady(bitmap);
    }
}
