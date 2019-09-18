package com.greatsokol.fluckr;

import android.content.res.Resources;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

abstract class ConstsAndUtils {
    final static String TAG_ARGS = "ARGS";
    final static String TAG_TR_POSITION = "TR_POSITION";
    final static String TAG_TR_NAME = "TR_NAME";
    final static String TAG_TITLE = "TITLE";
    final static String TAG_DETAILS = "DETAILS";
    final static String TAG_THUMBURL = "THUMBURL";
    final static String TAG_FULLSIZEURL = "FULLSIZEURL";
    final static String TAG_VIEWASGRID = "VIEWASGRID";
    final static String TAG_READY = "READY";


    static boolean isLandscape(Resources res){
        final int orientation = res.getConfiguration().orientation;
        return orientation == ORIENTATION_LANDSCAPE;
    }

    static int pxFromDp(Resources res, float dp) {
        return (int)(dp * res.getDisplayMetrics().density);
    }
}
