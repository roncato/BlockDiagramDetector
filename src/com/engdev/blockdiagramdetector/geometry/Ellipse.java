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
 * Represents a ellipse geometric object
 *
 * @author Engineer
 */
public final class Ellipse extends GeometricObject {

    public Ellipse(Rect bounds) {
        this.bounds = bounds;
    }

    public float area() {
        return (float) (Math.PI * tranverseDiameter() * conjugateDiameter() / 4);
    }

    public float perimeter() {
        float stdi = tranverseDiameter() / 2;
        float scdi = conjugateDiameter() / 2;
        return (float) (Math.PI * (stdi + scdi) / 2 *
                (1 + (3 * Math.pow((stdi - scdi) / (stdi + scdi), 2) /
                        (10 + Math.sqrt(4 - 3 * Math.pow((stdi - scdi) / (stdi + scdi), 2))))));
    }

    @Override
    public Point centroid() {
        return new Point(bounds.left + bounds.width() / 2, bounds.top + bounds.height() / 2);
    }

    public float conjugateDiameter() {
        return bounds.width();
    }

    public float tranverseDiameter() {
        return bounds.height();
    }

    public float eccentricity() {
        return (float) Math.sqrt(1 - Math.pow(conjugateDiameter() / tranverseDiameter(), 2));
    }

}
