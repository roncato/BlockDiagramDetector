package com.engdev.blockdiagramdetector.geometry;

import android.graphics.Rect;

public class Bar extends GeometricObject {

    public Bar(Rect bounds) {
        this.bounds = bounds;
    }

    @Override
    public Point centroid() {
        return new Point(bounds.left + bounds.width() / 2, bounds.top + bounds.height() / 2);
    }

}
