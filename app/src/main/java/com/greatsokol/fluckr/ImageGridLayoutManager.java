package com.greatsokol.fluckr;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;

class ImageGridLayoutManager extends GridLayoutManager {

    ImageGridLayoutManager(Context context, final ImageListAdapter adapter, final int spanCount) {
        super(context, spanCount);
        setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case ImageListItem.VIEW_TYPE_DATE:
                    case ImageListItem.VIEW_TYPE_LOADING:
                        return spanCount; //all width of line
                }
                return 1;
            }
        });
    }
}
