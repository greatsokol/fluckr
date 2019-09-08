package com.greatsokol.fluckr;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;

public class FluckrGridLayoutManager extends GridLayoutManager {

    public FluckrGridLayoutManager(Context context, final FlickrImageListAdapter adapter, final int spanCount) {
        super(context, spanCount);
        setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case FlickrImageListAdapter.VIEW_TYPE_LOADING:
                        return spanCount;
                    case FlickrImageListAdapter.VIEW_TYPE_NORMAL:
                        return 1;
                }
                return 1;
            }
        });
    }
}
