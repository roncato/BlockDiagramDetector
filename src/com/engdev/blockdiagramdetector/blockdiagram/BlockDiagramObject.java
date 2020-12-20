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
import com.engdev.blockdiagramdetector.geometry.Point;

/**
 * Interface that defines a block diagram object.
 *
 * @author Lucas Batista
 */
public interface BlockDiagramObject {

    public void draw(Canvas canvas);

    public int getX();

    public void setX(int x);

    public int getY();

    public void setY(int y);

    public int getHeight();

    public int getWidth();

    public Point getPoint();

}
