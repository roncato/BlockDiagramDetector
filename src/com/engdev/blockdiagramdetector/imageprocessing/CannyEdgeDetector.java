/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.imageprocessing;

import android.annotation.SuppressLint;
import android.graphics.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Implements a CannyEdgeDetector filter.
 *
 * @author Lucas Batista
 */
public class CannyEdgeDetector extends Filter {

    // Constants
    private final static float GAUSSIAN_CUT_OFF = 0.005F;
    private final static float MAGNITUDE_SCALE = 100F;
    private final static float MAGNITUDE_LIMIT = 1000F;
    private final static int MAGNITUDE_MAX = (int) (MAGNITUDE_SCALE * MAGNITUDE_LIMIT);

    // Parameters
    private float lowThreshold = 2.5F;
    private float highThreshold = 7.5F;
    private float sigma = 1.5F;
    private float gaussianThreshold = 0.1F;
    private boolean contrastNormalized = false;

    // Matrices
    private int[] magnitude = null;
    private int[] gradDirection = null;
    private float[] xConv = null;
    private float[] yConv = null;
    private float[] xGradient = null;
    private float[] yGradient = null;

    // Image parameters
    private int width = 0;
    private int height = 0;
    private int imageSize = 0;

    public CannyEdgeDetector(Bitmap sourceImage) {
        if (sourceImage == null)
            throw new IllegalArgumentException();
        this.sourceImage = sourceImage;
        init();
    }

    public static void writeFile(File file, float[] signal) throws IOException {
        FileWriter fstream = new FileWriter(file);
        BufferedWriter out = new BufferedWriter(fstream);
        for (int i = 0; i < signal.length; i++) {
            String line = Integer.toString(i) + "\t" + Float.toString(signal[i]);
            out.write(line);
            out.newLine();
        }
        out.close();
    }

    private void init() {
        initImage();
    }

    private void initImage() {
        width = sourceImage.getWidth();
        height = sourceImage.getHeight();
        imageSize = width * height;
    }

    private void initArrays() {

        pixels = new int[imageSize];
        magnitude = new int[imageSize];
        gradDirection = new int[imageSize];

        xConv = new float[imageSize];
        yConv = new float[imageSize];
        xGradient = new float[imageSize];
        yGradient = new float[imageSize];

    }

    public void apply() {

        int low = 0;
        int high = 0;

        if (sourceImage == null)
            return;

        // Initiates Data
        initArrays();

        // Reads pixels array
        fillPixels();

        // Reads Luminance into pixels
        readLuminance();

        if (contrastNormalized)
            normalizeContrast();

        // Computes the grandients
        computeGradients(sigma, gaussianThreshold);


        // Performs Hyteresis
        low = Math.round(lowThreshold * MAGNITUDE_SCALE);
        high = Math.round(highThreshold * MAGNITUDE_SCALE);
        performHysteresis(low, high);

        // Makes image a BLOB
        thresholdEdges();

    }

