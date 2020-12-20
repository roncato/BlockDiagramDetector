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
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.engdev.blockdiagramdetector.geometry.Region;
import com.engdev.blockdiagramdetector.state.AppState;
import com.engdev.blockdiagramdetector.state.AppState.StateType;
import com.engdev.blockdiagramdetector.views.BlockDiagramView;
import com.engdev.blockdiagramdetector.views.RegionRecognitionView;
import com.engdev.blockdiagramdetector.views.RegionRecognitionView.SelectionEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements user interface controller
 *
 * @author Engineer
 */
final class UserInterface {

    public final static int blockDiagramBGColor = -1;
    BlockDiagramView bdv = null;
    Map<String, Button> buttons = null;
    private AppBlockDiagramDetector app = null;
    private Activity activity = null;

    public UserInterface(AppBlockDiagramDetector app) {
        this.app = app;
        this.activity = app.getActivity();
        init();
    }

    public static void inputBox(Activity activity, String title, String msg, DialogInterface.OnClickListener onOkay, DialogInterface.OnClickListener onCancel, EditText input) {

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);

        // Sets Texts
        alert.setTitle(title);
        alert.setMessage(msg);

        // Set an EditText view to get user input
        alert.setView(input);

        // Sets Navigation
        alert.setPositiveButton(R.string.okay, onOkay);
        alert.setNegativeButton(R.string.cancel, onCancel);

        // Shows
        alert.show();

    }

    public static void alertBox(Activity activity, String title, String msg, DialogInterface.OnClickListener onClick) {

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);

        // Sets Texts
        alert.setTitle(title);
        alert.setMessage(msg);

        // Sets Navigation
        alert.setNeutralButton(R.string.okay, onClick);

        // Shows
        alert.show();

    }

    public static void alertBox(Activity activity, String title, String msg) {

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);

        // Sets Texts
        alert.setTitle(title);
        alert.setMessage(msg);

        // Sets Navigation
        alert.setNeutralButton(R.string.okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        // Shows
        alert.show();

    }

    public static void showToast(final Activity activity, final String text, final boolean fast) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                int duration = Toast.LENGTH_LONG;
                if (fast)
                    duration = Toast.LENGTH_SHORT;
                Toast.makeText(activity, text, duration).show();
            }

        });

    }

    private void init() {
        initLayout();
        initButtons();
    }

    private void initLayout() {
        initBlockDiagram();
    }

    @SuppressLint("NewApi")
    private void initBlockDiagram() {

        bdv = new BlockDiagramView(activity);
        RelativeLayout rl = (RelativeLayout) activity.findViewById(R.id.rlBlockDiagram);

        // Initial size
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        bdv.setLayoutParams(params);

        // Background
        ShapeDrawable s = new ShapeDrawable(new RectShape());
        s.getPaint().setColor(blockDiagramBGColor);
        bdv.setBackground(s);

        // Match view size
        bdv.setMatchViewSize(activity.findViewById(R.id.tblMain));

        rl.addView(bdv);
    }

    private void initButtons() {

        buttons = new HashMap<String, Button>();

        Button btnImport = (Button) activity.findViewById(R.id.btnImport);
        Button btnOpen = (Button) activity.findViewById(R.id.btnOpen);
        Button btnCamera = (Button) activity.findViewById(R.id.btnCamera);
        Button btnDetect = (Button) activity.findViewById(R.id.btnDetect);
        Button btnSaveAs = (Button) activity.findViewById(R.id.btnSaveAs);
        Button btnExport = (Button) activity.findViewById(R.id.btnExport);
        Button btnAbout = (Button) activity.findViewById(R.id.btnAbout);

        buttons.put("btnImport", btnImport);
        buttons.put("btnOpen", btnOpen);
        buttons.put("btnCamera", btnCamera);
        buttons.put("btnDetect", btnDetect);
        buttons.put("btnSaveAs", btnSaveAs);
        buttons.put("btnExport", btnExport);
        buttons.put("btnAbout", btnAbout);

        btnImport.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                handleImportImage();
            }

        });

        btnOpen.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                handleOpenBlockDiagramFile();
            }

        });

        btnCamera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                handleStartCamera();
            }

        });

        btnDetect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                handleDetectBlockDiagram();
            }

        });

        btnSaveAs.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO btnSaveAs.setOnClickListener
                UserInterface.alertBox(activity, "Not Implemented", "To be implemented...");
            }

        });

        btnExport.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO btnExport.setOnClickListener
                UserInterface.alertBox(activity, "Not Implemented", "To be implemented...");
            }

        });

        btnAbout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                displayAbout();
            }

        });

    }

    /**
     * Handles image import
     */
    protected void handleImportImage() {
        app.setState(new AppState(StateType.BrowsingImage));
    }

    /**
     * Starts Camera activity
     */
    private final void handleStartCamera() {
        app.setState(new AppState(StateType.PreviewingCamera));
    }

    private final void handleDetectBlockDiagram() {
        app.setState(new AppState(StateType.TracingObjects));
    }

    private final void handleOpenBlockDiagramFile() {
        // TODO btnOpen.setOnClickListener
        UserInterface.alertBox(activity, "Not Implemented", "To be implemented...");
    }

    final RegionRecognitionView showRegionRecognitionView(Region region, DialogInterface.OnClickListener onNegativeClick) {

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);

        RegionRecognitionView view = new RegionRecognitionView(activity, region);

        alert.setNegativeButton("Unrecognizable", onNegativeClick);

        alert.setTitle("What object is this region?");
        alert.setView(view);

        final AlertDialog dialog = alert.show();

        view.setOnSelectionListener(new RegionRecognitionView.OnSelectionHandler() {

            @Override
            public void onSelection(SelectionEvent event) {
                dialog.dismiss();
            }

        });

        return view;
    }

    protected void displayAbout() {

        String msg = activity.getResources().getString(R.string.aboutMessage);

        alertBox(activity, "About", msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }

    public AppBlockDiagramDetector getApp() {
        return app;
    }

}
