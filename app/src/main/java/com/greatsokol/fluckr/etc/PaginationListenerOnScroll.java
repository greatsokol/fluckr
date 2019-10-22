package com.greatsokol.fluckr.etc;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PaginationListenerOnScroll extends RecyclerView.OnScrollListener {
    @NonNull
    private GridLayoutManager layoutManager;
    protected PaginationListenerOnScroll(@NonNull GridLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    private int mFirstVisibleItemPostionSaved;
    private int mLastVisibleItemPostionSaved;

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

        if (dy > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
            loadNextPage();
        } else if (dy < 0 && firstVisibleItemPosition == 0) {
            loadPrevPage();
        }

        if(mFirstVisibleItemPostionSaved != firstVisibleItemPosition &&
            mLastVisibleItemPostionSaved != lastVisibleItemPosition){
            mFirstVisibleItemPostionSaved = firstVisibleItemPosition;
            mLastVisibleItemPostionSaved = lastVisibleItemPosition;
            onScrolled(lastVisibleItemPosition);
        }
    }

    protected abstract void onScrolled(int firstVisibleItemPosition);
    protected abstract void loadNextPage();
    protected abstract void loadPrevPage();
}
