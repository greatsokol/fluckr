package com.greatsokol.fluckr.etc;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greatsokol.fluckr.views.ImageListAdapter;
import com.greatsokol.fluckr.views.ImageListItem;

public class ImageGridLayoutManager extends GridLayoutManager {

    public ImageGridLayoutManager(Context context, final ImageListAdapter adapter, final int spanCount) {
        super(context, spanCount);
        setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case ImageListItem.VIEW_TYPE_DATE:
                        return spanCount; //all width of line
                }
                return 1;
            }
        });
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e("TAG", "meet a IOOBE in RecyclerView");
        }
    }
}
