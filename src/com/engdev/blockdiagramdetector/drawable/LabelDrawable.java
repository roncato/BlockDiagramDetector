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
 * Implements a text drawable
 *
 * @author Lucas Batista
 */
public class LabelDrawable extends Drawable {

    private final static float DEFAULT_TEXT_SIZE = 30;

    private String text = new String();
    private Paint paint = null;

    public LabelDrawable(String text) {
        this();
        this.text = text;
    }

    public LabelDrawable() {
        paint = new Paint();
        setTextSize(DEFAULT_TEXT_SIZE);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = this.getBounds();
        canvas.drawText(text, bounds.left, bounds.top, paint);
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getTextSize() {
        return paint.getTextSize();
    }

    public void setTextSize(float size) {
        paint.setTextSize(size);
    }

    public Paint getPaint() {
        return paint;
    }

}
