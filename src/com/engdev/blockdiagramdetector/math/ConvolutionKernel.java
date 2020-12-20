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
 * Interface for a convolution kernel
 *
 * @author Lucas Batista
 */
public interface ConvolutionKernel {

    public int getWidth();

    ;

    public int getHeight();

    public double[] getKernel();

    public enum KernelDirection {x, y, z}

}
