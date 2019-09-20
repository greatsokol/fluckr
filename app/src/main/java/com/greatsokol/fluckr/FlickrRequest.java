package com.greatsokol.fluckr;

import android.view.View;
import com.google.android.material.snackbar.Snackbar;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

class FlickrRequest {
    private static final String API_KEY = "dcfa7bcdfe436387cefa172c2d3dc2ae";
    private static final int FLICKR_PER_PAGE = 30;
    private AsyncFlickrInterestingListRequest mFlickrRequest;

    void prepareLoadNextPicturesListRequest(final View viewToShowSnackbar, final FlickrImageListAdapter adapter, String searchFor){
        if (adapter.isLoadingNow() || adapter.isLastPage())
            return;
        final WeakReference<View> refViewToShowSnackbar = new WeakReference<>(viewToShowSnackbar);
        adapter.startLoading();
        final int page_number = adapter.getCurrentPage();

        mFlickrRequest = new AsyncFlickrInterestingListRequest(new AsyncFlickrInterestingListRequest.OnAnswerListener() {
                    @Override
                    public void OnAnswerReady(ArrayList<FlickrImageListItem> items) {
                        adapter.addItems(items);
                        adapter.stopLoading();
                        if (adapter.getItemCount()==0)
                            _showSnack(refViewToShowSnackbar, "No results");
                        else
                            adapter.setCurrentPage(page_number + 1);
                    }

                    @Override
                    public void OnGetPagesNumber(int number) {
                        adapter.setTotalPage(number);
                    }

                    @Override
                    public void OnError() {
                        adapter.stopLoading();
                        _showSnack(refViewToShowSnackbar, "Network error");
                    }
                },
                searchFor,
                API_KEY,
                FLICKR_PER_PAGE,
                page_number,
                viewToShowSnackbar.getContext().getCacheDir().getAbsolutePath());
    }

    void Execute(){
        if(mFlickrRequest!=null)
            mFlickrRequest.execute();
    }

    void Stop(){
        if(mFlickrRequest!=null)
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
