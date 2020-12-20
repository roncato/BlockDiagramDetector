/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.geometry;

import android.graphics.Rect;

/**
 * Represents a polygon
 *
 * @author Lucas Batista
 */
public final class Rectangle extends GeometricObject {

    public Rectangle(Rect bounds) {
        this.bounds = bounds;
    }

    public float area() {
        return bounds.width() * bounds.height();
    }

    public float perimeter() {
        return bounds.width() + bounds.height();
    }

    @Override
    public Point centroid() {
        return new Point(bounds.left + bounds.width() / 2, bounds.top + bounds.height() / 2);
    }

}
