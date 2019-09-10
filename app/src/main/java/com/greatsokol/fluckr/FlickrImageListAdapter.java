package com.greatsokol.fluckr;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FlickrImageListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<FlickrImageListItem> mItems;
    private boolean mIsLoadingNow = false;
    private int mCurrentPage = 1;
    private boolean mViewAsGrid = true;

    boolean isLoadingNow(){return mIsLoadingNow;}
    int getCurrentPage(){return mCurrentPage;}
    void setCurrentPage(int number){mCurrentPage = number;}
    void setViewAsGrid(boolean viewAsGrid){mViewAsGrid = viewAsGrid;}

    private static int mTotalPage = 1; // обновится после LoadNextPicturesList
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

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(
                0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
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
        //notifyDataSetChanged();
        notifyItemRangeInserted(mItems.size()-items.size(), mItems.size());
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
        if (isLoadingNow()) return;
        mItems.clear();
        setCurrentPage(1);
        notifyDataSetChanged();
    }

    private FlickrImageListItem getItem(int position) {
        return mItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder {
        TextView textViewTitle;
        TextView textViewDetails;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textviewTitle);
            textViewDetails = itemView.findViewById(R.id.textviewDetails);
            imageView = itemView.findViewById(R.id.imageview);
        }

        @Override
        protected void clear() {}

        @Override
        public void onBind(int position) {
            super.onBind(position);
            FlickrImageListItem listItem = getItem(position);
            if(textViewTitle!=null)
                textViewTitle.setText(listItem.getTitle());
            if(textViewDetails!=null) {
                //textViewDetails.setText(listItem.getDetails());
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    textViewDetails.setText(Html.fromHtml(listItem.getDetails(),Html.FROM_HTML_MODE_LEGACY));
                } else {
                    textViewDetails.setText(Html.fromHtml(listItem.getDetails()));
                }
            }
            imageView.setImageBitmap(listItem.getBitmapThumbnail());
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
