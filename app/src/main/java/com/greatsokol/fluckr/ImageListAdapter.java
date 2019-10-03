package com.greatsokol.fluckr;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ImageListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<ImageListItem> mItems;
    private AsyncListRequest mFlickrRequest;
    //private RecyclerView mRecyclerView;
    private boolean mIsLoadingNow = false;
    private boolean mViewAsGrid = true;

    void setViewAsGrid(boolean viewAsGrid){mViewAsGrid = viewAsGrid;}

    private int mSpanCount = 3;
    void setSpanCount(int spanCount){mSpanCount = spanCount;}


    private View.OnClickListener mItemClickListener;


    ImageListAdapter(ArrayList<ImageListItem> items) {
        mItems = items;
    }

    void setOnItemClickListener(View.OnClickListener listener){
        mItemClickListener = listener;
    }

    /*@Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    } */

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ImageListItem.VIEW_TYPE_LOADING) {
            return new ProgressHolder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.listitem_loading, parent, false));
        }
        else if(viewType == ImageListItem.VIEW_TYPE_IMAGE){
            return new ViewHolder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(mViewAsGrid?
                                    R.layout.listitem_grid :
                                    R.layout.listitem_linear,
                                    parent, false));
        }
        else if(viewType == ImageListItem.VIEW_TYPE_DATE){
            return new ViewHolder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.listitem_grouptitle, parent, false));
        }
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.listitem_placeholder, parent, false));
    }

    @Override
    synchronized public int getItemViewType(int position) {
        ImageListItem item = getItem(position);
        return item != null  ?
                item.getViewType() :
                ImageListItem.VIEW_TYPE_UNKNOWN;
    }

    @Override
    synchronized public int getItemCount() {
        return mItems==null? 0 : mItems.size();
    }


    private synchronized void addItemsAtBottom(List<ImageListItem> items) {
        mItems.addAll(items);
        int itemsSize = items.size();
        int positionStart = mItems.size() - itemsSize;
        notifyItemRangeInserted(positionStart, itemsSize);
    }

    private synchronized void addItemsAtStart(List<ImageListItem> items) {
        __removeItemsOfType(ImageListItem.VIEW_TYPE_PLACEHOLDER);

        ImageListItem firstItem = items.get(0);
        int index = firstItem.getViewType()==ImageListItem.VIEW_TYPE_DATE ? 1 : 0;

        int c = items.size() - index /*- removed*/;
        if(c>0) {
            c = c % mSpanCount;
            if (c != 0) {
                c = mSpanCount - c;
                for (int i = 0; i < c; i++) {
                    items.add(
                            new ImageListItem( // empty placeholder
                                    ImageListItem.VIEW_TYPE_PLACEHOLDER,
                                    firstItem.getDate(),
                                    firstItem.getPagesTotal(),
                                    firstItem.getPage()));
                }
            }
        }

        mItems.addAll(0, items);
        notifyItemRangeInserted(0, items.size());
    }

    private synchronized void startLoading(boolean bAddProgressbarAtBottom) {
        mIsLoadingNow = true;
        if(bAddProgressbarAtBottom){
            mItems.add(new ImageListItem(ImageListItem.VIEW_TYPE_LOADING));
            notifyItemInserted(mItems.size() - 1);
        } else {
            mItems.add(0, new ImageListItem(ImageListItem.VIEW_TYPE_LOADING));
            notifyItemInserted(0);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
        setFadeAnimation(holder.itemView);
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }

    private void __removeArrayOfNumbers(ArrayList<Integer> list_to_remove, boolean bNotify){
        for(int i=0; i<list_to_remove.size(); i++){
            int num_to_remove = list_to_remove.get(i);
            if(num_to_remove < mItems.size()) {
                mItems.remove(num_to_remove);
                if(bNotify)
                    notifyItemRemoved(num_to_remove);
            }
        }
    }

    private synchronized void __removeItemsOfType(int itemType){
        ArrayList<Integer> list_to_remove = new ArrayList<>();
        for (int i = 0; i < mItems.size(); i++) {
            if (getItemViewType(i) == itemType) {
                mItems.remove(i);
                notifyItemRemoved(i);
                i--;
            }
        }
    }

    private synchronized void stopLoading() {
        __removeItemsOfType(ImageListItem.VIEW_TYPE_LOADING);
        mIsLoadingNow = false;
    }

    void clear() {
        if(mIsLoadingNow)return;
        mItems.clear();
        notifyDataSetChanged();
    }

    private ImageListItem getItem(int position) {
        return mItems.isEmpty() ? null : mItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder implements View.OnTouchListener {
        TextView textViewDate;
        TextView textViewTitle;
        TextView textViewDetails;
        ImageView imageView;
        int mItemPosition;
        ImageListItem listItem;

        ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textviewTitle);
            textViewDetails = itemView.findViewById(R.id.textviewDetails);
            imageView = itemView.findViewById(R.id.imageview);
            textViewDate = itemView.findViewById(R.id.textviewDate);
            itemView.setOnTouchListener(this);
        }

        @Override
        protected void clear() {}

        @Override
        public void onBind(int position) {
            super.onBind(position);
            mItemPosition = position;
            listItem = getItem(position);
            assert listItem!=null;
            if(textViewTitle!=null)
                textViewTitle.setText(listItem.getTitle());
            if(textViewDetails!=null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    textViewDetails.setText(Html.fromHtml(listItem.getDetails(),Html.FROM_HTML_MODE_LEGACY));
                } else {
                    textViewDetails.setText(Html.fromHtml(listItem.getDetails()));
                }
            }
            if(imageView!=null){
                imageView.setImageBitmap(listItem.getBitmapThumbnail());
                final String transitionName = "itemImage" + "_" + getClass().getName() + "_" + position;
                ViewCompat.setTransitionName(imageView, transitionName);
            }
            if(textViewDate!=null) {
                textViewDate.setText(ConstsAndUtils.DateToStr_dd_mmmm_yyyy(listItem.getDate()));
            }
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_CANCEL ||
                    listItem.getViewType()!=ImageListItem.VIEW_TYPE_IMAGE)
            {
                view.setPressed(false);
            } else
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.setPressed(false);
                if(mItemClickListener!=null) {
                    Bundle args = new Bundle();
                    args.putInt(ConstsAndUtils.TAG_TR_POSITION, mItemPosition);
                    args.putString(ConstsAndUtils.TAG_TITLE, listItem.getTitle());
                    args.putString(ConstsAndUtils.TAG_DETAILS, listItem.getDetails());
                    args.putString(ConstsAndUtils.TAG_THUMBURL, listItem.getThumbnailUrl());
                    args.putString(ConstsAndUtils.TAG_FULLSIZEURL, listItem.getFullsizeUrl());
                    view.setTag(args);
                    view.performClick();
                    mItemClickListener.onClick(view);
                }
            } else view.setPressed(true);
            return false;
        }
    }

    public class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void clear() {

        }
    }


    void saveNavigationSettings(SharedPreferences prefs, int firstVisibleItemPosition){
        ImageListItem item = mItems.get(firstVisibleItemPosition);
        if(item!=null) {
            Date dt = item.getDate();
            if(dt != null) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(ConstsAndUtils.TAG_DATE_TO_VIEW, dt.getTime());
                editor.putInt(ConstsAndUtils.TAG_PAGE_TO_VIEW, item.getPage());
                editor.putInt(ConstsAndUtils.TAG_NUMBER_ON_PAGE, item.getNumberOnPage());
                editor.apply();
            }
        }
    }

    void stopLoadingRequest(boolean clear){
        if(mFlickrRequest != null)
            mFlickrRequest.cancel(true);
        stopLoading();
        if(clear)clear();
    }

    private void __loadPage(final View viewToShowSnackbar,
                            final String searchFor,
                            final Date date,
                            final int page,
                            final int numberOnPage,
                            final boolean bAddDateHeader,
                            final boolean bAddItemsAtBottom,
                            final boolean bLoadPrevPageAfterFinish){
        if (mIsLoadingNow) return; // || (isLastPage() && getItemCount()>0)) return;
        //mIsLoadingNow = true;
        startLoading(bAddItemsAtBottom);
        mFlickrRequest = new AsyncListRequest(new AsyncListRequest.OnAnswerListener() {
                            @Override
                            public void OnStart() {
                                //startLoading(bAddItemsAtBottom);
                            }

                            @Override
                            public void OnAnswerReady(ArrayList<ImageListItem> items) {
                                if(bAddDateHeader){
                                    items.add(0,new ImageListItem(date, page));
                                }
                                if(bAddItemsAtBottom) addItemsAtBottom(items);
                                else addItemsAtStart(items);

                                if (getItemCount()==0)
                                    _showSnack(viewToShowSnackbar, "No results");
                                stopLoading();

                                // load previous page when run app with empty
                                // list at some previous date and page:
                                if(bLoadPrevPageAfterFinish)
                                    loadPrevPage(viewToShowSnackbar, searchFor);
                            }

                            @Override
                            public void OnError() {
                                stopLoading();
                                _showSnack(viewToShowSnackbar, "Network error");
                            }
                        },
                date,
                page,
                viewToShowSnackbar.getContext().getCacheDir().getAbsolutePath(),
                searchFor);
        mFlickrRequest.execute();
    }

    void loadInitialPage(SharedPreferences prefs, View viewToShowSnackbar, String searchFor){
        if(mIsLoadingNow)return;
        Date savedCurrentPageDate
                = new Date(prefs.getLong(ConstsAndUtils.TAG_DATE_TO_VIEW, ConstsAndUtils.DecDate(new Date()).getTime()));
        int savedCurrentPageNumber = prefs.getInt(ConstsAndUtils.TAG_PAGE_TO_VIEW,1);
        int savedCurentItemNumberOnPage = prefs.getInt(ConstsAndUtils.TAG_NUMBER_ON_PAGE,1);

        __loadPage(viewToShowSnackbar, searchFor,
                    savedCurrentPageDate,
                    savedCurrentPageNumber,
                    savedCurentItemNumberOnPage,
                savedCurrentPageNumber ==1,
                true,
                true);
    }

    void loadNextPage(View viewToShowSnackbar, String searchFor){
        if(mIsLoadingNow)return;
        ImageListItem item = mItems.get(mItems.size()-1); // last item of list
        Date dt = item.getDate();
        int page = item.getPage() + 1;
        int pagesTotal = item.getPagesTotal();
        if(page > pagesTotal){
            dt = ConstsAndUtils.DecDate(dt);
            page = 1;
        }
        __loadPage(viewToShowSnackbar, searchFor, dt, page, -1,
                page==1,
                true,
                false);
    }

    void loadPrevPage(View viewToShowSnackbar, String searchFor){
        if(mIsLoadingNow)return;
        ImageListItem item = mItems.get(0); // first item of list
        Date dt = item.getDate();
        int page = item.getPage()-1;
        if(page<=0){
            dt = ConstsAndUtils.IncDate(dt);
            if(ConstsAndUtils.IsToday(dt)) return;
            page=16; // TODO - detect number of pages;
        }
        __loadPage(viewToShowSnackbar, searchFor, dt, page, -1,
                page==1,
                false,
                false);
    }


    private void _showSnack(View view, String message){
        if (mFlickrRequest != null)
            if (!mFlickrRequest.isCancelled())
                    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}
