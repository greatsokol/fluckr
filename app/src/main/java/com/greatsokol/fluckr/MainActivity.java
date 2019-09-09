package com.greatsokol.fluckr;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
        setLayout();
        doApiCall();
    }

    private void setLayout(){
        final FlickrImageListAdapter adapter = getAdapter();
        RecyclerView.LayoutManager layoutManager;
        boolean viewAsGrid = getViewAsGrid();
        adapter.setViewAsGrid(viewAsGrid);
        if (viewAsGrid)
            layoutManager = new FluckrGridLayoutManager(this, adapter, getSpanCount());
        else
            layoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(getAdapter());
        mRecyclerView.addOnScrollListener(new PaginationListener((LinearLayoutManager)layoutManager) {
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
                return adapter.isLoadingNow();
            }
        });
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
                View viewToShowSnackbar = findViewById(R.id.swipeRefresh);
                FlickrApi.LoadNextPicturesList(viewToShowSnackbar, getAdapter());
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.viewSwitch) {
            setViewAsGrid(!getViewAsGrid());
            setLayout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private static String settingNameViewAsGrid = "ViewAsGrid";
    private boolean getViewAsGrid(){
        return getPreferences(MODE_PRIVATE).getBoolean(settingNameViewAsGrid, true);
    }

    private void setViewAsGrid(boolean bAsGrid){
        SharedPreferences activityPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putBoolean(settingNameViewAsGrid, bAsGrid);
        editor.apply();
    }

}
