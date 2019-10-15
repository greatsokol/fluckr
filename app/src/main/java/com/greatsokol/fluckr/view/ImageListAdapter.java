package com.greatsokol.fluckr.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

import com.greatsokol.fluckr.R;
import com.greatsokol.fluckr.etc.ConstsAndUtils;
import com.greatsokol.fluckr.etc.ThumbnailTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.BaseViewHolder> {

    private List<ImageListItem> mItems;
    //private RecyclerView mRecyclerView;
    private boolean mViewAsGrid = true;

    void setViewAsGrid(boolean viewAsGrid){mViewAsGrid = viewAsGrid;}

    private int mSpanCount = 3;
    void setSpanCount(int spanCount){mSpanCount = spanCount;}


    private View.OnClickListener mItemClickListener;


    public ImageListAdapter(ArrayList<ImageListItem> items) {
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
    }*/

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


    void addItemsAtBottom(List<ImageListItem> items, int restorePosition) {
        mItems.addAll(items);
        int itemsSize = items.size();
        int positionStart = mItems.size() - itemsSize;
        notifyItemRangeInserted(positionStart, itemsSize);
        if(restorePosition != ConstsAndUtils.NO_POSITION){

        }
    }

    void addItemsUpper(List<ImageListItem> items) {
        if(items.size()==0) return;

        int removed = __removeItemsOfType(ImageListItem.VIEW_TYPE_PLACEHOLDER, false, false);
        ImageListItem firstItem = items.get(0);
        int index = firstItem.getViewType()==ImageListItem.VIEW_TYPE_DATE ? 1 : 0;

        int c = items.size() - index - removed;
        if(c>0) {
            c = c % mSpanCount;
            if (c != 0) {
                c = mSpanCount - c;
                for (int i = 0; i < c; i++) {
                    items.add(index,
                            new ImageListItem( // empty placeholder
                                    ImageListItem.VIEW_TYPE_PLACEHOLDER,
                                    firstItem.getPageParams().getDate(),
                                    firstItem.getPageParams().getPagesTotal(),
                                    firstItem.getPageParams().getPage()));
                }
            }
        }

        mItems.addAll(0, items);
        if(removed>0)notifyItemRangeChanged(items.size()-removed, removed);
        notifyItemRangeInserted(0, items.size()-removed);
    }

    /*private synchronized void RemoveObsoletePagesUpper(Date JustLoadedDate, int JustLoadedPage){
        if(mItems.size() == 0) return;
        int HeadersToRemove = 0;
        int ImagesToRemove = 0;
        for (int i = 0; i < mItems.size(); i++) {
            int ItemType = getItemViewType(i);
            if(ItemType != ImageListItem.VIEW_TYPE_LOADING) {
                ImageListItem item = mItems.get(i);
                int ItemPage = item.getPage();
                Date ItemDate = item.getDate();
                if(ItemDate == null) continue;
                int Days = JustLoadedDate.compareTo(ItemDate);
                if (ItemPage < JustLoadedPage - 1 || Days > 0){
                    if(ItemType == ImageListItem.VIEW_TYPE_PLACEHOLDER || ItemType == ImageListItem.VIEW_TYPE_IMAGE)ImagesToRemove++;
                    if(ItemType == ImageListItem.VIEW_TYPE_DATE)HeadersToRemove++;
                }
                else break;
            }
        }

        ImagesToRemove = ImagesToRemove - ImagesToRemove % mSpanCount;
        int TotalToRemove = ImagesToRemove + HeadersToRemove;
        for (int i = 0; i < TotalToRemove; i++) {
            mItems.remove(0);
            notifyItemRemoved(0);
        }
    }

    private synchronized void RemoveObsoletePagesAtBottom(Date JustLoadedDate, int JustLoadedPage){
        for (int i = 0; i < mItems.size(); i++) {
            ImageListItem item = mItems.get(i);
            int itemPage = item.getPage();
            Date ItemDate = item.getDate();
            if(ItemDate == null) continue;
            int Days = JustLoadedDate.compareTo(ItemDate);
            if (itemPage > JustLoadedPage + 1 || Days < 0) {
                mItems.remove(i);
                notifyItemRemoved(i);
                i--;
            }
        }
    }*/


    private void __notifyItemInserted(final int position){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                notifyItemInserted(position);
            }
        });
    }

    void startLoading(boolean addProgressbarAtBottom) {
        if(addProgressbarAtBottom){
            mItems.add(new ImageListItem(ImageListItem.VIEW_TYPE_LOADING));
            __notifyItemInserted(mItems.size() - 1);
        } else {
            mItems.add(0, new ImageListItem(ImageListItem.VIEW_TYPE_LOADING));
            __notifyItemInserted(0);
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

    private synchronized int __removeItemsOfType(int itemType, boolean notify, boolean all){
        int removed = 0;
        for (int i = 0; i < mItems.size(); i++) {
            int type = getItemViewType(i);
            if (type == itemType) {
                mItems.remove(i);
                if(notify)notifyItemRemoved(i);
                i--;
                removed++;
            } else if(!all && type != ImageListItem.VIEW_TYPE_LOADING) break;
        }
        return removed;
    }

    void stopLoading() {
        __removeItemsOfType(ImageListItem.VIEW_TYPE_LOADING, true, true);
    }

    void clear() {
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
                //Picasso.get().setIndicatorsEnabled(true);
                Picasso.get().
                        load(listItem.getThumbnailUrl()).
                        transform(new ThumbnailTransformation(150, 150)).
                        into(imageView);
                final String transitionName = "itemImage" + "_" + getClass().getName() + "_" + position;
                ViewCompat.setTransitionName(imageView, transitionName);
            }
            if(textViewDate!=null) {
                Date date = listItem.getPageParams().getDate();
                if(date!=null)
                    textViewDate.setText(ConstsAndUtils.DateToStr_dd_mmmm_yyyy(date));
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


    private ImageListItem getFirstImageItem(){
        for(int i=0; i<mItems.size(); i++){
            ImageListItem item = mItems.get(i);
            if(item.getViewType() != ImageListItem.VIEW_TYPE_LOADING)
                return item;
        }
        return null;
    }

    private ImageListItem getLastImageItem(){
        for(int i=mItems.size()-1; i>=0; i--){
            ImageListItem item = mItems.get(i);
            if(item.getViewType() != ImageListItem.VIEW_TYPE_LOADING)
                return item;
        }
        return null;
    }



    ImageListItem.ListItemPageParams getLastItemPageParams(){
        ImageListItem item = getLastImageItem();
        if(item == null) return null;
        return item.getPageParams();
    }

    ImageListItem.ListItemPageParams getFirstItemPageParams(){
        ImageListItem item = getFirstImageItem();
        if(item == null) return null;
        return item.getPageParams();
    }


    String saveNavigationSettings(SharedPreferences prefs, int firstVisibleItemPosition){
        if(firstVisibleItemPosition>=0 && firstVisibleItemPosition<mItems.size()) {
            ImageListItem item = mItems.get(firstVisibleItemPosition);
            ImageListItem.ListItemPageParams pageParams = item.getPageParams();
            Date dt = pageParams.getDate();
            if(dt != null) {
                int savedCurrentPageNumber = prefs.getInt(ConstsAndUtils.TAG_PAGE_TO_VIEW,1);
                if(savedCurrentPageNumber!=pageParams.getPage()){
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(ConstsAndUtils.TAG_DATE_TO_VIEW, dt.getTime());
                    editor.putInt(ConstsAndUtils.TAG_PAGE_TO_VIEW, pageParams.getPage());
                    editor.putInt(ConstsAndUtils.TAG_NUMBER_ON_PAGE, pageParams.getNumberOnPage());
                    editor.apply();
                    return ConstsAndUtils.DateToStr_dd_mmmm_yyyy(dt);
                }
            }
        }
        return "";
    }

    void stopLoadingRequest(boolean clear){
        stopLoading();
        if(clear)clear();
    }


    public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
        BaseViewHolder(View itemView) {
            super(itemView);
        }
        protected abstract void clear();
        public void onBind(int position) {
            clear();
        }
    }
}
