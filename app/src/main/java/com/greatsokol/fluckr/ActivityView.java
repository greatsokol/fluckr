package com.greatsokol.fluckr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class ActivityView extends AppCompatActivity {

    ImageView mImageView;
    ProgressBar mProgress;
    Toolbar mToolbar;
    Bundle mArgs;
    String mCacheDir;
    private final static int FLAG_ALREADY_LOADED_HIGH_RES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        mToolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setInsets();

        mImageView = findViewById(R.id.imageViewBig);
        mProgress = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        ViewCompat.setTransitionName(mImageView, intent.getStringExtra(ConstsAndUtils.TAG_TR_NAME));

        mArgs = intent.getBundleExtra(ConstsAndUtils.TAG_ARGS);
        assert mArgs != null;
        mCacheDir = getCacheDir().getAbsolutePath();
        final String thumbnailPath = mArgs.getString(ConstsAndUtils.TAG_THUMBURL);
        assert thumbnailPath != null;
        Bitmap bmp = ImageLoader.loadPictureFromCache(
                            ImageLoader.convertUrlToCacheFileName(thumbnailPath, mCacheDir),false, 320);
        mImageView.setImageBitmap(bmp);

        final String title = mArgs.getString(ConstsAndUtils.TAG_TITLE);
        setTitle(title);

        String details = mArgs.getString(ConstsAndUtils.TAG_DETAILS);
        assert details != null;
        if (details.trim().equals("")) details = title;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ((TextView)findViewById(R.id.textviewDetails)).
                    setText(Html.fromHtml(details,Html.FROM_HTML_MODE_LEGACY));
        } else {
            ((TextView)findViewById(R.id.textviewDetails)).
                    setText(Html.fromHtml(details));
        }


        // run higher resolution picture
        if (savedInstanceState!=null){
            if(savedInstanceState.getInt(ConstsAndUtils.TAG_READY,0) == FLAG_ALREADY_LOADED_HIGH_RES)
                loadHigherResolution(); // load without waiting for shared element transition ends
        } else {
            // load after shared element transition ends
            final Transition windowTransition = getWindow().getSharedElementEnterTransition();
            windowTransition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {}
                @Override
                public void onTransitionEnd(Transition transition) {
                    loadHigherResolution();
                    windowTransition.removeListener(this);
                }
                @Override
                public void onTransitionCancel(Transition transition) {}
                @Override
                public void onTransitionPause(Transition transition) {}
                @Override
                public void onTransitionResume(Transition transition) {}
            });
        }

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



        final ScrollView scrollView = findViewById(R.id.scrollView);
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

    void loadHigherResolution(){
        final String fullSizeUrl = mArgs.getString(ConstsAndUtils.TAG_FULLSIZEURL);
        assert fullSizeUrl != null;
        if (fullSizeUrl.equals(""))return;
        mProgress.setVisibility(View.VISIBLE);
        AsyncFlickrImageRequest fullsizeImageRequest =
                new AsyncFlickrImageRequest(new AsyncFlickrImageRequest.OnAnswerListener() {
                        @Override
                        public void OnAnswerReady(Bitmap bitmap) {
                            if (bitmap!=null) {
                                mImageView.setImageBitmap(bitmap);
                            }else Snackbar.make(findViewById(R.id.constraint),
                                    "Picture download error", Snackbar.LENGTH_LONG).show();
                             mProgress.setVisibility(View.GONE);
                        }
                    }, fullSizeUrl, mCacheDir, 2048);
        fullsizeImageRequest.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
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
        outState.putInt(ConstsAndUtils.TAG_READY, FLAG_ALREADY_LOADED_HIGH_RES);
        super.onSaveInstanceState(outState);
    }


}
