/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;
import com.engdev.blockdiagramdetector.blockdiagram.BlockDiagramParser;
import com.engdev.blockdiagramdetector.database.Database;
import com.engdev.blockdiagramdetector.geometry.GeometricObject;
import com.engdev.blockdiagramdetector.geometry.Region;
import com.engdev.blockdiagramdetector.imageprocessing.*;
import com.engdev.blockdiagramdetector.imageprocessing.Filter.FinishEvent;
import com.engdev.blockdiagramdetector.imageprocessing.PointOperationFilter.Operation;
import com.engdev.blockdiagramdetector.state.AppState;
import com.engdev.blockdiagramdetector.state.AppState.StateType;
import com.engdev.blockdiagramdetector.state.State;
import com.engdev.blockdiagramdetector.state.StateMachine;
import com.engdev.blockdiagramdetector.util.Buffer;
import com.engdev.blockdiagramdetector.views.RegionRecognitionView;
import com.engdev.blockdiagramdetector.views.RegionRecognitionView.SelectionEvent;

import java.io.IOException;
import java.util.List;

/**
 * Application that detects block diagrams from images.
 *
 * @author Lucas Batista
 */
public final class AppBlockDiagramDetector implements App, Parcelable {

    public static final String DATABASE_FILE = "database.xml";

    /**
     * Number of buffered states
     */
    public static final int BUFFERED_N_STATES = 100;

    /**
     * Camera rotation constant. Dependent on OS
     */
    public final static int CAMERA_IMAGE_ROT = -90;
    public static final Parcelable.Creator<AppBlockDiagramDetector> CREATOR = new Parcelable.Creator<AppBlockDiagramDetector>() {

        public AppBlockDiagramDetector createFromParcel(Parcel in) {
            return new AppBlockDiagramDetector(in);
        }

        public AppBlockDiagramDetector[] newArray(int size) {
            return new AppBlockDiagramDetector[size];
        }

    };
    /**
     * Receives image from camera
     */
    public static byte[] CameraImage = null;
    private final StateMachine stateMachine = new AppStateMachine();
    private Activity activity = null;
    private UserInterface ui = null;
    private Buffer<AppState> states = null;
    private BlockDiagramParser bdp = null;

    public AppBlockDiagramDetector(Activity activity) {
        this.activity = activity;
    }

    private AppBlockDiagramDetector(Parcel in) {
    }

    // Following methods initiate the application
    private void init() {
        initDatabase();
        initUserInterface();
        initStates();
    }

    private void initStates() {
        states = new Buffer<AppState>(BUFFERED_N_STATES);
    }

    private void initUserInterface() {
        ui = new UserInterface(this);
    }

    private void initDatabase() {
        try {
            Database.load(activity, DATABASE_FILE);
        } catch (Exception e) {
            UserInterface.alertBox(activity, "Exception", e.getMessage());
        }
    }

    /**
     * Gets main activity
     *
     * @return
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Sets app state
     *
     * @param state
     */
    public void setState(AppState state) {
        states.add(state);
        onAppStateChange(state);
    }

    /**
     * @param buildingblockdiagram
     */
    private void setState(AppState.StateType stateType) {
        setState(new AppState(stateType));
    }

    /**
     * Handles app state change
     *
     * @param state
     */
    public void onAppStateChange(AppState state) {
        stateMachine.onStateChange(state);
    }

    public UserInterface getUi() {
        return ui;
    }

    @Override
    public void run() {
        init();
        onAppStateChange(new AppState(StateType.ScreenBlank));
    }

    @Override
    public void handleActivityMotionEvent(MotionEvent event, EventType evt) {
    }

