package com.greatsokol.fluckr.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.greatsokol.fluckr.R;
import com.greatsokol.fluckr.contracts.ViewContract;
import com.greatsokol.fluckr.etc.ConstsAndUtils;
import com.greatsokol.fluckr.presenters.ImageViewPresenter;
import com.jsibbold.zoomage.ZoomageView;

public class ActivityView extends AppCompatActivity implements ViewContract.ViewView {
    private View mRootView;
    private ZoomageView mImageView;
    private ProgressBar mProgress;
    private Toolbar mToolbar;
    private Bundle mArgs;
    private Bitmap mThumbnail;
    private final static int FLAG_ALREADY_LOADED_HIGH_RES = 1;
    ImageViewPresenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        postponeEnterTransition();
        mToolbar = findViewById(R.id.toolbar_actionbar);
        mRootView = findViewById(R.id.constraint);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        setInsets();

        mImageView = findViewById(R.id.image_view_big);
        mProgress = findViewById(R.id.progress_bar);
        Intent intent = getIntent();
        mArgs = intent.getBundleExtra(ConstsAndUtils.ARGS);
        ViewCompat.setTransitionName(mImageView, intent.getStringExtra(ConstsAndUtils.TRANS_NAME));


        mPresenter = new ImageViewPresenter();
        mPresenter.attachView(this);
        mPresenter.loadThumbnail();
        mPresenter.setTitle();
        mPresenter.setDescription();

        // run higher resolution picture
        if (savedInstanceState != null) {
            if (savedInstanceState.getInt(ConstsAndUtils.READY, 0) == FLAG_ALREADY_LOADED_HIGH_RES)
                mPresenter.loadHighResolutionImage(); // load without waiting for shared element transition ends
        } else {
            // load after shared element transition ends
            final Transition windowTransition = getWindow().getSharedElementEnterTransition();
            windowTransition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {}
                @Override
                public void onTransitionCancel(Transition transition) {
                    mPresenter.loadHighResolutionImage();
                    windowTransition.removeListener(this);
                }
                @Override
                public void onTransitionPause(Transition transition) {}
                @Override
                public void onTransitionResume(Transition transition) {}
                @Override
                public void onTransitionEnd(Transition transition) {
                    mPresenter.loadHighResolutionImage();
                    windowTransition.removeListener(this);
                }
            });
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    private void setInsets() {
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

        final ScrollView scrollView = findViewById(R.id.scroll_view);
        ViewCompat.setOnApplyWindowInsetsListener(scrollView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                boolean land = ConstsAndUtils.isLandscape(getResources());
                v.setPadding(v.getPaddingLeft(),
                        land ? toolbarHeight + insets.getSystemWindowInsetTop() : v.getPaddingTop(),
                        land ? insets.getSystemWindowInsetRight() : v.getPaddingRight(),
                        land ? v.getPaddingBottom() : insets.getSystemWindowInsetBottom());
                ViewCompat.setOnApplyWindowInsetsListener(scrollView, null);
                return insets;
            }
        });
    }


    @Override
    public void finishAfterTransition() {
        mImageView.setImageBitmap(mThumbnail);
        mImageView.reset(false);
        mImageView.setImageMatrix(mImageView.getMatrix()); //trick

        setResult(0);
        super.finishAfterTransition();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        if (id == android.R.id.home) {
            finishAfterTransition();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // save flag to load picture without waiting shared elements transition
        outState.putInt(ConstsAndUtils.READY, FLAG_ALREADY_LOADED_HIGH_RES);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onThumbnailLoaded(Bitmap image) {
        mThumbnail = image;
        mImageView.setImageBitmap(mThumbnail);
        startPostponedEnterTransition();
    }

    @Override
    public void onImageLoaded(Bitmap image) {
        mImageView.setImageBitmap(image);
    }

    @Override
    public void onLoadFailed(String message) {
        startPostponedEnterTransition();
        Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
    }

    @Override
    public int getThumbnailSize() {
        return ConstsAndUtils.pxFromDp(getResources(), 300);
    }

    @Override
    public String getThumbnailUrl() {
        return mArgs.getString(ConstsAndUtils.THUMBURL);
    }

    @Override
    public String getHighResolutionUrl() {
        return mArgs.getString(ConstsAndUtils.FULLSIZEURL);
    }

    @Override
    public String getTitleText() {
        return mArgs.getString(ConstsAndUtils.TITLE);
    }

    @Override
    public String getDescriptionText() {
        return mArgs.getString(ConstsAndUtils.DETAILS);
    }

    @Override
    public void setTitleText(String title) {
        setTitle(title);
    }

    @Override
    public void setDescriptionText(String description) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ((TextView) findViewById(R.id.text_view_details)).
                    setText(Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY));
        } else {
            ((TextView) findViewById(R.id.text_view_details)).
                    setText(Html.fromHtml(description));
        }
    }

    @Override
    public void showProgressBar() {
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        mProgress.setVisibility(View.GONE);
    }
}
