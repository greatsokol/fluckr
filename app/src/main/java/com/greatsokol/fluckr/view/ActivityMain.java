package com.greatsokol.fluckr.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.greatsokol.fluckr.FluckrApp;
import com.greatsokol.fluckr.R;
import com.greatsokol.fluckr.contract.ContractMain;
import com.greatsokol.fluckr.etc.ConstsAndUtils;
import com.greatsokol.fluckr.etc.ImageGridLayoutManager;
import com.greatsokol.fluckr.etc.PaginationListenerOnFling;
import com.greatsokol.fluckr.etc.PaginationListenerOnScroll;
import com.greatsokol.fluckr.presenter.ImageListPresenter;

import java.util.ArrayList;
import java.util.Date;


public class ActivityMain extends AppCompatActivity
        implements ContractMain.ViewMain, View.OnClickListener {

    private RecyclerView mRecyclerView;
    private int mTransitionPosition;
    private boolean mActivityViewStarted = false;
    private Toolbar mToolbar;
    private ContractMain.ImageListPresenter mPresenter;

    private ImageListAdapter getTodayListAdapter(){ return ((FluckrApp)getApplication()).getTodayListAdapter();}
    private ImageListAdapter getSearchAdapter(){ return ((FluckrApp)getApplication()).getSearchAdapter();}
    private ImageListAdapter getActiveAdapter(){ return
            mSearchFor == null ||
            mSearchFor.equals("") ? getTodayListAdapter() : getSearchAdapter();}
    private String mSearchFor = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        loadInstanceSettings(savedInstanceState);

        setInsets();
        setLayout();

        mPresenter = new ImageListPresenter();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Date date = new Date(prefs.getLong(ConstsAndUtils.DATE_TO_VIEW,
                        ConstsAndUtils.DecDate(ConstsAndUtils.CurrentGMTDate()).getTime()));
        int page = prefs.getInt(ConstsAndUtils.PAGE_TO_VIEW,1);
        int itemNumber = prefs.getInt(ConstsAndUtils.NUMBER_ON_PAGE,1);
        boolean firstLoad = getActiveAdapter().getItemCount()==0;
        mPresenter.onViewCreate(this, firstLoad, date, page, itemNumber);


        // shared element transition trick:
        final RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        assert lm != null;
        if(mTransitionPosition != ConstsAndUtils.NO_POSITION) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    lm.scrollToPosition(mTransitionPosition);
                }
            }, 500);
        }
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
            mTransitionPosition = settings.getInt(ConstsAndUtils.TRANS_POSITION);
            mSearchFor = settings.getString(ConstsAndUtils.SEARCH_PHRASE);
        } else{
            mTransitionPosition = ConstsAndUtils.NO_POSITION;
            mSearchFor = "";
        }
    }

    private void saveInstanceSettings(Bundle settings){
        settings.putInt(ConstsAndUtils.TRANS_POSITION, mTransitionPosition);
        settings.putString(ConstsAndUtils.SEARCH_PHRASE, mSearchFor);
    }




    private void setInsets(){
        findViewById(R.id.constraint).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        final int toolbarHeight = mToolbar.getLayoutParams().height;

        ViewCompat.setOnApplyWindowInsetsListener(mToolbar, new OnApplyWindowInsetsListener(){
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
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
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
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
        final int spanCount = getSpanCount(viewAsGrid);
        final ImageListAdapter adapter = getActiveAdapter();

        adapter.setViewAsGrid(viewAsGrid);
        adapter.setSpanCount(spanCount);
        ImageGridLayoutManager layoutManager
                = new ImageGridLayoutManager(this, adapter, spanCount);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setOnFlingListener(new PaginationListenerOnFling(layoutManager) {
            @Override
            protected void loadNextPage() {
                mPresenter.onScrolledDown();
            }

            @Override
            protected void loadPrevPage() {
                mPresenter.onScrolledUp();
            }
        });



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
                mPresenter.onScrolledDown();
            }

            @Override
            protected void loadPrevPage() {
                mPresenter.onScrolledUp();
            }
        });

        if (recycleViewSavedState!=null)
            layoutManager.onRestoreInstanceState(recycleViewSavedState);
    }

    protected int getSpanCount(boolean viewAsGrid){
        return getResources().
                getInteger(viewAsGrid ? R.integer.span_for_grid : R.integer.span_for_linear);
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
                mPresenter.onViewCreate(ActivityMain.this, true,null, 0, 0);
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
        if (args!=null && !mActivityViewStarted) {
            View imageView = view.findViewById(R.id.imageview);
            if (imageView != null) {
                mTransitionPosition = args.getInt(ConstsAndUtils.TRANS_POSITION);
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
        mTransitionPosition = ConstsAndUtils.NO_POSITION;
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
        if(addAtBottom)
            getActiveAdapter().addItemsAtBottom(items, restorePosition);
        else
            getActiveAdapter().addItemsUpper(items);
    }

    @Override
    public void onFailure(String message) {
        Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onStartLoading(boolean addProgressbarAtBottom) {
        getActiveAdapter().startLoading(addProgressbarAtBottom);
    }

    @Override
    public void onStopLoading() {
        getActiveAdapter().stopLoading();
    }


    @Override
    public String getSearchPhrase() {
        return mSearchFor;
    }

    @Override
    public ImageListItem.ListItemPageParams getLastItemPageParams() {
        return getActiveAdapter().getLastItemPageParams();
    }

    @Override
    public ImageListItem.ListItemPageParams getFirstItemPageParams() {
        return getActiveAdapter().getFirstItemPageParams();
    }


}
