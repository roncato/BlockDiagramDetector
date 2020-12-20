/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.*;
import com.engdev.blockdiagramdetector.exceptions.CameraCannotBeAllocatedException;
import com.engdev.blockdiagramdetector.state.CameraState;
import com.engdev.blockdiagramdetector.state.CameraState.StateType;
import com.engdev.blockdiagramdetector.util.Buffer;

import java.io.IOException;
import java.util.List;

/**
 * Implements camera surface view
 *
 * @author Lucas Batista
 */
public class CameraSurfaceView extends ViewGroup implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private static final int STORE_N_STATES = 10;

    private Context context = null;
    private Camera camera = null;
    private List<Size> supportedPreviewSizes = null;
    private List<String> supportedFlashModes = null;
    private Size previewSize = null;
    private SurfaceHolder holder = null;
    private SurfaceView surface = null;
    private OnSurfaceReadyListener listener = null;
    private Buffer<CameraState> states = null;

    public CameraSurfaceView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        surface = new SurfaceView(context);
        //setCamera(openCamera());
        addView(surface);

        holder = surface.getHolder();
        holder.addCallback(this);
        holder.setKeepScreenOn(true);
        states = new Buffer<CameraState>(STORE_N_STATES);
        setState(CameraState.StateType.Uninitialized);
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int width, int height) {
        Size optimalSize = null;

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) height / width;

        // Try to find a size match.
        for (Size size : sizes) {
            if (size.height != width) continue;
            double ratio = (double) size.width / size.height;
            if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE)
                optimalSize = size;
        }

        return optimalSize;
    }

    @SuppressLint("NewApi")
    public Camera openCamera() throws CameraCannotBeAllocatedException {
        if (!(getState().getStateType() == StateType.Open)) {
            try {
                camera = Camera.open();

                if (camera == null)
                    camera = Camera.open(0);
                setState(StateType.Open);
            } catch (Exception e) {
                throw new CameraCannotBeAllocatedException(e.getMessage());
            }
        }

        return camera;
    }

    public void release() {
        if (!(getState().getStateType() == StateType.Released)) {
            if (camera != null) {
                try {
                    stopPreview();
                    camera.setPreviewCallback(null);
                    camera.release();
                    setState(StateType.Released);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void unlock() {
        if (camera != null)
            camera.unlock();
    }

    public void startPreview() {
        try {
            adjustCameraOrientation();
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            setState(StateType.Previewing);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void adjustCameraOrientation() {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                camera.setDisplayOrientation(90);
                break;
            case Surface.ROTATION_180:
                camera.setDisplayOrientation(270);
                break;
            case Surface.ROTATION_270:
                camera.setDisplayOrientation(180);
                break;
        }
    }

    public void stopPreview() {
        if (camera != null && ((!(getState().getStateType() == StateType.Released)) || (!(getState().getStateType() == StateType.Stopped))))
            try {
                camera.stopPreview();
                setState(StateType.Stopped);
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    public final void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw, Camera.PictureCallback postview, Camera.PictureCallback jpeg) {
        camera.takePicture(shutter, raw, postview, jpeg);
        setState(StateType.PictureTaken);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            if (previewSize != null)
                adjustPreviewSizeLayout(left, top, right, bottom);
            else
                adjustSymetricalLayout(left, top, right, bottom);

            if (camera != null)
                adjustCameraOrientation();

        }
    }

    private void adjustPreviewSizeLayout(int left, int top, int right, int bottom) {
        final int width = right - left;
        final int height = bottom - top;

        int previewWidth = width;
        int previewHeight = height;

        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                previewWidth = previewSize.height;
                previewHeight = previewSize.width;
                break;
            case Surface.ROTATION_90:
                previewWidth = previewSize.width;
                previewHeight = previewSize.height;
                break;
            case Surface.ROTATION_180:
                previewWidth = previewSize.height;
                previewHeight = previewSize.width;
                break;
            case Surface.ROTATION_270:
                previewWidth = previewSize.width;
                previewHeight = previewSize.height;
                break;
        }

        final int scaledChildHeight = previewHeight * width / previewWidth;
        surface.layout(0, height - scaledChildHeight, width, height);

    }

    private void adjustSymetricalLayout(int left, int top, int right, int bottom) {
        final int width = right - left;
        final int height = bottom - top;

        int previewWidth = width;
        int previewHeight = height;

        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        switch (display.getRotation()) {
            // Y axis is pointing up
            case Surface.ROTATION_0:
                previewHeight = height;
                break;
            // Y axis pointing to the left
            case Surface.ROTATION_90:
                previewHeight = width;
                break;
            // Y axis pointing to the down
            case Surface.ROTATION_180:
                previewHeight = height;
                break;
            // Y axis pointing to the right
            case Surface.ROTATION_270:
                previewHeight = width;
                break;
        }

        surface.layout(left, top, left + previewWidth, top + previewHeight);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) 0xFF;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            Size previewSize = getPreviewSize();
            if (previewSize != null)
                parameters.setPreviewSize(previewSize.width, previewSize.height);

            camera.setParameters(parameters);
            camera.startPreview();
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
        if (listener != null)
            listener.onSurfaceReady(new SurfaceReadyEvent());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        release();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (supportedPreviewSizes != null) {
            previewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height);
        }
    }

    public void setPreviewHandlers() {
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.setPreviewCallback(this);
    }

    public void setSupportedPreviewSizes(List<Size> supportedPreviewSizes) {
        this.supportedPreviewSizes = supportedPreviewSizes;
    }

    public Size getPreviewSize() {
        return previewSize;
    }

    public void setPreviewSize(Size previewSize) {
        this.previewSize = previewSize;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        if (camera != null) {
            supportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
            supportedFlashModes = camera.getParameters().getSupportedFlashModes();
            // Set the camera to Auto Flash mode.
            if (supportedFlashModes != null && supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                camera.setParameters(parameters);
            }
            setPreviewHandlers();
        }
    }

    public void setCameraOrientation(int orientation) {
        if (camera != null)
            camera.setDisplayOrientation(orientation);
    }

    public void finalize() {
        release();
    }

    public final void setOnSurfaceReadyHandler(OnSurfaceReadyHandler handler) {
        listener = new OnSurfaceReadyListener(handler);
    }

    public CameraState getState() {
        return states.peek(0);
    }

    public void setState(CameraState.StateType stateType) {
        states.add(new CameraState(stateType));
    }

    public CameraState peekState(int offSet) {
        return states.peek(offSet);
    }

    public Buffer<CameraState> getStatesBuffer() {
        return states;
    }

    public interface OnSurfaceReadyHandler {
        public void onSurfaceReady(SurfaceReadyEvent event);
    }

    public final class SurfaceReadyEvent {
    }

    public final class OnSurfaceReadyListener implements OnSurfaceReadyHandler {

        private OnSurfaceReadyHandler handler = null;

        public OnSurfaceReadyListener(OnSurfaceReadyHandler handler) {
            this.handler = handler;
        }

        @Override
        public void onSurfaceReady(SurfaceReadyEvent event) {
            handler.onSurfaceReady(event);
        }
    }

}
