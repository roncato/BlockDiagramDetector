package com.engdev.blockdiagramdetector.math;

/**
 * @author Lucas Batista
 */
public abstract class SobelKernel implements ConvolutionKernel {

    public static final double[] KERNEL_X = {-1, 0, 1, -2, 0, 2, -1, 0, 1};

    public static final double[] KERNEL_Y = {-1, -2, -1, 0, 0, 0, 1, 2, 1};

    public static final int WIDTH = 3;

    public static final int HEIGHT = 3;

}
