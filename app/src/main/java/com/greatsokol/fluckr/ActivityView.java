package com.greatsokol.fluckr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;

import java.util.Objects;

public class ActivityView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_actionbar_view));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


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
}