    private void computeGradients(float kernelRadius, float gaussianThreshold) {

        // 1. Noise Filter
        int kernelSize = getMaskSize(gaussianThreshold, sigma);
        int kwidth = 0;

        // Creates the two Gaussian convolution masks.
        float kernel[] = new float[kernelSize];
        float diffKernel[] = new float[kernelSize];

        for (kwidth = 0; kwidth < kernelSize; kwidth++) {
            float g1 = gaussian(kwidth, kernelRadius);

            if (g1 <= GAUSSIAN_CUT_OFF && kwidth >= 2)
                break;

            float g2 = gaussian(kwidth - 0.5F, kernelRadius);
            float g3 = gaussian(kwidth + 0.5F, kernelRadius);

            kernel[kwidth] = (g1 + g2 + g3) / 3F;
            diffKernel[kwidth] = (g3 - g2) / 1F;
        }

        // Sets boundaries
        int initX = kwidth - 1;
        int maxX = width - kwidth;
        int initY = kwidth - 1;
        int maxY = height - kwidth;

        // Finds masks in x and y direction. Calculation is performed as follows
		/*
			xOffset = k;
			yOffset = k * width;

			xLeft = index - xOffset;
			xRight = index + xOffset;

			yTop = index - yOffset;
			yBottom = index + yOffset;
			xLeftColumn = getColumn(xLeft);
			xRightColumn = getColumn(xRight);

			yTopRow = getRow(yTop);
			yBottomRow = getRow(yBottom);
		 */

        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y++) {

                int index = y * width + x;

                int sumX = 0;
                int sumY = 0;

                for (int k = 0; k < kwidth; k++) {
                    sumX += kernel[k] * (pixels[index - k] + pixels[index + k]);
                    sumY += kernel[k] * (pixels[index - k * width] + pixels[index + k * width]);
                }

                xConv[index] = sumX;
                yConv[index] = sumY;

            }
        }

        // 2. Find the intensity gradient of the image

        // a. Applies the pair of convolution masks in x and y directions
        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y++) {
                int index = y * width + x;
                float sum = 0;
                for (int k = 0; k < kwidth; k++)
                    sum += diffKernel[k] * (yConv[index - k] - yConv[index + k]);
                xGradient[index] = sum;
            }
        }

        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y++) {
                float sum = 0;
                int index = y * width + x;
                int yOffset = 0;
                for (int k = 0; k < kwidth; k++) {
                    yOffset = k * width;
                    sum += diffKernel[k] * (xConv[index - yOffset] - xConv[index + yOffset]);
                }
                yGradient[index] = sum;
            }
        }

        initX = kwidth;
        maxX = width - kwidth - 1;
        initY = kwidth;
        maxY = height - kwidth - 1;
        // b. Finds the gradient strength and direction
        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y++) {

                // Locations of surrounding pixels
                int index = x + y * width;
                int indexN = index - width;
                int indexS = index + width;
                int indexW = index - 1;
                int indexE = index + 1;
                int indexNW = indexN - 1;
                int indexNE = indexN + 1;
                int indexSW = indexS - 1;
                int indexSE = indexS + 1;

                // Gets Gx and Gy and calculate G
                float xGrad = xGradient[index];
                float yGrad = yGradient[index];
                float gradMag = hypot(xGrad, yGrad);

                // Calculates Gradient direction
                int direction = (int) Math.round(Math.atan2(yGrad, xGrad));
                direction = direction < 45 / 2 ? 0 : direction < (90 + 45) / 2 ? 45 : direction < (90 + 135) / 2 ? 90 : 135;
                gradDirection[index] = direction;

                // Perform non-maximal supression
                float nMag = hypot(xGradient[indexN], yGradient[indexN]);
                float sMag = hypot(xGradient[indexS], yGradient[indexS]);
                float wMag = hypot(xGradient[indexW], yGradient[indexW]);
                float eMag = hypot(xGradient[indexE], yGradient[indexE]);
                float neMag = hypot(xGradient[indexNE], yGradient[indexNE]);
                float seMag = hypot(xGradient[indexSE], yGradient[indexSE]);
                float swMag = hypot(xGradient[indexSW], yGradient[indexSW]);
                float nwMag = hypot(xGradient[indexNW], yGradient[indexNW]);
                float tmp = 0;

                /*
                 * An explanation of what's happening here, for those who want
                 * to understand the source: This performs the "non-maximal
                 * supression" phase of the Canny edge detection in which we
                 * need to compare the gradient magnitude to that in the
                 * direction of the gradient; only if the value is a local
                 * maximum do we consider the point as an edge candidate.
                 *
                 * We need to break the comparison into a number of different
                 * cases depending on the gradient direction so that the
                 * appropriate values can be used. To avoid computing the
                 * gradient direction, we use two simple comparisons: first we
                 * check that the partial derivatives have the same sign (1)
                 * and then we check which is larger (2). As a consequence, we
                 * have reduced the problem to one of four identical cases that
                 * each test the central gradient magnitude against the values at
                 * two points with 'identical support'; what this means is that
                 * the geometry required to accurately interpolate the magnitude
                 * of gradient function at those points has an identical
                 * geometry (upto right-angled-rotation/reflection).
                 *
                 * When comparing the central gradient to the two interpolated
                 * values, we avoid performing any divisions by multiplying both
                 * sides of each inequality by the greater of the two partial
                 * derivatives. The common comparand is stored in a temporary
                 * variable (3) and reused in the mirror case (4).
                 *
                 */

                if (xGrad * yGrad <= (float) 0 /*(1)*/
                        ? Math.abs(xGrad) >= Math.abs(yGrad) /*(2)*/
                        ? (tmp = Math.abs(xGrad * gradMag)) >= Math.abs(yGrad * neMag - (xGrad + yGrad) * eMag) /*(3)*/
                        && tmp > Math.abs(yGrad * swMag - (xGrad + yGrad) * wMag) /*(4)*/
                        : (tmp = Math.abs(yGrad * gradMag)) >= Math.abs(xGrad * neMag - (yGrad + xGrad) * nMag) /*(3)*/
                        && tmp > Math.abs(xGrad * swMag - (yGrad + xGrad) * sMag) /*(4)*/
                        : Math.abs(xGrad) >= Math.abs(yGrad) /*(2)*/
                        ? (tmp = Math.abs(xGrad * gradMag)) >= Math.abs(yGrad * seMag + (xGrad - yGrad) * eMag) /*(3)*/
                        && tmp > Math.abs(yGrad * nwMag + (xGrad - yGrad) * wMag) /*(4)*/
                        : (tmp = Math.abs(yGrad * gradMag)) >= Math.abs(xGrad * seMag + (yGrad - xGrad) * sMag) /*(3)*/
                        && tmp > Math.abs(xGrad * nwMag + (yGrad - xGrad) * nMag) /*(4)*/
                )
                    magnitude[index] = gradMag >= MAGNITUDE_LIMIT ? MAGNITUDE_MAX : (int) (MAGNITUDE_SCALE * gradMag);
            }
        }
    }

    private void performHysteresis(int low, int high) {

        Arrays.fill(pixels, 0);

        int index = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                index = x + y * width;
                if (magnitude[index] >= high) {
                    pixels[index] = magnitude[index];
                    markNeighbors(x, y, low);
                }
            }
        }
    }

    private void markNeighbors(int x, int y, int threshold) {

        if (x > 0 && x < width - 1 && y > 0 && y < height - 1) {

            // Edges
            markMagnitude(x + 1, y, threshold);
            markMagnitude(x, y - 1, threshold);
            markMagnitude(x - 1, y, threshold);
            markMagnitude(x, y + 1, threshold);

            //Vertices
            markMagnitude(x + 1, y - 1, threshold);
            markMagnitude(x - 1, y - 1, threshold);
            markMagnitude(x - 1, y + 1, threshold);
            markMagnitude(x + 1, y + 1, threshold);

        }

    }

    private void markMagnitude(int x, int y, int threshold) {
        int index = getIndex(x, y);
        if (magnitude[index] >= threshold)
            pixels[index] = magnitude[index];
    }

    private void thresholdEdges() {
        for (int i = 0; i < imageSize; i++)
            pixels[i] = pixels[i] > 0 ? -1 : 0xFF000000;
    }

    private int luminance(float red, float green, float blue) {
        return Math.round(0.299F * red + 0.587F * green + 0.114F * blue);
    }

    private int getMaskSize(float threshold, float sigma) {
        int halfWidth = (int) Math.round(Math.sqrt(-Math.log(threshold) * 2 * Math.pow(sigma, 2)));
        return 2 * halfWidth + 1;
    }

    private void readLuminance() {
        for (int i = 0; i < imageSize; i++) {
            int pixel = pixels[i];
            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);
            pixels[i] = luminance(red, green, blue);
        }
    }

    private void normalizeContrast() {
        int[] histogram = new int[256];
        for (int i = 0; i < pixels.length; i++)
            histogram[pixels[i]]++;
        int[] remap = new int[256];
        int sum = 0;
        int j = 0;
        for (int i = 0; i < histogram.length; i++) {
            sum += histogram[i];
            int target = sum * 255 / imageSize;
            for (int k = j + 1; k <= target; k++)
                remap[k] = i;
            j = target;
        }

        for (int i = 0; i < pixels.length; i++)
            pixels[i] = remap[pixels[i]];
    }

    public int[] getProcessedPixels() {
        return pixels;
    }

    private float hypot(float x, float y) {
        return (float) Math.hypot(x, y);
    }

    public float gaussian(float x, float sigma) {
        return (float) Math.exp(-Math.pow(x, 2) / (2F * Math.pow(sigma, 2))) /
                (float) Math.sqrt(2F * Math.PI * sigma);
    }

    public float gaussian(float x, float y, float sigma) {
        return (float) Math.exp(-(Math.pow(x, 2) + Math.pow(y, 2)) / (2F * Math.pow(sigma, 2))) /
                (float) (2 * Math.PI * Math.pow(sigma, 2));
    }

    final public int getRow(int pixelIndex) {
        return (int) Math.floor(pixelIndex / sourceImage.getWidth());
    }

    final public int getColumn(int pixelIndex) {
        return pixelIndex - (sourceImage.getWidth() * getRow(pixelIndex));
    }

    final public int[][] toMatrix(int[] pixels) {
        int[][] matrix = new int[sourceImage.getHeight()][sourceImage.getWidth()];
        for (int i = 0; i < pixels.length; i++) {
            matrix[getRow(i)][getColumn(i)] = pixels[i];
        }
        return matrix;
    }

    private void fillPixels(Bitmap image, int[] pixels) {
        image.getPixels(pixels, 0, image.getWidth(), 1, 1, image.getWidth() - 1, image.getHeight() - 1);
    }

    @SuppressLint("NewApi")
    public final EdgeMap createEdgeMap() {
        int[] originalPixels = new int[imageSize];
        fillPixels(sourceImage, originalPixels);
        EdgeMap em = new EdgeMap(Arrays.copyOf(pixels, pixels.length), width, height);
        return em;
    }

    public int[] getPixels() {
        return pixels;
    }

    public Bitmap getSourceImage() {
        return sourceImage;
    }

    public void setSourceImage(Bitmap sourceImage) {
        this.sourceImage = sourceImage;
        initImage();
    }

    public Bitmap createEdgeImage() {
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    public float getLowThreshold() {
        return lowThreshold;
    }

    public void setLowThreshold(float lowThreshold) {
        if (lowThreshold < 0)
            throw new IllegalArgumentException("Low Threshold cannot be negative.");
        this.lowThreshold = lowThreshold;
    }

    public int getIndex(int x, int y) {
        return x + y * width;
    }

    public float getHighThreshold() {
        return highThreshold;
    }

    public void setHighThreshold(float highThreshold) {
        if (lowThreshold < 0)
            throw new IllegalArgumentException("Low Threshold cannot be negative.");
        this.highThreshold = highThreshold;
    }

    public float getSigma() {
        return sigma;
    }

    public void setSigma(float sigma) {
        this.sigma = sigma;
    }

    public float getGaussianThreshold() {
        return this.gaussianThreshold;
    }

    public void setGaussianThreshold(float gaussianThreshold) {
        this.gaussianThreshold = gaussianThreshold;
    }

    public boolean isContrastNormalized() {
        return contrastNormalized;
    }

    public void setContrastNormalized(boolean contrastNormalized) {
        this.contrastNormalized = contrastNormalized;
    }

}
