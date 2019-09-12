package com.greatsokol.fluckr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ActivityMain extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefresh;
    private int mTransitionPosition;
    private FlickrRequest mFlickrRequest;

    private FlickrImageListAdapter getTodayListAdapter(){ return ((FluckrApplication)getApplication()).getAdapter();}
    private FlickrImageListAdapter getSearchAdapter(){ return ((FluckrApplication)getApplication()).getSearchAdapter();}
    private FlickrImageListAdapter getActiveAdapter(){ return
            mSearchFor == null ||
            mSearchFor.equals("") ? ((FluckrApplication)getApplication()).getAdapter() :
                                    ((FluckrApplication)getApplication()).getSearchAdapter();}
    private String mSearchFor = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_actionbar));
        mSwipeRefresh = findViewById(R.id.swipeRefresh);
        mSwipeRefresh.setOnRefreshListener(this);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        setLayout();
        doApiCall();


        // Workaround for orientation change issue
        if (savedInstanceState != null) {
            mTransitionPosition = savedInstanceState.getInt(Consts.TAG_TR_POSITION);
        }

        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
                if (sharedElements.isEmpty()) {
                    View view = Objects.
                            requireNonNull(mRecyclerView.getLayoutManager()).
                            findViewByPosition(mTransitionPosition);
                    if (view != null && names.size()>0) {
                        sharedElements.put(names.get(0), view);
                    }
                }
            }
        });
    }


    private void setLayout(){
        RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        Parcelable recycle_state = lm!=null ? lm.onSaveInstanceState() : null;

        final FlickrImageListAdapter adapter = getActiveAdapter();
        boolean viewAsGrid = getViewAsGrid();
        adapter.setViewAsGrid(viewAsGrid);
        FluckrGridLayoutManager layoutManager
                = new FluckrGridLayoutManager(this, adapter, getSpanCount(viewAsGrid));

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(getActiveAdapter());
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
                return adapter.isLoadingNow();
            }
        });

        if (recycle_state!=null)
            mRecyclerView.getLayoutManager().onRestoreInstanceState(recycle_state);
    }

    protected int getSpanCount(boolean viewAsGrid){
        return getResources().
                getInteger(viewAsGrid ? R.integer.span_for_grid : R.integer.span_for_linear);
    }

    private void stopRequestLoading(boolean clear){
        if(mFlickrRequest!=null) mFlickrRequest.Stop();
        getActiveAdapter().stopLoading();
        if(clear)getActiveAdapter().clear();
    }

    @Override
    public void onRefresh() {
        stopRequestLoading(true);
        doApiCall();
    }

    private void doApiCall() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefresh.setRefreshing(false);
                if(mFlickrRequest!=null) mFlickrRequest.Stop();
                mFlickrRequest = new FlickrRequest();
                mFlickrRequest.prepareLoadNextPicturesListRequest(
                        mSwipeRefresh,
                        getActiveAdapter(),
                        mSearchFor);
                mFlickrRequest.Execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        searchViewMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                stopRequestLoading(!mSearchFor.equals(""));
                mSearchFor = "";
                setLayout();
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                // hide keyboard:
                View v = getCurrentFocus();
                if(v!=null){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    v.clearFocus();
                }

                if(queryText.trim().equals("")) return false;
                stopRequestLoading(false);
                mSearchFor = queryText;
                getSearchAdapter().clear();
                setLayout();
                doApiCall();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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


    private boolean getViewAsGrid(){
        return getPreferences(MODE_PRIVATE).getBoolean(Consts.TAG_VIEWASGRID, true);
    }

    private void setViewAsGrid(boolean bAsGrid){
        SharedPreferences activityPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putBoolean(Consts.TAG_VIEWASGRID, bAsGrid);
        editor.apply();
    }


    @Override
    protected void onStart() {
        super.onStart();
        getTodayListAdapter().setOnItemClickListener(this);
        getSearchAdapter().setOnItemClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getTodayListAdapter().setOnItemClickListener(null);
        getSearchAdapter().setOnItemClickListener(null);
    }

    @Override
    public void onClick(View view) {
        Bundle args = (Bundle) view.getTag();
        if (args!=null) {
            mTransitionPosition = args.getInt(Consts.TAG_TR_POSITION);
            View imageView = view.findViewById(R.id.imageview);
            if (imageView != null) {
                Intent intent = new Intent(ActivityMain.this, ActivityView.class);
                String transitionName = ViewCompat.getTransitionName(imageView);
                assert transitionName != null;
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(ActivityMain.this,
                                imageView,
                                transitionName);
                intent.putExtra(Consts.TAG_TR_NAME, transitionName);
                intent.putExtra(Consts.TAG_ARGS, args);
                startActivity(intent, options.toBundle());
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(Consts.TAG_TR_POSITION, mTransitionPosition);
        super.onSaveInstanceState(outState);
    }
}
