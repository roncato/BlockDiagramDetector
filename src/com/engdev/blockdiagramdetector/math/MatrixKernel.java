/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.math;


/**
 * Implements a convolution kernel that is supplied by the client.
 *
 * @author Lucas Batista
 */
public class MatrixKernel implements ConvolutionKernel {

    private double[] kernel = null;
    private int width = 0;
    private int height = 0;

    public MatrixKernel(double[] ds, int width, int height) {
        this.kernel = ds;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public double[] getKernel() {
        return kernel;
    }

}
