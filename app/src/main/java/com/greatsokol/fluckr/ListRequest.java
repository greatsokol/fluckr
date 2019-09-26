package com.greatsokol.fluckr;

import android.view.View;
import com.google.android.material.snackbar.Snackbar;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

class ListRequest {
    private static final String API_KEY = "dcfa7bcdfe436387cefa172c2d3dc2ae";
    private static final int FLICKR_PER_PAGE = 24;
    private AsyncListRequest mFlickrRequest;
    private ImageListAdapter mAdapter;

    void prepareLoadNextPicturesListRequest(final View viewToShowSnackbar, final ImageListAdapter adapter, String searchFor){
        if (adapter.isLoadingNow() || (adapter.isLastPage() && adapter.getItemCount()>0))
            return;

        final WeakReference<View> refViewToShowSnackbar = new WeakReference<>(viewToShowSnackbar);
        mAdapter = adapter;
        final int page_number = mAdapter.getCurrentPage();

        mFlickrRequest = new AsyncListRequest(new AsyncListRequest.OnAnswerListener() {
                    @Override
                    public void OnAnswerReady(ArrayList<ImageListItem> items) {
                        mAdapter.addItems(items);
                        mAdapter.stopLoading();
                        if (mAdapter.getItemCount()==0)
                            _showSnack(refViewToShowSnackbar, "No results");
                        else
                            mAdapter.setCurrentPage(page_number + 1);
                    }

                    @Override
                    public void OnGetPagesNumber(int number) {
                        mAdapter.setTotalPage(number);
                    }

                    @Override
                    public void OnError() {
                        mAdapter.stopLoading();
                        _showSnack(refViewToShowSnackbar, "Network error");
                    }
                },
                searchFor,
                API_KEY,
                FLICKR_PER_PAGE,
                page_number+1,
                viewToShowSnackbar.getContext().getCacheDir().getAbsolutePath());
    }

    void Execute(){
        if(mFlickrRequest != null) {
            mAdapter.startLoading();
            mFlickrRequest.execute();
        }
    }

    void Stop(){
        if(mFlickrRequest != null)
            mFlickrRequest.cancel(true);
    }

    private void _showSnack(WeakReference<View> refParentView, String message){
        if (mFlickrRequest != null)
            if (!mFlickrRequest.isCancelled())
                if (refParentView.get() != null)
                    Snackbar.make(refParentView.get(),
                            message, Snackbar.LENGTH_LONG).show();
    }
}
