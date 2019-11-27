package com.greatsokol.fluckr.views;

import android.app.LauncherActivity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.BaseViewHolder> {

    private List<ImageListItem> mItems;
    private RecyclerView mRecyclerView;
    private boolean mViewAsGrid = true;

    void setViewAsGrid(boolean viewAsGrid){mViewAsGrid = viewAsGrid;}

    private int mSpanCount = 3;
    void setSpanCount(int spanCount){mSpanCount = spanCount;}


    private View.OnClickListener itemClickListener;


    ImageListAdapter(ArrayList<ImageListItem> items) {
        mItems = items;
    }

    void setOnItemClickListener(View.OnClickListener listener){
        itemClickListener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ImageListItem.VIEW_TYPE_IMAGE){
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

    void addItemsAtBottom(List<ImageListItem> newItems,
                          ImageListItem.ListItemPageParams restorationPageParams) {
        mItems.addAll(newItems);
        int itemsSize = newItems.size();
        int positionStart = mItems.size() - itemsSize;
        notifyItemRangeInserted(positionStart, itemsSize);
        if(restorationPageParams != null) {
            final int position = getItemPosition(restorationPageParams.getDate(),
                    restorationPageParams.getPage(), restorationPageParams.getNumberOnPage());
            if (position != ConstsAndUtils.NO_POSITION) {
                mRecyclerView.scrollToPosition(position);

                /*mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.smoothScrollToPosition(position);
                    }
                }, 1000); */
            }
        }
    }

    void addItemsUpper(List<ImageListItem> newItems) {
        if(newItems.size()==0) return;

        int removed = __removeItemsOfType(ImageListItem.VIEW_TYPE_PLACEHOLDER, false, false);
        ImageListItem firstItem = newItems.get(0);
        int headerIndex = firstItem.getViewType()==ImageListItem.VIEW_TYPE_DATE ? 1 : 0;

        int countOfNew = newItems.size() - headerIndex - removed;
        if(countOfNew>0) {
            int remainder = countOfNew % mSpanCount;
            if (remainder != 0) {
                int countOfPlaceholders = mSpanCount - remainder;
                for (int i = 0; i < countOfPlaceholders; i++) {
                    newItems.add(headerIndex,
                            new ImageListItem( // empty placeholder
                                    ImageListItem.VIEW_TYPE_PLACEHOLDER,
                                    firstItem.getPageParams().getDate(),
                                    firstItem.getPageParams().getPagesTotal(),
                                    firstItem.getPageParams().getPage()));
                }

                Log.d("DEBUG ADD UPPER ",
                        String.format("mSpanCount %d; removed %d; index = %d; countOfNew=%d; countOfPlaceholders=%d",
                                mSpanCount, removed, headerIndex, countOfNew, countOfPlaceholders));
            }
        }


        mItems.addAll(0, newItems);
        if(removed>0)notifyItemRangeChanged(newItems.size()-removed, removed);
        notifyItemRangeInserted(0, newItems.size()-removed);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
        setFadeAnimation(holder.itemView);
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);
    }

    private int __removeItemsOfType(int itemType, boolean notify, boolean all){
        int removed = 0;
        for (int i = 0; i < mItems.size(); i++) {
            int type = getItemViewType(i);
            if (type == itemType) {
                mItems.remove(i);
                if(notify)notifyItemRemoved(i);
                i--;
                removed++;
            } else if(!all) break;
        }
        return removed;
    }

    void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    private ImageListItem getItem(int position) {
        return mItems.isEmpty() ? null : mItems.get(position);
    }

    private int getItemPosition(Date date, int page, int posAtPage) {
        for(int i=0; i<mItems.size()-1; i++){
            ImageListItem item = getItem(i);
            if(item != null) {
                ImageListItem.ListItemPageParams pageParams = item.getPageParams();
                if (pageParams.equalparams(date, page, posAtPage))
                    return i;
            }
        }
        return ConstsAndUtils.NO_POSITION;
    }

    public class ViewHolder extends BaseViewHolder implements View.OnTouchListener {
        TextView textViewDate;
        TextView textViewTitle;
        TextView textViewDetails;
        ImageView imageView;
        boolean imageLoaded;
        View progressBar;
        int itemPosition;
        ImageListItem listItem;

        ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDetails = itemView.findViewById(R.id.text_view_details);
            imageView = itemView.findViewById(R.id.image_view);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            progressBar = itemView.findViewById(R.id.progress_bar);
            itemView.setOnTouchListener(this);
        }

        @Override
        protected void clear() {}

        @Override
        public void onBind(int position) {
            super.onBind(position);
            itemPosition = position;
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
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        progressBar.setVisibility(View.GONE);
                        imageView.setImageBitmap(bitmap);
                        imageView.setTag(null);
                        imageLoaded = true;
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        imageView.setTag(null);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        imageLoaded = false;
                        imageView.setImageBitmap(null);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                };

                imageView.setTag(target); // making strong reference
                Picasso.get().
                        load(listItem.getThumbnailUrl()).
                        transform(new ThumbnailTransformation(150, 150)).
                        into(target);

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
                view.performClick();
                if(itemClickListener !=null && imageLoaded) {
                    Bundle args = new Bundle();
                    args.putInt(ConstsAndUtils.TRANS_POSITION, itemPosition);
                    args.putString(ConstsAndUtils.TITLE, listItem.getTitle());
                    args.putString(ConstsAndUtils.DETAILS, listItem.getDetails());
                    args.putString(ConstsAndUtils.THUMBURL, listItem.getThumbnailUrl());
                    args.putString(ConstsAndUtils.FULLSIZEURL, listItem.getFullsizeUrl());
                    view.setTag(args);
                    itemClickListener.onClick(view);
                }
            } else view.setPressed(true);
            return false;
        }
    }


    private ImageListItem getFirstImageItem(){
        if(mItems.size()>0) return mItems.get(0);
        return null;
    }

    private ImageListItem getLastImageItem(){
        if(mItems.size()>0) return mItems.get(mItems.size()-1);
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
            if(item.getViewType() == ImageListItem.VIEW_TYPE_IMAGE) {
                ImageListItem.ListItemPageParams pageParams = item.getPageParams();
                Date dt = pageParams.getDate();
                if (dt != null) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(ConstsAndUtils.DATE_TO_VIEW, dt.getTime());
                    editor.putInt(ConstsAndUtils.PAGE_TO_VIEW, pageParams.getPage());
                    editor.putInt(ConstsAndUtils.NUMBER_ON_PAGE, pageParams.getNumberOnPage());
                    editor.putInt(ConstsAndUtils.PAGES_TOTAL, pageParams.getPagesTotal());
                    editor.apply();
                    return String.format(Locale.getDefault(), "%s (pg %d/%d)",
                            ConstsAndUtils.DateToStr_dd_mmmm_yyyy(dt),
                            pageParams.getPage(), pageParams.getPagesTotal());
                }
            }
        }
        return "";
    }

    void stopLoadingRequest(boolean clear){
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
