/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.math;


import com.engdev.blockdiagramdetector.imageprocessing.ImageUtility;

/**
 * Exports methods for math convolution
 *
 * @author Lucas Batista
 */
public abstract class Convolution {

    public static double[] convolveMatrices(int[] matrix, int matrixWidth, int matrixHeight, double[] kernel, int kernelWidth, int kernelHeight) {
        double[] product = new double[matrixWidth * matrixHeight];
        int initX = kernelWidth;
        int maxX = matrixWidth - kernelWidth;
        int initY = kernelHeight;
        int maxY = matrixHeight - kernelHeight;

        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y++) {
                int index = y * matrixWidth + x;
                double sum = 0;
                for (int k = 0; k < kernel.length; k++) {
                    int i = ImageUtility.getRow(k, kernelWidth);
                    int j = ImageUtility.getColumn(k, kernelWidth);
                    int iIndex = index + j + matrixWidth * i;
                    sum += kernel[k] * matrix[iIndex];
                }
                product[index] = sum;
            }
        }
        return product;
    }

    public static double[] convolveLinear(int[] matrix, int matrixWidth, int matrixHeight, double[] kernel) {

        double[] product = new double[matrixWidth * matrixHeight];
        int initX = kernel.length - 1;
        int maxX = matrixWidth - kernel.length;
        int initY = kernel.length - 1;
        int maxY = matrixHeight - kernel.length;

        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y++) {

                int index = y * matrixWidth + x;

                double sumX = 0;
                double sumY = 0;

                for (int k = 0; k < kernel.length; k++) {
                    sumX += kernel[k] * (matrix[index - k] + matrix[index + k]);
                    sumY += kernel[k] * (matrix[index - k * matrixWidth] + matrix[index + k * matrixWidth]);
                }
                product[index] = sumX + sumY;
            }
        }
        return product;
    }

    public static int[] convolveImage(int[] matrix, int matrixWidth, int matrixHeight, double[] kernel, int kernelWidth, int kernelHeight) {
        int[] product = new int[matrixWidth * matrixHeight];
        int initX = kernelWidth;
        int maxX = matrixWidth - kernelWidth;
        int initY = kernelHeight;
        int maxY = matrixHeight - kernelHeight;

        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y++) {
                int index = y * matrixWidth + x;

                int a = 0;
                int r = 0;
                int g = 0;
                int b = 0;
                int weight = 0;

                for (int k = 0; k < kernel.length; k++) {
                    int i = ImageUtility.getRow(k, kernelWidth);
                    int j = ImageUtility.getColumn(k, kernelWidth);
                    int iIndex = index + j + matrixWidth * i;
                    int pixel = matrix[iIndex];
                    a += ImageUtility.alpha(pixel);
                    r += Math.round(kernel[k] * ImageUtility.red(pixel));
                    g += Math.round(kernel[k] * ImageUtility.green(pixel));
                    b += Math.round(kernel[k] * ImageUtility.blue(pixel));
                    weight += Math.round(kernel[k]);
                }

                if (weight == 0)
                    weight = 1;
                if (weight > 0) {
                    a = a / kernel.length;
                    r = r / weight;
                    g = g / weight;
                    b = b / weight;
                }

                product[index] = ImageUtility.clamp(a, r, g, b);
            }
        }
        return product;
    }

    public static int[] convolveImageLinear(int[] matrix, int matrixWidth, int matrixHeight, double[] kernel) {

        int[] product = new int[matrixWidth * matrixHeight];
        int initX = kernel.length - 1;
        int maxX = matrixWidth - kernel.length;
        int initY = kernel.length - 1;
        int maxY = matrixHeight - kernel.length;

        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y++) {

                int index = y * matrixWidth + x;

                int a = 0;
                int r = 0;
                int g = 0;
                int b = 0;
                int weight = 0;

                // X Direction
                for (int k = 0; k < kernel.length; k++) {
                    int iIndexX = index + k;
                    int pixelX = matrix[iIndexX];
                    a += ImageUtility.alpha(pixelX);
                    r += Math.round(kernel[k] * ImageUtility.red(pixelX));
                    g += Math.round(kernel[k] * ImageUtility.green(pixelX));
                    b += Math.round(kernel[k] * ImageUtility.blue(pixelX));
                    weight += Math.round(kernel[k]);
                }
                if (weight == 0)
                    weight = 1;
                if (weight > 0) {
                    a = a / kernel.length;
                    r = r / weight;
                    g = g / weight;
                    b = b / weight;
                }

                product[index] = ImageUtility.clamp(a, r, g, b);

                // Y Direction
                a = 0;
                r = 0;
                g = 0;
                b = 0;
                weight = 0;
                for (int k = 0; k < kernel.length; k++) {
                    int iIndexY = index + k * matrixWidth;
                    int pixelY = matrix[iIndexY];
                    a += ImageUtility.alpha(pixelY);
                    r += Math.round(kernel[k] * ImageUtility.red(pixelY));
                    g += Math.round(kernel[k] * ImageUtility.green(pixelY));
                    b += Math.round(kernel[k] * ImageUtility.blue(pixelY));
                    weight += Math.round(kernel[k]);
                }
                if (weight == 0)
                    weight = 1;
                if (weight > 0) {
                    a = a / kernel.length;
                    r = r / weight;
                    g = g / weight;
                    b = b / weight;
                }

            }
        }
        return product;
    }


}
