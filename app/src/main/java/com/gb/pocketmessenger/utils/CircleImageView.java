package com.gb.pocketmessenger.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;


public class CircleImageView extends android.support.v7.widget.AppCompatImageView {


    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final float halfWidth = canvas.getWidth() / 2;
        final float halfHeight = canvas.getHeight() / 2;
        final float circleRadius = Math.min(halfHeight, halfWidth);
        final Path path = new Path();
        path.addCircle(halfWidth, halfHeight, circleRadius, Path.Direction.CCW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
