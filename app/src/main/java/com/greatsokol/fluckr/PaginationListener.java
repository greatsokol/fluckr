package com.greatsokol.fluckr;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public abstract class PaginationListener extends RecyclerView.OnScrollListener {

    @NonNull
    private GridLayoutManager layoutManager;
    PaginationListener(@NonNull GridLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        //boolean isloading = isLoading();
        //boolean islast = isLastPage();

        //if (!isloading && !islast) {
            if (dy > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    /*&& firstVisibleItemPosition >= 0
                    && totalItemCount >= FLICKR_PER_PAGE*/) {
                loadNextPage();
            } else if (dy < 0 && firstVisibleItemPosition == 0) {
                loadPrevPage();
            }
        //}
    }

    protected abstract void loadNextPage();

    protected abstract void loadPrevPage();

    //public abstract boolean isLastPage();

    //public abstract boolean isLoading();
}
