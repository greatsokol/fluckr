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

public class ImageListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<ImageListItem> mItems;
    private AsyncListRequest mFlickrRequest;
    private boolean mIsLoadingNow = false;
    private int mCurrentPage;
    private Date mCurrentDate;
    private boolean mViewAsGrid = true;

    boolean isLoadingNow(){return mIsLoadingNow; }
    void setViewAsGrid(boolean viewAsGrid){mViewAsGrid = viewAsGrid;}

    private int mTotalPage = 0; // обновится после LoadNextPicturesList
    private int mSpanCount = 3;
    boolean isLastPage(){return false;}//mCurrentPage>(mTotalPage-1);}
    private boolean __isLastPage(){ return mCurrentPage>(mTotalPage-1);}
    void setSpanCount(int spanCount){mSpanCount = spanCount;}


    private View.OnClickListener mItemClickListener;


    ImageListAdapter(ArrayList<ImageListItem> items) {
        mItems = items;
    }

    void setOnItemClickListener(View.OnClickListener listener){
        mItemClickListener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ImageListItem.VIEW_TYPE_LOADING)
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.listitem_loading, parent, false));
        else {

            return new ViewHolder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(mViewAsGrid?
                                    R.layout.listitem_grid :
                                    R.layout.listitem_linear,
                                    parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
        setFadeAnimation(holder.itemView);
    }

    private static int FADE_DURATION = 500;
    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    /*
    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(
                0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }*/

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
        removeObsoleteDates();
    }

    private synchronized void addItemsAtStart(List<ImageListItem> items) {
        mItems.addAll(0, items);
        int itemsSize = items.size();
        notifyItemRangeInserted(0, itemsSize);
        removeObsoleteDates();
    }

    private synchronized void startLoading(int page) {
        mIsLoadingNow = true;
        if(page >= mCurrentPage){
            mItems.add(new ImageListItem(ImageListItem.VIEW_TYPE_LOADING));
            notifyItemInserted(mItems.size() - 1);
        } else {
            mItems.add(0, new ImageListItem(ImageListItem.VIEW_TYPE_LOADING));
            notifyItemInserted(0);
        }
    }

    private void __removeArrayOfNumbers(ArrayList<Integer> list_to_remove){
        for(int i=0; i<list_to_remove.size(); i++){
            int num_to_remove = list_to_remove.get(i);
            if(mItems.size() > num_to_remove) {
                mItems.remove(num_to_remove);
                notifyItemRemoved(num_to_remove);
            }
        }
    }

    private synchronized void stopLoading() {
        ArrayList<Integer> list_to_remove = new ArrayList<>();
        for (int i = 0; i < mItems.size(); i++) {
            if (getItemViewType(i) == ImageListItem.VIEW_TYPE_LOADING) {
                list_to_remove.add(i);
            }
        }
        __removeArrayOfNumbers(list_to_remove);
        mIsLoadingNow = false;
    }

    private synchronized void removeObsoleteDates() {
        //Date dateNext = ConstsAndUtils.DecDate(mCurrentDate);
        Date datePrev = mCurrentDate; //ConstsAndUtils.IncDate(mCurrentDate);
        ArrayList<Integer> list_to_remove = new ArrayList<>();

        for(int i=0; i<mItems.size(); i++){
            ImageListItem item = mItems.get(i);
            if(item.getViewType()!=ImageListItem.VIEW_TYPE_LOADING) {
                Date itemDate = item.getDate();
                if (!ConstsAndUtils.IsEqualDay(itemDate, datePrev))
                    list_to_remove.add(i);
            }
        }

        //удаление неполного ряда в конце списка
        /*int c = list_to_remove.size() % mSpanCount;
        if(c > 0){
            int size = list_to_remove.size();
            list_to_remove.subList(size - c, size).clear();
        }

        for(int i=0; i<mItems.size(); i++){
            ImageListItem item = mItems.get(i);
            if(item.getViewType()!=ImageListItem.VIEW_TYPE_LOADING) {
                int itempage = item.getPageNumber();
                if (itempage > pageNext)
                    list_to_remove.add(i);
            }
        }*/
        __removeArrayOfNumbers(list_to_remove);
    }

    void clear() {
        if (isLoadingNow()) return;
        mItems.clear();
        mCurrentPage=0;
        notifyDataSetChanged();
    }

    private ImageListItem getItem(int position) {
        return mItems.isEmpty() ? null : mItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder implements View.OnTouchListener {
        TextView textViewTitle;
        TextView textViewDetails;
        ImageView imageView;
        int mItemPosition;


        ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textviewTitle);
            textViewDetails = itemView.findViewById(R.id.textviewDetails);
            imageView = itemView.findViewById(R.id.imageview);
            itemView.setOnTouchListener(this);
        }

        @Override
        protected void clear() {}

        @Override
        public void onBind(int position) {
            super.onBind(position);
            mItemPosition = position;
            ImageListItem listItem = getItem(position);
            assert listItem != null;
            if(textViewTitle!=null)
                textViewTitle.setText(listItem.getTitle());
            if(textViewDetails!=null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    textViewDetails.setText(Html.fromHtml(listItem.getDetails(),Html.FROM_HTML_MODE_LEGACY));
                } else {
                    textViewDetails.setText(Html.fromHtml(listItem.getDetails()));
                }
            }
            imageView.setImageBitmap(listItem.getBitmapThumbnail());
            final String transitionName = "itemImage" + "_" + getClass().getName() + "_" + position;
            ViewCompat.setTransitionName(imageView, transitionName);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_CANCEL)
            {
                view.setPressed(false);
            } else
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.setPressed(false);
                if(mItemClickListener!=null) {
                    ImageListItem listItem = getItem(mItemPosition);
                    assert listItem != null;
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


    void loadNavigationSettings(SharedPreferences prefs){
        mCurrentDate
         = new Date(prefs.getLong(ConstsAndUtils.TAG_DATE_TO_VIEW, ConstsAndUtils.DecDate(new Date()).getTime()));
        mCurrentPage = prefs.getInt(ConstsAndUtils.TAG_PAGE_TO_VIEW,0);
        mViewAsGrid = prefs.getBoolean(ConstsAndUtils.TAG_VIEWASGRID, true);
    }

    void saveNavigationSettings(SharedPreferences prefs){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(ConstsAndUtils.TAG_DATE_TO_VIEW, mCurrentDate.getTime());
        editor.putInt(ConstsAndUtils.TAG_PAGE_TO_VIEW, mCurrentPage);
        editor.putBoolean(ConstsAndUtils.TAG_VIEWASGRID, mViewAsGrid);
        editor.apply();
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
                            final int page){
        if (isLoadingNow() || (isLastPage() && getItemCount()>0)) return;

        mFlickrRequest = new AsyncListRequest(new AsyncListRequest.OnAnswerListener() {
                            @Override
                            public void OnStart() {
                                startLoading(page);
                            }

                            @Override
                            public void OnAnswerReady(ArrayList<ImageListItem> items) {
                                if(page >= mCurrentPage) addItemsAtBottom(items);
                                else addItemsAtStart(items);

                                stopLoading();
                                if (getItemCount()==0)
                                    _showSnack(viewToShowSnackbar, "No results");
                                else {
                                    mCurrentPage = page;
                                    mCurrentDate = date;
                                }
                            }

                            @Override
                            public void OnGetPagesNumber(int number) {
                                mTotalPage = number;
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


    void loadCurrentPage(View viewToShowSnackbar, String searchFor){
        __loadPage(viewToShowSnackbar, searchFor, mCurrentDate, mCurrentPage);
    }

    void loadNextPage(View viewToShowSnackbar, String searchFor){
        Date dt = mCurrentDate;
        int page = mCurrentPage+1;
        if(__isLastPage()){
            dt = ConstsAndUtils.DecDate(mCurrentDate);
            page = 1;
        }
        __loadPage(viewToShowSnackbar, searchFor, dt, page);
    }

    void loadPrevPage(View viewToShowSnackbar, String searchFor){
        Date dt = mCurrentDate;
        int page = mCurrentPage-1;
        if(page==0){
            dt = ConstsAndUtils.IncDate(dt);
            if(ConstsAndUtils.IsToday(dt)) return;
            page=16; // TODO - detect number of pages;
        }
        __loadPage(viewToShowSnackbar, searchFor, dt, page);
    }


    private void _showSnack(View view, String message){
        if (mFlickrRequest != null)
            if (!mFlickrRequest.isCancelled())
                    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}
