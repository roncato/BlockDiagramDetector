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

import java.util.Arrays;

/**
 * Implements an EdgeMap ADT.
 *
 * @author Lucas Batista
 */
public class EdgeMap {

    private int[] pixels = null;
    private int width = 0;
    private int height = 0;
    private int blackColor = 0xFF000000;

    public EdgeMap(int[] pixels, int width, int height) {
        this.setPixels(pixels);
        this.width = width;
        this.height = height;
    }

    public EdgeMap(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void normalizeMap(int blackColor, int whiteColor, int searchColor) {
        int color = 0;
        for (int i = 0; i < pixels.length; i++) {
            color = pixels[i];
            if (color == searchColor)
                color = blackColor;
            else
                color = whiteColor;
            pixels[i] = color;
        }
    }

    public Bitmap createEdgeBitmap() {
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    public boolean isBlack(int pixel) {
        return pixel == blackColor;
    }

    public boolean isBlack(int x, int y) {
        return isBlack(getPixel(x, y));
    }

    public int[] getPixels() {
        return pixels;
    }

    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getIndex(int x, int y) {
        return y * width + x;
    }

    public int getPixel(int x, int y) {
        return pixels[getIndex(x, y)];
    }

    public void setPixel(int x, int y, int color) {
        pixels[getIndex(x, y)] = color;
    }

    public int getPixel(int index) {
        return pixels[index];
    }

    public int getX(int index) {
        return (int) Math.floor(index / width);
    }

    public int getY(int index) {
        return index - (width * getX(index));
    }

    public int getBlackColor() {
        return blackColor;
    }

    public void setBlackColor(int blackColor) {
        this.blackColor = blackColor;
    }

    public int getBackGroundColorPixel() {
        return ImageUtility.invert(blackColor);
    }

    public int area() {
        return width * height;
    }

    @Override
    public EdgeMap clone() {
        return new EdgeMap(Arrays.copyOf(pixels, pixels.length), width, height);
    }

}