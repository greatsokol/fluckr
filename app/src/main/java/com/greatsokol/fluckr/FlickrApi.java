package com.greatsokol.fluckr;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

class FlickrApi {
    private static final String API_KEY = "dcfa7bcdfe436387cefa172c2d3dc2ae";
    static final int FLICKR_PER_PAGE = 30;


    static void LoadNextPicturesList(final View viewToShowSnackbar, final FlickrImageListAdapter adapter){
        if (adapter.isLastPage() || adapter.isLoadingNow()) return;
        final WeakReference<View> refViewToShowSnackbar = new WeakReference<>(viewToShowSnackbar);
        adapter.startLoading();
        final int page_number = adapter.getCurrentPage();

        AsyncFlickrApi flickr = new AsyncFlickrApi(new AsyncFlickrApi.OnAnswerListener() {
                    @Override
                    public void OnAnswerReady(ArrayList<FlickrImageListItem> items) {
                        adapter.addItems(items);
                        adapter.setCurrentPage(page_number + 1);
                        adapter.stopLoading();
                    }

                    @Override
                    public void OnGetPagesNumber(int number) {
                        adapter.setTotalPage(number);
                    }

                    @Override
                    public void OnError() {
                        if (refViewToShowSnackbar.get() != null)
                            Snackbar.make(refViewToShowSnackbar.get(),
                                    "Network error",
                                    Snackbar.LENGTH_LONG).show();
                        adapter.stopLoading();
                    }
                },
                API_KEY,
                FLICKR_PER_PAGE,
                page_number,
                viewToShowSnackbar.getContext().getCacheDir().getAbsolutePath());
        flickr.ExecuteRequest();
    }

}
