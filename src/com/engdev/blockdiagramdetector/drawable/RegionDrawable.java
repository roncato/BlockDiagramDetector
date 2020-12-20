/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import com.engdev.blockdiagramdetector.geometry.Point;
import com.engdev.blockdiagramdetector.geometry.Region;

import java.util.Set;

/**
 * Decorator of an region. It does not respect the bounds.
 *
 * @author Lucas Batista
 */
public class RegionDrawable extends Drawable {

    private Paint paint = null;
    private Region region = null;

    public RegionDrawable(Region region) {
        this.region = region;
        this.paint = new Paint();
    }

    @Override
    public void draw(Canvas arg0) {
        Set<Point> points = region.getPoints();
        for (Point point : points)
            arg0.drawPoint(point.x, point.y, paint);
    }

    @Override
    public int getOpacity() {
        return paint.getAlpha();
    }

    @Override
    public void setAlpha(int arg0) {
        paint.setAlpha(arg0);
    }

    @Override
    public void setColorFilter(ColorFilter arg0) {
        paint.setColorFilter(arg0);
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

}
