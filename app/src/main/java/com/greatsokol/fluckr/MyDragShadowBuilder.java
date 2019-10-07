package com.greatsokol.fluckr;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;


public class MyDragShadowBuilder extends View.DragShadowBuilder {

    private int mX, mY;

    MyDragShadowBuilder(View view, float touchPointX, float touchPointY) {
        super(view);
        mX = (int)touchPointX;
        mY = (int)touchPointY;
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
        shadowTouchPoint.set(mX, mY);//(width / 2), (height / 2));
    }
}

