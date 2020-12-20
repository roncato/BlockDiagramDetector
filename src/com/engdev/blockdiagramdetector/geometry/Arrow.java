/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.geometry;

/**
 * Implements an Arrow object
 *
 * @author Lucas Batista
 */
public class Arrow extends GeometricObject {

    private PointerDirection pointerDirection = null;
    private Point startPoint = null;
    private Point endPoint = null;
    public Arrow(PointerDirection pointerDirection, Point startPoint, Point endPoint) {
        this.pointerDirection = pointerDirection;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    @Override
    public Point centroid() {
        return new Point((endPoint.x + startPoint.x) / 2, (endPoint.y + startPoint.y) / 2);
    }

    public PointerDirection getPointerDirection() {
        return pointerDirection;
    }

    public void setPointerDirection(PointerDirection pointerDirection) {
        this.pointerDirection = pointerDirection;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    @Override
    public String toString() {
        return "{Arrow:PointerDirection=" + pointerDirection + ", StartPoint=" + startPoint + ", EndPoint" + endPoint + "}";
    }

    public static enum PointerDirection {
        East,
        North,
        West,
        South
    }

}
