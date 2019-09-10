package com.greatsokol.fluckr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

public class ActivityView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);


        ImageView imageView = findViewById(R.id.imageViewBig);
        Intent intent = getIntent();
        ViewCompat.setTransitionName(imageView, intent.getStringExtra(Consts.TAG_TR_NAME));

        Bundle args = intent.getBundleExtra(Consts.TAG_ARGS);
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
}
