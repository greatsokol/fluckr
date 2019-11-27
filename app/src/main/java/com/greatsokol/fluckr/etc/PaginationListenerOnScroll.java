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

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();

        if (dy > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
            loadNextPage();
        } else if (dy < 0 && firstVisibleItemPosition == 0) {
            loadPrevPage();
        }

        if(mFirstVisibleItemPostionSaved != firstVisibleItemPosition){
            mFirstVisibleItemPostionSaved = firstVisibleItemPosition;
            onScrolled(firstVisibleItemPosition);
        }
    }

    protected abstract void onScrolled(int firstVisibleItemPosition);
    protected abstract void loadNextPage();
    protected abstract void loadPrevPage();
}
