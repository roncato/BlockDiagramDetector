/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.drawable;

import android.graphics.*;
import android.graphics.drawable.Drawable;

/**
 * Implements a drawable that draws lines.
 *
 * @author Lucas Batista
 */
public class LineDrawable extends Drawable {

    private Paint paint = null;

    public LineDrawable() {
        paint = new Paint();
    }

    @Override
    public void draw(Canvas canvas) {
        Rect rect = getBounds();
        canvas.drawLine(rect.left, rect.bottom, rect.right, rect.top, paint);
    }

    @Override
    public int getOpacity() {
        return Color.alpha(paint.getColor());
    }

    @Override
    public void setAlpha(int alpha) {
        int red = Color.red(paint.getColor());
        int green = Color.green(paint.getColor());
        int blue = Color.blue(paint.getColor());
        paint.setColor(Color.argb(alpha, red, green, blue));
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

}
