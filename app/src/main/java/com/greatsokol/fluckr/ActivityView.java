package com.greatsokol.fluckr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class ActivityView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_actionbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setInsets();

        ImageView imageView = findViewById(R.id.imageViewBig);
        Intent intent = getIntent();
        ViewCompat.setTransitionName(imageView, intent.getStringExtra(Consts.TAG_TR_NAME));

        Bundle args = intent.getBundleExtra(Consts.TAG_ARGS);
        assert args != null;
        String cacheFileName = args.getString(Consts.TAG_PATH);
        Bitmap bmp = ImageLoader.loadPictureFromCache(cacheFileName, false);
        imageView.setImageBitmap(bmp);

        setTitle(args.getString(Consts.TAG_TITLE));

        String details = args.getString(Consts.TAG_DETAILS);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ((TextView)findViewById(R.id.textviewDetails)).
                    setText(Html.fromHtml(details,Html.FROM_HTML_MODE_LEGACY));
        } else {
            ((TextView)findViewById(R.id.textviewDetails)).
                    setText(Html.fromHtml(details));
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
                lp.setMargins(  lp.leftMargin,
                        insets.getSystemWindowInsetTop(),
                        lp.rightMargin,
                        lp.bottomMargin);
                v.setLayoutParams(lp);
                return insets;
            }
        });



        ScrollView scrollView = findViewById(R.id.scrollView);
        ViewCompat.setOnApplyWindowInsetsListener(scrollView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                v.setPadding(v.getPaddingLeft(),
                        isLandscape() ? toolbarHeight + insets.getSystemWindowInsetTop() : v.getPaddingTop(),
                        isLandscape() ? insets.getSystemWindowInsetRight() : v.getPaddingRight(),
                        isLandscape() ? v.getPaddingBottom() : insets.getSystemWindowInsetBottom());
                return insets;
            }
        });
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



    public boolean isLandscape(){
        final int orientation = getResources().getConfiguration().orientation;
        return orientation == Surface.ROTATION_180 ||
               orientation == Surface.ROTATION_270;
    }
}
