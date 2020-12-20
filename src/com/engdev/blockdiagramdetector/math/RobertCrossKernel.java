package com.engdev.blockdiagramdetector.math;


/**
 * @author Lucas Batista
 */
public abstract class RobertCrossKernel implements ConvolutionKernel {

    public static final double[] KERNEL_X = {1, 0, 0, -1};

    public static final double[] KERNEL_Y = {0, 1, -1, 0};

    public static final int WIDTH = 2;

    public static final int HEIGHT = 2;

}
