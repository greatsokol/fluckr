package com.greatsokol.fluckr;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView mRecyclerView;
    SwipeRefreshLayout swipeRefresh;

    FlickrImageListAdapter getAdapter(){ return ((FluckrApplication)getApplication()).getAdapter();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        final FlickrImageListAdapter adapter = getAdapter();
        final GridLayoutManager layoutManager = new FluckrGridLayoutManager(this, adapter, getSpanCount());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(getAdapter());
        mRecyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                doApiCall();
            }

            @Override
            public boolean isLastPage() {
                return adapter.isLastPage();
            }

            @Override
            public boolean isLoading() {
                return adapter.isLoadingNow;
            }
        });

        doApiCall();
    }

    protected int getSpanCount(){
        return getResources().getInteger(R.integer.span_for_grid);
    }

    @Override
    public void onRefresh() {
        getAdapter().clear();
        doApiCall();
    }

    private void doApiCall() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                FlickrApi.LoadPicturesList(getAdapter());
                swipeRefresh.setRefreshing(false);
            }
        });
    }
}
