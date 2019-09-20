package com.greatsokol.fluckr;

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
import java.util.ArrayList;
import java.util.List;

public class FlickrImageListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<FlickrImageListItem> mItems;
    private boolean mIsLoadingNow = false;
    private int mCurrentPage = 1;
    private boolean mViewAsGrid = true;

    boolean isLoadingNow(){return mIsLoadingNow; }
    int getCurrentPage(){return mCurrentPage;}
    void setCurrentPage(int number){mCurrentPage = number;}
    void setViewAsGrid(boolean viewAsGrid){mViewAsGrid = viewAsGrid;}

    private int mTotalPage = 1; // обновится после LoadNextPicturesList
    boolean isLastPage(){return mCurrentPage>mTotalPage;}
    void setTotalPage(int totalPage){mTotalPage = totalPage;}


    private View.OnClickListener mItemClickListener;


    FlickrImageListAdapter(ArrayList<FlickrImageListItem> items) {
        mItems = items;
    }

    void setOnItemClickListener(View.OnClickListener listener){
        mItemClickListener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == FlickrImageListItem.VIEW_TYPE_LOADING)
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
    public int getItemViewType(int position) {
        FlickrImageListItem item = getItem(position);
        return item != null  ?
                item.getViewType() :
                FlickrImageListItem.VIEW_TYPE_UNKNOWN;
    }

    @Override
    public int getItemCount() {
        return mItems==null? 0 : mItems.size();
    }

    void addItems(List<FlickrImageListItem> items) {
        mItems.addAll(items);
        int itemsSize = items.size();
        int positionStart = mItems.size() - itemsSize;
        notifyItemRangeInserted(positionStart, itemsSize);
    }

    void startLoading() {
        mIsLoadingNow = true;
        mItems.add(new FlickrImageListItem(FlickrImageListItem.VIEW_TYPE_LOADING));
        notifyItemInserted(mItems.size() - 1);
    }

    void stopLoading() {
        ArrayList<Integer> list_to_remove = new ArrayList<>();
        for(int i=0; i<mItems.size(); i++){
            if (getItemViewType(i)==FlickrImageListItem.VIEW_TYPE_LOADING){
                list_to_remove.add(i);
            }
        }

        for(int i=0; i<list_to_remove.size(); i++){
            int num_to_remove = list_to_remove.get(i);
            if(mItems.size() > num_to_remove) {
                mItems.remove(num_to_remove);
                notifyItemRemoved(num_to_remove);
            }
        }
        mIsLoadingNow = false;
    }

    void clear() {
        if (isLoadingNow()) return;
        mItems.clear();
        setCurrentPage(1);
        notifyDataSetChanged();
    }

    private FlickrImageListItem getItem(int position) {
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
            FlickrImageListItem listItem = getItem(position);
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
                    FlickrImageListItem listItem = getItem(mItemPosition);
                    assert listItem != null;
                    Bundle args = new Bundle();
                    args.putInt(ConstsAndUtils.TAG_TR_POSITION, mItemPosition);
                    args.putString(ConstsAndUtils.TAG_TITLE, listItem.getTitle());
                    args.putString(ConstsAndUtils.TAG_DETAILS, listItem.getDetails());
                    args.putString(ConstsAndUtils.TAG_THUMBURL, listItem.getThumbnailUrl());
                    args.putString(ConstsAndUtils.TAG_FULLSIZEURL, listItem.getFullsizeUrl());
                    view.setTag(args);
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
}
