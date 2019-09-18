package com.greatsokol.fluckr;

import android.animation.LayoutTransition;
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
import androidx.transition.TransitionListenerAdapter;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class ActivityView extends AppCompatActivity {

    ImageView mImageView;
    ProgressBar mProgress;
    Bundle mArgs;
    String mCacheDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_actionbar));
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
        Bitmap bmp = ImageLoader.loadPictureFromCache(
                            ImageLoader.convertUrlToCacheFileName(thumbnailPath, mCacheDir),false, 320);
        mImageView.setImageBitmap(bmp);

        setTitle(mArgs.getString(ConstsAndUtils.TAG_TITLE));

        String details = mArgs.getString(ConstsAndUtils.TAG_DETAILS);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ((TextView)findViewById(R.id.textviewDetails)).
                    setText(Html.fromHtml(details,Html.FROM_HTML_MODE_LEGACY));
        } else {
            ((TextView)findViewById(R.id.textviewDetails)).
                    setText(Html.fromHtml(details));
        }


        // run higher resolution picture
        if (savedInstanceState!=null){
            if(savedInstanceState.getInt(ConstsAndUtils.TAG_READY,0)==1){
                loadHigherResolution(); // load without waiting for shared element transition ends
            }
        } else {
            // load after shared element transition ends
            getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    loadHigherResolution();
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                }

                @Override
                public void onTransitionPause(Transition transition) {
                }

                @Override
                public void onTransitionResume(Transition transition) {
                }
            });
        }

    }

    private void setInsets() {
        findViewById(R.id.constraint).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        Toolbar toolbar = findViewById(R.id.toolbar_actionbar);
        final int toolbarHeight = toolbar.getLayoutParams().height;

        ViewCompat.setOnApplyWindowInsetsListener(toolbar, new OnApplyWindowInsetsListener(){
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {

                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                lp.setMargins(  insets.getSystemWindowInsetLeft(),
                                insets.getSystemWindowInsetTop(),
                                insets.getSystemWindowInsetRight(),
                                lp.bottomMargin);
                v.setLayoutParams(lp);
                return insets;
            }
        });



        ScrollView scrollView = findViewById(R.id.scrollView);
        ViewCompat.setOnApplyWindowInsetsListener(scrollView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                boolean land = ConstsAndUtils.isLandscape(getResources());
                v.setPadding(v.getPaddingLeft(),
                        land ? toolbarHeight + insets.getSystemWindowInsetTop() : v.getPaddingTop(),
                        land ? insets.getSystemWindowInsetRight() : v.getPaddingRight(),
                        land ? v.getPaddingBottom() : insets.getSystemWindowInsetBottom());
                return insets;
            }
        });
    }

    void loadHigherResolution(){
        Intent intent = getIntent();

        final String fullSizeUrl = mArgs.getString(ConstsAndUtils.TAG_FULLSIZEURL);
        AsyncFlickrImageRequest fullsizeImageRequest =
                new AsyncFlickrImageRequest(new AsyncFlickrImageRequest.OnAnswerListener() {
                        @Override
                        public void OnAnswerReady(Bitmap bitmap) {
                            mImageView.setImageBitmap(bitmap);
                            mProgress.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void OnError() {
                            Snackbar.make(findViewById(R.id.constraint),
                                    "Picture download error", Snackbar.LENGTH_LONG).show();
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
        outState.putInt(ConstsAndUtils.TAG_READY, 1);
        super.onSaveInstanceState(outState);
    }


}
