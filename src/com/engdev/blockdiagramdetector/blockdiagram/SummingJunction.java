/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.blockdiagram;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

/**
 * A summing junction component
 *
 * @author Lucas Batista
 */
public class SummingJunction extends BlockDiagramComponent {

    public SummingJunction() {
        init();
    }

    private void init() {
        initDrawing();
    }

    private void initDrawing() {
        drawing = new ShapeDrawable(new OvalShape());
    }

    public void setColor(int color) {
        ((ShapeDrawable) drawing).getPaint().setColor(color);
    }

    @Override
    public void onResize() {
        // TODO onResize

    }

    @Override
    public void onMove() {
        // TODO onMove

    }

}
