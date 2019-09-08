package com.greatsokol.fluckr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.greatsokol.fluckr.FlickrApi.FLICKR_PER_PAGE;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView mRecyclerView;
    SwipeRefreshLayout swipeRefresh;


    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;

    FlickrImageListAdapter getAdapter(){ return ((FluckrApplication)getApplication()).getAdapter();}
    int getCurrentPage(){ return ((FluckrApplication)getApplication()).getCurrentPage();}
    void setCurrentPage(int number){((FluckrApplication)getApplication()).setCurrentPage(number);}


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

        mRecyclerView.setAdapter(getAdapter());
        doApiCall();
        mRecyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                setCurrentPage(getCurrentPage() + 1);
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
        setCurrentPage(1);
        isLastPage = false;
        getAdapter().clear();
        doApiCall();
    }

    private void doApiCall() {
        final ArrayList<FlickrApi.FlickrImageListItem> items = new ArrayList<>();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                FlickrImageListAdapter adapter = getAdapter();
                int currentPage = getCurrentPage();
                FlickrApi.LoadPicturesList(adapter, items, currentPage);

                if (currentPage != 1) adapter.removeLoading();
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
