/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.blockdiagram;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import com.engdev.blockdiagramdetector.geometry.Point;

/**
 * Block diagram component
 *
 * @author Lucas Batista
 */
public abstract class BlockDiagramComponent implements BlockDiagramObject {
    protected Drawable drawing = null;

    protected Point point = new Point();
    protected int width = 0;
    protected int height = 0;

    @Override
    public void draw(Canvas canvas) {
        drawing.draw(canvas);
    }

    public int getX() {
        return point.x;
    }

    public void setX(int x) {
        point.x = x;
        drawing.setBounds(x, point.y, x + width, point.y + height);
        onMove();
    }

    public int getY() {
        return point.y;
    }

    public void setY(int y) {
        point.y = y;
        drawing.setBounds(point.x, y, point.x + width, y + height);
        onMove();
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        drawing.setBounds(point.x, point.y, point.x + width, point.y + height);
        onResize();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        drawing.setBounds(point.x, point.y, point.x + width, point.y + height);
        onResize();
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Drawable getDrawing() {
        return drawing;
    }

    public abstract void onResize();

    public abstract void onMove();

    public void invalidate() {

    }
}