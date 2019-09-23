package com.greatsokol.fluckr;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;


class MyDragShadowBuilder extends View.DragShadowBuilder {

    MyDragShadowBuilder(View view) {
        super(view);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        View v = getView();
        v.draw(canvas);
    }


    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        View v = getView();
        int height = v.getHeight();
        int width = v.getWidth();
        shadowSize.set(width, height);
        shadowTouchPoint.set((width / 2), (height / 2));
    }
}

