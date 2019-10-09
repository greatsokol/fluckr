package com.greatsokol.fluckr.etc;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public abstract class PaginationListenerOnFling extends RecyclerView.OnFlingListener {
    @NonNull
    private GridLayoutManager layoutManager;
    public PaginationListenerOnFling(@NonNull GridLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public boolean onFling(int velocityX, int velocityY){
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        if (velocityY > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
            loadNextPage();
        } else if (velocityY < 0 && firstVisibleItemPosition == 0) {
            loadPrevPage();
        }
        return false;
    }

    protected abstract void loadNextPage();
    protected abstract void loadPrevPage();

}