    @Override
    public void handleActivityResult(int requestCode, int resultCode,
                                     Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // Goes to this state first.
            setState(new AppState(StateType.ScreenBlank));
            switch (requestCode) {
                case RequestCodes.IMAGE_BROWSER:
                    try {
                        Uri uri = data.getData();
                        ui.bdv.setImage(uri);
                        setState(new AppState(StateType.ImageLoaded));
                    } catch (IOException e) {
                        UserInterface.alertBox(activity, "IO Exception", e.getMessage());
                    }
                    break;
                case RequestCodes.CAMERA_IMAGE:
                    Bitmap bm = ImageUtility.RasterToBitmap(CameraImage);
                    CameraImage = null;
                    bm = ImageUtility.fitByHeightImage(ui.bdv.getWidth(), bm);
                    bm = ImageUtility.rotate(bm, CAMERA_IMAGE_ROT);
                    ui.bdv.setImage(bm);
                    setState(new AppState(StateType.ImageLoaded));
                    break;
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public final static class RequestCodes {
        public final static int IMAGE_BROWSER = 1;
        public static final int CAMERA_IMAGE = 2;
    }

    private final class AppStateMachine extends StateMachine {

        /**
         * Handles app state change
         *
         * @param state
         */
        public void onStateChange(State state) {
            AppState st = (AppState) state;
            switch (st.getStateType()) {
                case ScreenBlank:
                    handleScreenBlank();
                    break;
                case BrowsingImage:
                    handleBrowsingImage();
                    break;
                case ImageLoaded:
                    handleImageLoaded();
                    break;
                case TracingObjects:
                    handleTracingObjects();
                    break;
                case RecognizingObjects:
                    handleRecognizingObjects();
                    break;
                case UserRecognition:
                    handleUserRecognition();
                    break;
                case BuildingBlockDiagram:
                    handleBuildingBlockDiagram();
                    break;
                case BlockDiagramBuilt:
                    hanldeBlockDiagramBuilt();
                    break;
                case ExportingBlockDiagram:
                    handleExportingBlockDiagram();
                    break;
                case SavingBlockDiagram:
                    handleSavingBlockDiagram();
                    break;
                case ResettingApp:
                    handleResettingApp();
                    break;
                case PreviewingCamera:
                    handlePreviewCamera();
                    break;
                case AcceptingImage:
                    handleAcceptingImage();
                    break;
            }
        }

        /**
         * Handles Screen blank state. This is the initial state
         */
        private void handleScreenBlank() {
            ui.bdv.clear();
        }

        private void handleBrowsingImage() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(Intent.createChooser(intent, "Select Image"), RequestCodes.IMAGE_BROWSER);
        }

        /**
         * The entry activity of this state has been implemented at handleActivityResult
         */
        private void handleImageLoaded() {
        }

        private void handleTracingObjects() {

            // Detects edges
            final CannyEdgeDetector ed = new CannyEdgeDetector(ui.bdv.getImage().getBitmap());

            ed.setOnFinishHandler(new Filter.OnFinishHandler() {

                @Override
                public void onFinish(FinishEvent event) {

                    final EdgeMap em = ed.createEdgeMap();

                    // Inverts color from canny filter
                    final PointOperationFilter po = new PointOperationFilter(em.createEdgeBitmap(), Operation.Invert);
                    po.run();
                    em.setPixels(po.getProcessedPixels());

                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ui.bdv.setImage(em.createEdgeBitmap());
                        }

                    });

                    bdp = new BlockDiagramParser(em);

                    // Handles when block diagram detector is done
                    bdp.setOnFinishHandler(new BlockDiagramParser.OnFinishHandler() {

                        @Override
                        public void onFinish(BlockDiagramParser.FinishEvent event) {

                            final Bitmap bm = bdp.getExploredMap().createEdgeBitmap();
                            final AppState.StateType nextState;

                            activity.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    ui.bdv.setImage(bm);
                                }

                            });

                            switch (event.result) {
                                case Successful:
                                    nextState = AppState.StateType.RecognizingObjects;
                                    break;
                                case PartialSuccessful:
                                    nextState = AppState.StateType.RecognizingObjects;
                                    break;
                                default:
                                    UserInterface.showToast(activity, "Unable to trace image.", true);
                                    nextState = AppState.StateType.ImageLoaded;
                            }

                            activity.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    setState(nextState);
                                }

                            });

                        }

                    });

                    UserInterface.showToast(activity, "Tracing...", true);

                    new Thread(bdp).start();

                }

            });

            UserInterface.showToast(activity, "Detecting...", true);

            new Thread(ed).start();

        }

        private void handleRecognizingObjects() {

            // Handles when block diagram scanner is done
            bdp.setOnFinishHandler(new BlockDiagramParser.OnFinishHandler() {

                @Override
                public void onFinish(BlockDiagramParser.FinishEvent event) {

                    final AppState.StateType nextState;

                    switch (event.result) {
                        case Successful:
                            nextState = AppState.StateType.BuildingBlockDiagram;
                            break;
                        case PartialSuccessful:
                        default:
                            nextState = AppState.StateType.UserRecognition;
                    }

                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            setState(nextState);
                        }

                    });

                }

            });
            (new Thread(bdp)).start();
            UserInterface.showToast(activity, "Recognizing...", true);
        }

        private void handleUserRecognition() {
            List<Region> regions = bdp.getUnrecognizedRegions();

            bdp.setOnFinishHandler(new BlockDiagramParser.OnFinishHandler() {

                @Override
                public void onFinish(BlockDiagramParser.FinishEvent event) {

                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            setState(AppState.StateType.BuildingBlockDiagram);
                        }

                    });
                }

            });

            for (final Region region : regions) {
                final RegionRecognitionView view = ui.showRegionRecognitionView(region,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                bdp.setRecognition(region, GeometricObject.GeometricObjectType.Noise);
                            }
                        });
                view.setOnSelectionListener(new RegionRecognitionView.OnSelectionHandler() {
                    @Override
                    public void onSelection(SelectionEvent event) {
                        bdp.setRecognition(region, view.getObjectType());
                    }
                });
            }
        }

        private void handleBuildingBlockDiagram() {
            //TODO handleBuildingBlockDiagram
        }

        private void hanldeBlockDiagramBuilt() {
            // TODO hanldeBlockDiagramBuilt

        }

        private void handleExportingBlockDiagram() {
            // TODO handleExportingBlockDiagram

        }

        private void handleSavingBlockDiagram() {
            // TODO handleSavingBlockDiagram

        }

        private void handleResettingApp() {
            // TODO handleResettingApp

        }

        private void handlePreviewCamera() {
            Intent i = new Intent(activity, CameraActivity.class);
            i.setAction(Intent.ACTION_VIEW);
            activity.startActivityForResult(i, RequestCodes.CAMERA_IMAGE);
        }

        private void handleAcceptingImage() {
            // handleAcceptingImage

        }

    }
}
