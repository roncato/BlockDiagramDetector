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
 * Abstract class for filters. It handles threads and event listener handlers.
 *
 * @author Lucas Batista
 */
public abstract class Filter implements Runnable {

    protected Bitmap sourceImage = null;
    protected int[] pixels = null;
    private OnFinishListener listener = null;

    protected abstract void apply();

    protected void onFinished() {
        if (listener != null)
            listener.onFinish(new FinishEvent());
    }

    @Override
    public void run() {
        apply();
        onFinished();
    }

    protected void fillPixels() {
        sourceImage.getPixels(pixels, 0, sourceImage.getWidth(), 1, 1, sourceImage.getWidth() - 1, sourceImage.getHeight() - 1);
    }

    public int[] getProcessedPixels() {
        return pixels;
    }

    public Bitmap createProcessedBitmap() {
        return Bitmap.createBitmap(pixels, sourceImage.getWidth(), sourceImage.getHeight(), Bitmap.Config.ARGB_8888);
    }

    public void setOnFinishHandler(OnFinishHandler handler) {
        listener = new OnFinishListener(handler);
    }

    public interface OnFinishHandler {
        public void onFinish(FinishEvent event);
    }

    public class FinishEvent {
    }

    private final class OnFinishListener implements OnFinishHandler {

        private OnFinishHandler handler = null;

        private OnFinishListener(OnFinishHandler handler) {
            this.handler = handler;
        }

        public void onFinish(FinishEvent event) {
            handler.onFinish(event);
        }

    }

}
