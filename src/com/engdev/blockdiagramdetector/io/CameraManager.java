/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.io;

import android.hardware.Camera;
import com.engdev.blockdiagramdetector.exceptions.CameraCannotBeAllocatedException;
import com.engdev.blockdiagramdetector.state.CameraState;
import com.engdev.blockdiagramdetector.state.CameraState.StateType;
import com.engdev.blockdiagramdetector.util.Buffer;
import com.engdev.blockdiagramdetector.views.CameraSurfaceView;
import com.engdev.blockdiagramdetector.views.CameraSurfaceView.OnSurfaceReadyHandler;
import com.engdev.blockdiagramdetector.views.CameraSurfaceView.SurfaceReadyEvent;

import java.io.IOException;

/**
 * Manages camera operations.
 *
 * @author Lucas Batista
 */
public class CameraManager {

    private CameraSurfaceView surface = null;
    private int imageWidth = 0;
    private int imageHeight = 0;

    public CameraManager(CameraSurfaceView surface) {
        this.surface = surface;
    }

    public void openCamera() {
        if (surface.getState().getStateType() == StateType.Released || surface.getState().getStateType() == StateType.Uninitialized) {
            try {
                Camera camera = surface.openCamera();
                surface.setCamera(camera);
            } catch (CameraCannotBeAllocatedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startPreview() throws IOException {

        StateType currState = surface.getState().getStateType();

        if (currState == StateType.Uninitialized) {
            openCamera();

            surface.setOnSurfaceReadyHandler(new OnSurfaceReadyHandler() {
                @Override
                public void onSurfaceReady(SurfaceReadyEvent event) {
                    surface.startPreview();
                }
            });
        }
        // Paused while previewing
        else if (!(currState == StateType.Previewing)) {
            openCamera();
            surface.startPreview();
        }
        // It was stopped
        else if (currState == StateType.Stopped) {
            surface.startPreview();
        }
    }

    public boolean wasInPictureTakenState() {
        Buffer<CameraState> buffer = surface.getStatesBuffer();
        if (buffer.bufferedSize() > 2)
            return buffer.peek(2).getStateType() == CameraState.StateType.PictureTaken;
        return false;
    }

    public void stopPreview() {
        surface.stopPreview();
    }

    public void setPictureSize(int width, int height) {
        imageWidth = width;
        imageHeight = height;
    }

    public final void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw, Camera.PictureCallback postview, Camera.PictureCallback jpeg) {
        if (imageWidth > 0 || imageHeight > 0) {
            Camera.Parameters params = surface.getCamera().getParameters();
            params.setPictureSize(imageWidth, imageHeight);
        }
        surface.takePicture(shutter, raw, postview, jpeg);
    }

    public void unlock() {
        surface.getCamera().unlock();
    }

    public void release() {
        surface.release();
    }

    public CameraSurfaceView getSurface() {
        return surface;
    }
}
