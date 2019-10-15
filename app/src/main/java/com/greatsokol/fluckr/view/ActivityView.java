package com.greatsokol.fluckr.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.greatsokol.fluckr.R;
import com.greatsokol.fluckr.etc.ConstsAndUtils;
import com.greatsokol.fluckr.etc.ThumbnailTransformation;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Objects;

public class ActivityView extends AppCompatActivity {
    private View mRootView;
    private ZoomageView mImageView;
    private ProgressBar mProgress;
    private Toolbar mToolbar;
    private Bundle mArgs;
    private Bitmap mThumbnail;
    private final static int FLAG_ALREADY_LOADED_HIGH_RES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        postponeEnterTransition();
        mToolbar = findViewById(R.id.toolbar_actionbar);
        mRootView = findViewById(R.id.constraint);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setInsets();

        mImageView = findViewById(R.id.imageViewBig);
        mProgress = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        ViewCompat.setTransitionName(mImageView, intent.getStringExtra(ConstsAndUtils.TAG_TR_NAME));

        mArgs = intent.getBundleExtra(ConstsAndUtils.TAG_ARGS);
        assert mArgs != null;
        setTextLabels(mArgs);

        final String thumbnailPath = mArgs.getString(ConstsAndUtils.TAG_THUMBURL);
        assert thumbnailPath != null;

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mThumbnail = bitmap;
                mImageView.setImageBitmap(mThumbnail);
                startPostponedEnterTransition();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                startPostponedEnterTransition();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        mImageView.setTag(target); // making strong reference
        int size = ConstsAndUtils.pxFromDp(getResources(), 300);
        Picasso.get().load(thumbnailPath).transform(new ThumbnailTransformation(size, size)).into(target);

        // run higher resolution picture
        if (savedInstanceState != null) {
            if (savedInstanceState.getInt(ConstsAndUtils.TAG_READY, 0) == FLAG_ALREADY_LOADED_HIGH_RES)
                loadHigherResolution(); // load without waiting for shared element transition ends
        } else {
            // load after shared element transition ends
            final Transition windowTransition = getWindow().getSharedElementEnterTransition();
            windowTransition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {}
                @Override
                public void onTransitionCancel(Transition transition) {
                    loadHigherResolution();
                    windowTransition.removeListener(this);
                }
                @Override
                public void onTransitionPause(Transition transition) {}
                @Override
                public void onTransitionResume(Transition transition) {}
                @Override
                public void onTransitionEnd(Transition transition) {
                    loadHigherResolution();
                    windowTransition.removeListener(this);
                }
            });
        }

        /*
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP: {
                        view.performClick();
                        return false;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        ClipData dragData = ClipData.newPlainText("dfsdfsdf","");
                        view.startDrag(dragData,
                                new MyDragShadowBuilder(view,
                                        motionEvent.getX(),
                                        motionEvent.getY()),
                                    view,0);
                        return false;
                    }
                }
                return true;
            }
        });


        mImageView.setOnDragListener(new View.OnDragListener() {
            private int mStartY;
            private int mStartX;
            private final float coeff = 1.5f;
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                final int action = dragEvent.getAction();
                final float height = view.getHeight();
                final float width = view.getWidth();
                final int deltaY = Math.abs((int)(mStartY-dragEvent.getY()));
                final int deltaX = Math.abs((int)(mStartX-dragEvent.getX()));
                switch(action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        view.setVisibility(View.INVISIBLE);
                        mStartY = (int)dragEvent.getY();
                        mStartX = (int)dragEvent.getX();
                        return true;

                    case DragEvent.ACTION_DRAG_LOCATION:

                        float deltaY_coeff = coeff * deltaY;
                        deltaY_coeff = deltaY_coeff > height ? height : deltaY_coeff;
                        float alphaY = 1f - 1f * (deltaY_coeff / height);

                        float deltaX_coeff = coeff * deltaX;
                        deltaX_coeff = deltaX_coeff > height ? height : deltaX_coeff;
                        float alphaX = 1f - 1f * (deltaX_coeff / width);


                        mRootView.setAlpha(alphaY < alphaX? alphaY : alphaX);
                        return true;

                    case DragEvent.ACTION_DRAG_ENTERED:
                    case DragEvent.ACTION_DRAG_EXITED:
                    case DragEvent.ACTION_DROP:
                        return false;

                    case DragEvent.ACTION_DRAG_ENDED:
                        view.setVisibility(View.VISIBLE);
                        mRootView.setAlpha(1f);
                        int thresholdX = (int)(width * 0.5f);
                        int thresholdY = (int)(height * 0.5f);
                        if(deltaY > thresholdY || deltaX > thresholdX)
                            finishAfterTransition();
                        return true;
                }

                return true;
            }
        });

        */
    }


    private void setTextLabels(Bundle params){
        final String title = params.getString(ConstsAndUtils.TAG_TITLE);
        setTitle(title);

        String details = params.getString(ConstsAndUtils.TAG_DETAILS);
        assert details != null;
        if (details.trim().equals("")) details = title;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ((TextView) findViewById(R.id.textviewDetails)).
                    setText(Html.fromHtml(details, Html.FROM_HTML_MODE_LEGACY));
        } else {
            ((TextView) findViewById(R.id.textviewDetails)).
                    setText(Html.fromHtml(details));
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


    @Override
    public void finishAfterTransition() {
        mImageView.setImageBitmap(mThumbnail);
        mImageView.reset(false);
        mImageView.setImageMatrix(mImageView.getMatrix()); //trick

        setResult(0);
        super.finishAfterTransition();
    }


    void loadHigherResolution(){//final boolean animate){
        String fullSizeUrl = mArgs.getString(ConstsAndUtils.TAG_FULLSIZEURL);
        if(fullSizeUrl==null || fullSizeUrl.isEmpty())return;

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                //if(animate)
                  //  ImageViewAnimatedChange(mImageView, bitmap);
                //else
                    mImageView.setImageBitmap(bitmap);
                mProgress.setVisibility(View.GONE);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                String message = e.getMessage();
                if(message != null && !message.isEmpty())
                    Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
                mProgress.setVisibility(View.GONE);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                mProgress.setVisibility(View.VISIBLE);
            }
        };
        mImageView.setTag(target); // making strong reference
        Picasso.get().load(fullSizeUrl).into(target);
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

    /*private static void ImageViewAnimatedChange(final ImageView v, final Bitmap newImage) {
        final Animation animOut = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_out);
        final Animation animIn  = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(newImage);
                v.startAnimation(animIn);
            }
        });
        v.startAnimation(animOut);
    }*/

}
