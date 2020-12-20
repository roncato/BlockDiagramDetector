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

/**
 * Perform point operations. Implementation is still under development
 *
 * @author Engineer
 */
public class PointOperationFilter extends Filter {

    private Operation operation = null;
    private Operation[] operations = null;
    private float multiplicationFactor = 0F;
    private float additionFactor = 0F;
    private float gammaFactor = 0F;
    private Channel channel = null;
    private int channelValue = 0;
    public PointOperationFilter(Bitmap sourceImage, Operation operation) {
        this.sourceImage = sourceImage;
        this.operation = operation;
    }
    public PointOperationFilter(Bitmap sourceImage, Operation[] operations) {
        this.sourceImage = sourceImage;
        this.operations = operations;
    }

    private void applyOperation() {
        pixels = new int[sourceImage.getWidth() * sourceImage.getHeight()];
        fillPixels();
        int color = 0;
        for (int index = 0; index < pixels.length; index++) {
            color = pixels[index];
            color = applyOperation(color, operation);
            pixels[index] = color;
        }
    }

    private void applyOperations() {
        pixels = new int[sourceImage.getWidth() * sourceImage.getHeight()];
        fillPixels();
        int color = 0;
        for (int index = 0; index < pixels.length; index++) {
            for (Operation operation : operations) {
                color = pixels[index];
                color = applyOperation(color, operation);
                pixels[index] = color;
            }
        }
    }

    private int applyOperation(int color, Operation op) {
        switch (operation) {
            case Multiply:
                return applyMultiply(color);
            case Addition:
                return applyAddition(color);
            case Invert:
                return applyInvert(color);
            case Gamma:
                return applyGamma(color);
            case Luminance:
                return applyLuminance(color);
            case Channel:
                return applyChannel(color);
            default:
                return -1;
        }
    }

    private int applyMultiply(int color) {
        return ImageUtility.multiply(color, multiplicationFactor);
    }

    private int applyAddition(int color) {
        return ImageUtility.add(color, additionFactor);
    }

    private int applyInvert(int color) {
        return ImageUtility.invert(color);
    }

    private int applyGamma(int color) {
        return ImageUtility.gamma(color, gammaFactor);
    }

    private int applyLuminance(int color) {
        return ImageUtility.luminance(color);
    }

    private int applyChannel(int color) {
        switch (channel) {
            case Alpha:
                return ImageUtility.setAlpha(color, channelValue);
            case Red:
                return ImageUtility.setRed(color, channelValue);
            case Green:
                return ImageUtility.setGreen(color, channelValue);
            case Blue:
                return ImageUtility.setBlue(color, channelValue);
            default:
                return -1;
        }
    }

    @Override
    protected void apply() {
        if (operations == null)
            applyOperation();
        else
            applyOperations();
    }

    public float getMultiplyFactor() {
        return multiplicationFactor;
    }

    public void setMultiplyFactor(float multiplyFactor) {
        this.multiplicationFactor = multiplyFactor;
    }

    public float getAdditionFactor() {
        return additionFactor;
    }

    public void setAdditionFactor(float additionFactor) {
        this.additionFactor = additionFactor;
    }

    public float getGammaFactor() {
        return gammaFactor;
    }

    public void setGammaFactor(float gammaFactor) {
        this.gammaFactor = gammaFactor;
    }

    public void setChannel(Channel channel, int value) {
        this.channel = channel;
        this.channelValue = value;
    }

    public Channel getChannel() {
        return channel;
    }

    public int getChannelValue() {
        return channelValue;
    }

    public static enum Operation {
        Multiply,
        Addition,
        Invert,
        Gamma,
        Luminance,
        AutoContrast,
        Absolute,
        Log,
        Max,
        Min,
        Square,
        SquareRoot,
        Channel
    }

    public static enum Channel {
        Alpha,
        Red,
        Green,
        Blue
    }


}
