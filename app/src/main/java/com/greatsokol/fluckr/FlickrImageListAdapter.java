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


    private List<FlickrImageListItem> mItems;
    private boolean mIsLoadingNow = false;
    private int mCurrentPage = 1;

    boolean isLoadingNow(){return mIsLoadingNow;}
    int getCurrentPage(){return mCurrentPage;}
    void setCurrentPage(int number){mCurrentPage = number;}

    private static int mTotalPage = 10; // todo сделать правильное ограничение
    boolean isLastPage(){return mCurrentPage>mTotalPage;}
    void setTotalPage(int totalPage){mTotalPage = totalPage;}


    FlickrImageListAdapter(ArrayList<FlickrImageListItem> items) {
        this.mItems = items;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == FlickrImageListItem.VIEW_TYPE_LOADING)
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.listitem_loading, parent, false));
        else return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.listitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return mItems==null? 0 : mItems.size();
    }

    void addItems(List<FlickrImageListItem> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
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
            mItems.remove(num_to_remove);
            notifyItemRemoved(num_to_remove);
        }
        mIsLoadingNow = false;
    }

    void clear() {
        mItems.clear();
        setCurrentPage(1);
        notifyDataSetChanged();
    }

    private FlickrImageListItem getItem(int position) {
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
