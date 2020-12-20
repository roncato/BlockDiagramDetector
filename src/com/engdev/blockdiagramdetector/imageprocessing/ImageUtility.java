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
import android.view.Display;
import com.engdev.blockdiagramdetector.math.MathUtility;

/**
 * Utility for image and color operations.
 *
 * @author Lucas Batista
 */
public abstract class ImageUtility {

    // Static properties
    public static void fillPixels(int[] pixels, Bitmap image) {
        image.getPixels(pixels, 0, image.getWidth(), 1, 1, image.getWidth() - 1, image.getHeight() - 1);
    }

    ;

    public static int getRow(int pixelIndex, int width) {
        return (int) Math.floor(pixelIndex / width);
    }

    public static int getColumn(int pixelIndex, int width) {
        return pixelIndex - (width * getRow(pixelIndex, width));
    }

    public static int getIndex(int x, int y, int width) {
        return y * width + x;
    }

    public static int[] convertToPixels(double[] matrix) {
        int[] pixels = new int[matrix.length];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = adjustColor((int) matrix[i]);
        }
        return pixels;
    }

    public static int adjustColor(int color) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        a = MathUtility.clamp(a, 0, 255);
        r = MathUtility.clamp(r, 0, 255);
        g = MathUtility.clamp(g, 0, 255);
        b = MathUtility.clamp(b, 0, 255);
        return Color.argb(a, r, g, b);
    }

    public static Bitmap rotate(Bitmap bm, int angle) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    public static Bitmap RasterToBitmap(byte[] buffer) {
        return BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
    }

    @SuppressLint("NewApi")
    public static Bitmap fitImageOnDisplay(Display display, Bitmap bm) {
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        float imageRatio = (float) bm.getWidth() / (float) bm.getHeight();
        height = (int) (width / imageRatio);

        return Bitmap.createScaledBitmap(bm, width, height, false);
    }

    public static Bitmap fitByWidthImage(int width, Bitmap bm) {

        float imageRatio = (float) bm.getWidth() / (float) bm.getHeight();
        int height = (int) (width / imageRatio);

        return Bitmap.createScaledBitmap(bm, width, height, false);
    }

    public static Bitmap fitByHeightImage(int height, Bitmap bm) {

        float imageRatio = (float) bm.getWidth() / (float) bm.getHeight();
        int width = (int) (height * imageRatio);

        return Bitmap.createScaledBitmap(bm, width, height, false);
    }

    public static Bitmap scale(Bitmap bm, float scale) {
        int width = (int) (bm.getWidth() / scale);
        int height = (int) (bm.getHeight() / scale);

        return Bitmap.createScaledBitmap(bm, width, height, false);
    }

    public static boolean isNeighbor(Point p1, Point p2) {
        for (int y = p1.y - 1; y <= p1.y + 1; y++) {
            for (int x = p1.x - 1; x <= p1.x + 1; x++) {
                if (x == p2.x && y == p2.y)
                    return true;
            }
        }
        return false;
    }

    public static int multiply(int color, float factor) {
        int a = alpha(color);
        int r = (int) Math.round(red(color) * factor);
        int g = (int) Math.round(green(color) * factor);
        int b = (int) Math.round(blue(color) * factor);
        color = clamp(a, r, g, b);
        return color;
    }

    public static int add(int color, float value) {
        int a = alpha(color);
        int r = (int) Math.round(red(color) + value);
        int g = (int) Math.round(green(color) + value);
        int b = (int) Math.round(blue(color) + value);
        color = clamp(a, r, g, b);
        return color;
    }

    public static int invert(int color) {
        int a = alpha(color);
        int r = 255 - red(color);
        int g = 255 - green(color);
        int b = 255 - blue(color);
        color = clamp(a, r, g, b);
        return color;
    }

    public static int gamma(int color, float factor) {
        int a = alpha(color);
        int r = (int) Math.round(Math.pow(red(color) / 255, factor) * 255);
        int g = (int) Math.round(Math.pow(green(color) / 255, factor) * 255);
        int b = (int) Math.round(Math.pow(blue(color) / 255, factor) * 255);
        color = clamp(a, r, g, b);
        return color;
    }

    public static int log(int color) {
        int a = alpha(color);
        int r = (int) Math.round(Math.log10(red(color)));
        int g = (int) Math.round(Math.log10(green(color)));
        int b = (int) Math.round(Math.log10(blue(color)));
        color = clamp(a, r, g, b);
        return color;
    }

    public static int max(int color, int value) {
        int a = alpha(color);
        int r = (int) Math.max(red(color), value);
        int g = (int) Math.max(green(color), value);
        int b = (int) Math.max(blue(color), value);
        color = clamp(a, r, g, b);
        return color;
    }

    public static int min(int color, int value) {
        int a = alpha(color);
        int r = (int) Math.min(red(color), value);
        int g = (int) Math.min(green(color), value);
        int b = (int) Math.min(blue(color), value);
        color = clamp(a, r, g, b);
        return color;
    }

    public static int luminance(int color) {
        int a = alpha(color);
        int r = (int) Math.round(red(color) * 0.299F);
        int g = (int) Math.round(green(color) * 0.587F);
        int b = (int) Math.round(blue(color) * 0.114F);
        color = clamp(a, r, g, b);
        return color;
    }

    public static int clamp(int alpha, int red, int green, int blue) {
        alpha = MathUtility.clamp(alpha, 0, 255);
        red = MathUtility.clamp(red, 0, 255);
        green = MathUtility.clamp(green, 0, 255);
        blue = MathUtility.clamp(blue, 0, 255);
        return argb(alpha, red, green, blue);
    }

    public static int clamp(int color) {
        return clamp(alpha(color), red(color), green(color), blue(color));
    }

    public static int alpha(int color) {
        return (color >> 24) & 0xFF;
    }

    public static int red(int color) {
        return (color >> 16) & 0xFF;
    }

    public static int green(int color) {
        return (color >> 8) & 0xFF;
    }

    public static int blue(int color) {
        return color & 0xFF;
    }

    public static int argb(int alpha, int red, int green, int blue) {
        return (alpha << 24) + (red << 16) + (green << 8) + blue;
    }

    public static int setAlpha(int color, int alpha) {
        int r = red(color);
        int g = green(color);
        int b = blue(color);
        return argb(alpha, r, g, b);
    }

    public static int setRed(int color, int red) {
        int a = alpha(color);
        int g = green(color);
        int b = blue(color);
        return argb(a, red, g, b);
    }

    public static int setGreen(int color, int green) {
        int a = alpha(color);
        int r = red(color);
        int b = blue(color);
        return argb(a, r, green, b);
    }

    public static int setBlue(int color, int blue) {
        int a = alpha(color);
        int r = red(color);
        int g = green(color);
        return argb(a, r, g, blue);
    }

    public static int randomColor() {
        int alpha = 255;
        int red = (int) (Math.random() * 255);
        int green = (int) (Math.random() * 255);
        int blue = (int) (Math.random() * 255);
        return argb(alpha, red, green, blue);
    }

    public static int randomColor(int alpha) {
        int red = (int) (Math.random() * 255);
        int green = (int) (Math.random() * 255);
        int blue = (int) (Math.random() * 255);
        return argb(alpha, red, green, blue);
    }

    public enum ImageAxis {x, y}

}
