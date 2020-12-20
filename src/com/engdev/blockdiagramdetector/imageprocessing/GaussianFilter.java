/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.imageprocessing;

import android.graphics.Bitmap;
import com.engdev.blockdiagramdetector.math.Convolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Imaplements a Gaussian Filter.
 *
 * @author Lucas Batista
 */
public class GaussianFilter extends Filter {

    // Instance Variables
    private int width = 0;

    ;
    private int height = 0;
    private double[] Kernel = null;
    private int kernelWidth = 0;
    private int kernelHeight = 0;
    private KernelType kernelType = KernelType.Linear;

    public GaussianFilter(Bitmap sourceImage) {
        this.sourceImage = sourceImage;
        init();
    }

    // Static Methods
    public static float gaussian(float x, float sigma) {
        return (float) Math.exp(-Math.pow(x, 2) / (2F * Math.pow(sigma, 2))) /
                (float) Math.sqrt(2F * Math.PI * sigma);
    }

    public static float gaussian(float x, float y, float sigma) {
        return (float) Math.exp(-(Math.pow(x, 2) + Math.pow(y, 2)) / (2F * Math.pow(sigma, 2))) /
                (float) (2 * Math.PI * Math.pow(sigma, 2));
    }

    public static int getKernelWidth(float sigma, float threshold) {
        int halfWidth = (int) Math.round(Math.sqrt(-Math.log(threshold) * 2 * Math.pow(sigma, 2)));
        return 2 * halfWidth + 1;
    }

    public static double[] getMatrixGaussianKernel(float sigma, int kernelWidth) {
        int kernelSize = (int) Math.pow(kernelWidth, 2);
        double[] kernel = new double[(int) Math.pow(kernelWidth, 2)];

        for (int index = 0; index < kernelSize; index++) {
            int i = ImageUtility.getRow(index, kernelWidth);
            int j = ImageUtility.getColumn(index, kernelWidth);
            int x = (int) (j - Math.floor(kernelWidth / 2));
            int y = (int) (i - Math.floor(kernelWidth / 2));
            kernel[index] = gaussian(x, y, sigma);
        }

        return kernel;
    }

    public static double[] getMatrixGaussianKernel() {
        double[] kernel = {2, 4, 5, 4, 2, 4, 9, 12, 9, 4, 5, 12, 15, 12, 5, 4, 9, 12, 9, 4, 2, 4, 5, 4, 2};
        for (int i = 0; i < kernel.length; i++) {
            kernel[i] /= 159;
        }
        return kernel;
    }

    public static double[] getLinearKernel(float sigma, float threshold) {
        int kernelWidth = getKernelWidth(sigma, threshold);
        double[] aKernel = null;
        List<Float> kernel = new ArrayList<Float>(kernelWidth);

        int kWidth = 0;
        for (kWidth = 0; kWidth < kernelWidth; kWidth++) {
            float g = gaussian(kWidth, sigma);

            if (kWidth >= 2 && g <= threshold)
                break;

            kernel.add(g);
        }

        aKernel = new double[kWidth];

        for (int i = 0; i < aKernel.length; i++)
            aKernel[i] = kernel.get(i);

        return aKernel;
    }

    private void init() {
        initImage();
    }

    private void initImage() {
        width = sourceImage.getWidth();
        height = sourceImage.getHeight();
        pixels = new int[width * height];
        ImageUtility.fillPixels(pixels, sourceImage);
    }

    public void setKernel(double[] kernel, int kernelWidth, int kernelHeight, KernelType kernelType) {
        this.Kernel = kernel;
        this.kernelWidth = kernelWidth;
        this.kernelHeight = kernelHeight;
        this.kernelType = kernelType;
    }

    public int getKernelWidth() {
        return kernelWidth;
    }

    public int getKernelHeight() {
        return kernelHeight;
    }

    @Override
    public void apply() {
        switch (kernelType) {
            case Linear:
                pixels = Convolution.convolveImageLinear(pixels, width, height, Kernel);
                break;
            case Matrix:
                pixels = Convolution.convolveImage(pixels, width, height, Kernel, kernelWidth, kernelHeight);
                break;
        }
    }

    // Constants
    public enum KernelType {Matrix, Linear}

}
