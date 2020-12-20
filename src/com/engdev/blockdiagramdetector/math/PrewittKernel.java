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
 * Implements a Prewitt Kernel
 *
 * @author Lucas Batista
 */
public abstract class PrewittKernel implements ConvolutionKernel {

    public static final double[] KERNEL_X = {-1, 0, 1, -1, 0, 1, -1, 0, 1};

    public static final double[] KERNEL_Y = {1, 1, 1, 0, 0, 0, -1, -1, -1};

    public static final int WIDTH = 3;

    public static final int HEIGHT = 3;

}
