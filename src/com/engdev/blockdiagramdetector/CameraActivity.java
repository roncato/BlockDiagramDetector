/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.engdev.blockdiagramdetector.io.CameraManager;
import com.engdev.blockdiagramdetector.state.ActivityState;
import com.engdev.blockdiagramdetector.state.State;
import com.engdev.blockdiagramdetector.state.StateMachine;
import com.engdev.blockdiagramdetector.views.CameraSurfaceView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements a camera activity
 *
 * @author Lucas Batista
 */
public final class CameraActivity extends AbstractActivity {

    private CameraManager cameraManager = null;
    private Map<String, Button> buttons = null;
    private ActivityStateMachine stateMachine = new ActivityStateMachine();
    private byte[] image = null;

    private void init() {
        initCameraManager();
        initButtons();
    }

    @SuppressLint("NewApi")
    private void initCameraManager() {

        //Creates new view and adds it to layout
        CameraSurfaceView cameraSurface = new CameraSurfaceView(this);

        RelativeLayout rlCameraSurfaceWrapper = (RelativeLayout) findViewById(R.id.rlCameraSurfaceWrapper);
        rlCameraSurfaceWrapper.addView(cameraSurface);

        cameraManager = new CameraManager(cameraSurface);

        // Sets camera size
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        LayoutParams csParams = new LayoutParams(size.x, size.y);
        cameraSurface.setLayoutParams(csParams);

        cameraManager.setPictureSize(size.x, size.y);

    }

    private void initButtons() {

        buttons = new HashMap<String, Button>();

        Button btnRejectPicture = (Button) findViewById(R.id.btnRejectPicture);
        Button btnTakePicture = (Button) findViewById(R.id.btnTakePicture);
        Button btnAcceptPicture = (Button) findViewById(R.id.btnAcceptPicture);

        buttons.put("btnRejectPicture", btnRejectPicture);
        buttons.put("btnTakePicture", btnTakePicture);
        buttons.put("btnAcceptPicture", btnAcceptPicture);

        // Set buttons visibility
        setActionButtonsVisibility(false);

        //Disable sound effects
        btnRejectPicture.setSoundEffectsEnabled(false);
        btnTakePicture.setSoundEffectsEnabled(false);
        btnAcceptPicture.setSoundEffectsEnabled(false);

        btnRejectPicture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                handleRejectPicture();
            }

        });

        btnTakePicture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                handleTakePicture();
            }

        });

        btnAcceptPicture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                handleAcceptPicture();
            }

        });

    }

    private void setTakePictureButtonVisibility(boolean b) {
        Button btnTakePicture = buttons.get("btnTakePicture");

        if (b)
            btnTakePicture.setVisibility(View.VISIBLE);
        else
            btnTakePicture.setVisibility(View.INVISIBLE);

    }

    private void setActionButtonsVisibility(boolean b) {
        Button btnRejectPicture = buttons.get("btnRejectPicture");
        Button btnAcceptPicture = buttons.get("btnAcceptPicture");

        if (b) {
            btnRejectPicture.setVisibility(View.VISIBLE);
            btnAcceptPicture.setVisibility(View.VISIBLE);
        } else {
            btnRejectPicture.setVisibility(View.INVISIBLE);
            btnAcceptPicture.setVisibility(View.INVISIBLE);
        }

    }

    protected void handleRejectPicture() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.reject);
        mp.setVolume(0.0F, 0.3F);
        mp.start();
        setTakePictureButtonVisibility(true);
        setActionButtonsVisibility(false);

        try {
            cameraManager.startPreview();
        } catch (IOException e) {
            UserInterface.alertBox(this, "IOException", e.getMessage());
        }
    }

    protected void handleTakePicture() {
        setTakePictureButtonVisibility(false);
        takePicture();
    }

    protected void handleAcceptPicture() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.confirm);
        mp.setVolume(0.0F, 0.3F);
        mp.start();
        sleep(500);
        prepareImage();
        setResult(RESULT_OK);
        finish();
    }

    private void prepareImage() {
        AppBlockDiagramDetector.CameraImage = image;
    }

    /**
     * Starts camera preview on surface
     */
    private final void startCameraPreview() {
        try {
            cameraManager.startPreview();
        } catch (IOException e) {
            UserInterface.alertBox(this, "IO Exception", e.getMessage(),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
        }

    }

    private final void takePicture() {
        cameraManager.takePicture(new Camera.ShutterCallback() {

                                      @Override
                                      public void onShutter() {
                                          setActionButtonsVisibility(true);
                                      }
                                  },
                new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] arg0, Camera arg1) {
                    }
                },
                null,
                new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        image = data;
                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        init();
    }

    @Override
    public void onActivityStateChange(ActivityState state) {
        stateMachine.onStateChange(state);
    }

    private class ActivityStateMachine extends StateMachine {

        @Override
        public void onStateChange(State state) {
            ActivityState st = (ActivityState) state;
            switch (st.getStateType()) {
                case Running:
                    handleRunningState();
                    break;
                case Paused:
                    handlePausedState();
                    break;
                case Stopped:
                    break;
                case Killed:
                    handleKilledState();
                    break;
                default:
            }
        }

        /**
         * Handles Killed State
         */
        private void handleKilledState() {
            if (cameraManager != null)
                cameraManager.release();
        }

        /**
         * Handles on entering stopped state
         */
        private void handleRunningState() {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (!cameraManager.wasInPictureTakenState())
                startCameraPreview();
        }

        /**
         * Handles on entering stopped state
         */
        private void handlePausedState() {
            cameraManager.release();
        }

    }
}
