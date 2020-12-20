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
 * Represents a character geometric object
 *
 * @author Engineer
 */
public final class Character extends GeometricObject {

    private Point centroid = null;
    private String text = null;

    public Character(Rect bounds, String text, Point centroid) {
        this.bounds = bounds;
        this.text = text;
        this.centroid = new Point(centroid.x, centroid.y);
    }

    @Override
    public Point centroid() {
        return new Point(centroid.x, centroid.y);
    }

    public String toString() {
        return text;
    }

}
