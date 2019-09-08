package com.greatsokol.fluckr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FlickrImageListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    static final int VIEW_TYPE_LOADING = 0;
    static final int VIEW_TYPE_NORMAL = 1;
    private List<FlickrApi.FlickrImageListItem> mItems;
    private boolean mIsLoadingNow = false;
    private int mCurrentPage = 1;

    boolean isLoadingNow(){return mIsLoadingNow;}
    int getCurrentPage(){return mCurrentPage;}
    void setCurrentPage(int number){mCurrentPage = number;};

    private static int mTotalPage = 10; // todo сделать правильное ограничение
    boolean isLastPage(){return mCurrentPage>mTotalPage;}
    void setTotalPage(int totalPage){mTotalPage = totalPage;}


    FlickrImageListAdapter(ArrayList<FlickrApi.FlickrImageListItem> items) {
        this.mItems = items;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_loading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoadingNow()) {
            return position == mItems.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return mItems==null? 0 : mItems.size();
    }

    void addItems(List<FlickrApi.FlickrImageListItem> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    void addLoading() {
        mIsLoadingNow = true;
        mItems.add(new FlickrApi.FlickrImageListItem());
        notifyItemInserted(mItems.size() - 1);
    }

    void removeLoading() {
        /*for(int i=0; i<mItems.size()-1; i++){
            if(getItemViewType(i)==VIEW_TYPE_LOADING){
                mItems.remove(i);
                notifyItemRemoved(i);
            }
        } */

        int position = mItems.size()-1;
        mItems.remove(position);
        notifyItemRemoved(position);
        mIsLoadingNow = false;
    }

    void clear() {
        mItems.clear();
        setCurrentPage(1);
        notifyDataSetChanged();
    }

    FlickrApi.FlickrImageListItem getItem(int position) {
        return mItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder {
        TextView textViewTitle;
        ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textTitle);
            //Load picture from cache or internet ??
        }

        @Override
        protected void clear() {}

        @Override
        public void onBind(int position) {
            super.onBind(position);
            textViewTitle.setText(getItem(position).getTitle());
            //Load picture from cache or internet ??
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
