package com.greatsokol.fluckr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.greatsokol.fluckr.PaginationListener.PAGE_START;
import static com.greatsokol.fluckr.FlickrApi.FLICKR_PER_PAGE;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView mRecyclerView;
    SwipeRefreshLayout swipeRefresh;
    private FlickrImageListAdapter adapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new FlickrImageListAdapter(new ArrayList<FlickrApi.FlickrImageListItem>());
        mRecyclerView.setAdapter(adapter);
        doApiCall();
        mRecyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                doApiCall();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    @Override
    public void onRefresh() {
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
        doApiCall();
    }

    private void doApiCall() {
        final ArrayList<FlickrApi.FlickrImageListItem> items = new ArrayList<>();




        new Handler().post(new Runnable() {

            @Override
            public void run() {
                /*for (int i = 0; i < FLICKR_PER_PAGE; i++) {
                    itemCount++;
                    FlickrApi.FlickrImageListItem item = new FlickrApi.FlickrImageListItem();
                    item.setTitle(String.valueOf(itemCount));
                    items.add(item);
                }*/
                FlickrApi.LoadPicturesList(adapter, items, currentPage);

                if (currentPage != PAGE_START) adapter.removeLoading();
                adapter.addItems(items);
                swipeRefresh.setRefreshing(false);

                // check weather is last page or not
                if (currentPage < totalPage) {
                    adapter.addLoading();
                } else {
                    isLastPage = true;
                }
                isLoading = false;
            }
        });
    }
}
