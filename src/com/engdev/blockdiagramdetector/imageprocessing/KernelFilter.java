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
import com.engdev.blockdiagramdetector.math.ConvolutionKernel;

/**
 * Implements a supplied kernel filter.
 *
 * @author Lucas Batista
 */
public class KernelFilter extends Filter {

    private ConvolutionKernel kernel = null;
    private int width = 0;
    private int height = 0;

    public KernelFilter(Bitmap sourceImage, ConvolutionKernel kernel) {
        this.sourceImage = sourceImage;
        this.kernel = kernel;
        init();
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

    @Override
    protected void apply() {
        pixels = new int[width * height];
        fillPixels();
        pixels = Convolution.convolveImage(pixels, width, height, kernel.getKernel(), kernel.getWidth(), kernel.getHeight());
    }

}
