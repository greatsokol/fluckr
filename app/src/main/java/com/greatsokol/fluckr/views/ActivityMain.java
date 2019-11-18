package com.greatsokol.fluckr.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.greatsokol.fluckr.R;
import com.greatsokol.fluckr.contracts.MainContract;
import com.greatsokol.fluckr.etc.ConstsAndUtils;
import com.greatsokol.fluckr.etc.ImageGridLayoutManager;
import com.greatsokol.fluckr.etc.PaginationListenerOnScroll;
import com.greatsokol.fluckr.presenters.ImageListPresenter;

import java.util.ArrayList;
import java.util.Date;


public class ActivityMain extends AppCompatActivity
        implements MainContract.View, android.view.View.OnClickListener {

    private ImageListAdapter mAdapter;
    //private ImageListAdapter mSearchAdapter;
    private android.view.View mToolbarProgressBar;

    private RecyclerView mRecyclerView;
    //private int mTransitionPosition;
    private boolean mActivityViewStarted = false;
    private Toolbar mToolbar;
    private MainContract.Presenter mPresenter;

    private ImageListAdapter getTodayListAdapter(){ return mAdapter;}
    private ImageListAdapter getActiveAdapter(){ return mAdapter; }
    //private ImageListAdapter getSearchAdapter(){ return mSearchAdapter;}
    //private ImageListAdapter getActiveAdapter(){ return
      //      mSearchFor == null ||
        //    mSearchFor.equals("") ? getTodayListAdapter() : getSearchAdapter();}
    private String mSearchFor = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.toolbar_actionbar);
        mToolbarProgressBar = mToolbar.findViewById(R.id.toolbar_progress_bar);
        setSupportActionBar(mToolbar);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new ImageListAdapter(new ArrayList<ImageListItem>());
        //mSearchAdapter = new ImageListAdapter(new ArrayList<ImageListItem>());

        loadInstanceSettings(savedInstanceState);

        setInsets();
        setLayout();

        mPresenter = new ImageListPresenter();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Date date = new Date(prefs.getLong(ConstsAndUtils.DATE_TO_VIEW,
                        ConstsAndUtils.DecDate(ConstsAndUtils.CurrentGMTDate()).getTime()));
        int page = prefs.getInt(ConstsAndUtils.PAGE_TO_VIEW,1);
        int itemNumber = prefs.getInt(ConstsAndUtils.NUMBER_ON_PAGE,1);
        ImageListItem.ListItemPageParams params =
                new ImageListItem.ListItemPageParams(itemNumber, date, page, 0);
        mPresenter.onViewCreate(this, params);


        // shared element transition trick:
        /*final RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        assert lm != null;
        if(mTransitionPosition != ConstsAndUtils.NO_POSITION) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    lm.scrollToPosition(mTransitionPosition);
                }
            }, 500);
        } */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onViewDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        saveInstanceSettings(outState);
        super.onSaveInstanceState(outState);
    }


    private void loadInstanceSettings(Bundle settings){
        if (settings != null) {
            //mTransitionPosition = settings.getInt(ConstsAndUtils.TRANS_POSITION);
            mSearchFor = settings.getString(ConstsAndUtils.SEARCH_PHRASE);
        } else{
            //mTransitionPosition = ConstsAndUtils.NO_POSITION;
            mSearchFor = "";
        }
    }

    private void saveInstanceSettings(Bundle settings){
        //settings.putInt(ConstsAndUtils.TRANS_POSITION, mTransitionPosition);
        settings.putString(ConstsAndUtils.SEARCH_PHRASE, mSearchFor);
    }




    private void setInsets(){
        findViewById(R.id.constraint).setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        final int toolbarHeight = mToolbar.getLayoutParams().height;

        ViewCompat.setOnApplyWindowInsetsListener(mToolbar, new OnApplyWindowInsetsListener(){
            @Override
            public WindowInsetsCompat onApplyWindowInsets(android.view.View v, WindowInsetsCompat insets) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                lp.setMargins(  insets.getSystemWindowInsetLeft(),
                                insets.getSystemWindowInsetTop(),
                                insets.getSystemWindowInsetRight(),
                                lp.bottomMargin);
                v.setLayoutParams(lp);
                ViewCompat.setOnApplyWindowInsetsListener(mToolbar, null);
                return insets;
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(mRecyclerView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(android.view.View v, WindowInsetsCompat insets) {
                v.setPadding(
                        insets.getSystemWindowInsetLeft() + v.getPaddingLeft(),
                        insets.getSystemWindowInsetTop() + toolbarHeight ,
                        insets.getSystemWindowInsetRight() + v.getPaddingRight(),
                        v.getPaddingBottom() + insets.getSystemWindowInsetBottom());
                ViewCompat.setOnApplyWindowInsetsListener(mRecyclerView, null);
                return insets;
            }
        });
    }

    private void setLayout(){
        RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        Parcelable recycleViewSavedState = lm!=null ? lm.onSaveInstanceState() : null;

        final boolean viewAsGrid = settings_getViewAsGrid();
        final int spanCount = getResources().
                getInteger(viewAsGrid ? R.integer.span_for_grid : R.integer.span_for_linear);
        final ImageListAdapter adapter = getActiveAdapter();

        adapter.setViewAsGrid(viewAsGrid);
        adapter.setSpanCount(spanCount);
        ImageGridLayoutManager layoutManager
                = new ImageGridLayoutManager(this, adapter, spanCount);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);


        mRecyclerView.clearOnScrollListeners();
        mRecyclerView.addOnScrollListener(new PaginationListenerOnScroll(layoutManager) {
            @Override
            protected void onScrolled(int firstVisibleItemPosition) {
                String title = getActiveAdapter().saveNavigationSettings(
                        getPreferences(MODE_PRIVATE),
                        firstVisibleItemPosition);
                if(!title.equals(""))
                    mToolbar.setTitle(title);
            }

            @Override
            protected void loadNextPage() {
                ImageListItem.ListItemPageParams pageParams =
                        getActiveAdapter().getLastItemPageParams();
                if(pageParams != null)
                    mPresenter.onScrolledDown(pageParams);
            }

            @Override
            protected void loadPrevPage() {
                ImageListItem.ListItemPageParams pageParams =
                        getActiveAdapter().getFirstItemPageParams();
                if(pageParams != null)
                    mPresenter.onScrolledUp(pageParams);
            }
        });

        if (recycleViewSavedState!=null)
            layoutManager.onRestoreInstanceState(recycleViewSavedState);
    }

    private void stopRequestLoading(boolean clear){
        getActiveAdapter().stopLoadingRequest(clear);
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
                android.view.View v = getCurrentFocus();
                if(v!=null){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    v.clearFocus();
                }

                if(queryText.trim().equals("")) return false;
                stopRequestLoading(false);
                mSearchFor = queryText;
                getActiveAdapter().clear();
                setLayout();
                mPresenter.onViewCreate(ActivityMain.this,
                        new ImageListItem.ListItemPageParams(0,null,0,0));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        if(!mSearchFor.equals(""))
            restoreSearchViewState(searchViewMenuItem, mSearchFor);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.viewSwitch) {
            settings_setViewAsGrid(!settings_getViewAsGrid());
            setLayout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getTodayListAdapter().setOnItemClickListener(this);
        //getSearchAdapter().setOnItemClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getTodayListAdapter().setOnItemClickListener(null);
        //getSearchAdapter().setOnItemClickListener(null);
    }

        @Override
    public void onClick(android.view.View view) {
        Bundle args = (Bundle) view.getTag();
        if (args!=null && !mActivityViewStarted) {
            android.view.View imageView = view.findViewById(R.id.image_view);
            if (imageView != null) {
                //mTransitionPosition = args.getInt(ConstsAndUtils.TRANS_POSITION);
                mActivityViewStarted = true;
                Intent intent = new Intent(ActivityMain.this, ActivityView.class);
                String transitionName = ViewCompat.getTransitionName(imageView);
                assert transitionName != null;

                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                ActivityMain.this,
                                       imageView, transitionName);


                intent.putExtra(ConstsAndUtils.TRANS_NAME, transitionName);
                intent.putExtra(ConstsAndUtils.ARGS, args);
                startActivityForResult(intent, 0, options.toBundle());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mActivityViewStarted = false;
        //mTransitionPosition = ConstsAndUtils.NO_POSITION;
    }

    void restoreSearchViewState(MenuItem searchItem, String query){
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (!TextUtils.isEmpty(query)) {
            searchItem.expandActionView();
            searchView.setQuery(query, false);
            searchView.clearFocus();
        }
    }

    private boolean settings_getViewAsGrid(){
        return getPreferences(MODE_PRIVATE).getBoolean(ConstsAndUtils.VIEWASGRID, true);
    }

    private void settings_setViewAsGrid(boolean bAsGrid){
        SharedPreferences activityPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putBoolean(ConstsAndUtils.VIEWASGRID, bAsGrid);
        editor.apply();
    }



    @Override
    public void onImageListDownloaded(ArrayList<ImageListItem> items, boolean addAtBottom, int restorePosition) {
        if(items != null) {
            if (addAtBottom)
                getActiveAdapter().addItemsAtBottom(items, restorePosition);
            else
                getActiveAdapter().addItemsUpper(items);
        }
    }

    @Override
    public void onFailure(String message) {
        Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onStartLoading() {
        mToolbarProgressBar.setVisibility(android.view.View.VISIBLE);
    }

    @Override
    public void onStopLoading() {
        mToolbarProgressBar.setVisibility(android.view.View.GONE);
    }


    @Override
    public String getSearchPhrase() {
        return mSearchFor;
    }

}
