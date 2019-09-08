package com.greatsokol.fluckr;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;

class FluckrGridLayoutManager extends GridLayoutManager {

    FluckrGridLayoutManager(Context context, final FlickrImageListAdapter adapter, final int spanCount) {
        super(context, spanCount);
        setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case FlickrImageListItem.VIEW_TYPE_LOADING:
                        return spanCount;
                    case FlickrImageListItem.VIEW_TYPE_NORMAL:
                        return 1;
                }
                return 1;
            }
        });
    }
}
