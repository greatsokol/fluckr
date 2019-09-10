package com.greatsokol.fluckr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.Map;


public class ActivityMain extends AppCompatActivity implements /*SwipeRefreshLayout.OnRefreshListener,*/ View.OnClickListener {
    RecyclerView mRecyclerView;
    //SwipeRefreshLayout swipeRefresh;
    private int mTransitionPosition;
    private
    FlickrImageListAdapter getAdapter(){ return ((FluckrApplication)getApplication()).getAdapter();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);

        //swipeRefresh = findViewById(R.id.swipeRefresh);
        //swipeRefresh.setOnRefreshListener(this);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        setLayout();
        doApiCall();


        // Workaround for orientation change issue
        if (savedInstanceState != null) {
            mTransitionPosition = savedInstanceState.getInt(Consts.TAG_TR_POSITION);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //mRecyclerView.scrollToPosition(mTransitionPosition);
                    //startPostponedEnterTransition();
                }
            }, 500);
        }

        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
                if (sharedElements.isEmpty()) {
                    View view = mRecyclerView.getLayoutManager().findViewByPosition(mTransitionPosition);
                    if (view != null && names.size()>0) {
                        sharedElements.put(names.get(0), view);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        //postponeEnterTransition();
    }

    private void setLayout(){
        RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        Parcelable recycle_state = lm!=null ? lm.onSaveInstanceState() : null;

        final FlickrImageListAdapter adapter = getAdapter();
        boolean viewAsGrid = getViewAsGrid();
        adapter.setViewAsGrid(viewAsGrid);
        FluckrGridLayoutManager layoutManager
                = new FluckrGridLayoutManager(this, adapter, getSpanCount(viewAsGrid));

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

    /*@Override
    public void onRefresh() {
        getAdapter().clear();
        doApiCall();
    }*/

    private void doApiCall() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                View viewToShowSnackbar = findViewById(R.id.recyclerView);
                FlickrApi.LoadNextPicturesList(viewToShowSnackbar, getAdapter());
                //swipeRefresh.setRefreshing(false);
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
        getAdapter().setOnItemClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getAdapter().setOnItemClickListener(null);
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
