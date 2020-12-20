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
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.View;
import android.widget.*;
import com.engdev.blockdiagramdetector.R;
import com.engdev.blockdiagramdetector.geometry.GeometricObject.GeometricObjectType;
import com.engdev.blockdiagramdetector.geometry.Region;
import com.engdev.blockdiagramdetector.imageprocessing.ImageUtility;

import java.util.ArrayList;
import java.util.List;

public final class RegionRecognitionView extends ScrollView {

    private GeometricObjectType objectType = null;
    private Context context = null;
    private Region region = null;
    private List<OnSelectionListener> listeners = null;

    public RegionRecognitionView(Context context) {
        super(context);
    }

    public RegionRecognitionView(Context context, Region region) {
        super(context);
        this.region = region;
        this.context = context;
        listeners = new ArrayList<OnSelectionListener>();
        buildLayout();
    }

    @SuppressLint("NewApi")
    private void buildLayout() {
        TableLayout table = new TableLayout(context);
        RelativeLayout rel = (RelativeLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.region_image_layout, null);
        FrameLayout frame = (FrameLayout) rel.getChildAt(0);
        TableRow row = null;

        // Region Image
        row = new TableRow(context);
        ImageView view = getRegionImage();
        frame.addView(view);

        // Add views
        row.addView(rel);
        addView(table);
        table.addView(row);

        GeometricObjectType[] objectTypes = GeometricObjectType.values();

        for (final GeometricObjectType objectType : objectTypes) {

            // Row
            rel = (RelativeLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.region_row_layout, null);
            row = (TableRow) rel.getChildAt(0);

            row.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    RegionRecognitionView.this.objectType = objectType;
                    onSelection();
                }

            });

            // TextView
            TextView label = new TextView(context);
            label.setHeight(50);
            label.setTextColor(-1);
            label.setText(objectType.toString());
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            // Add views
            row.addView(label);
            table.addView(rel);
        }
    }

    private ImageView getRegionImage() {
        ImageView imgView = new ImageView(context);
        Bitmap bm = Region.createImageBitmap(region, ImageUtility.randomColor());
        imgView.setImageBitmap(bm);

        return imgView;
    }

    public GeometricObjectType getObjectType() {
        return objectType;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    private void onSelection() {
        if (listeners.size() > 0) {
            for (OnSelectionListener listener : listeners)
                listener.onSelection(new SelectionEvent(objectType));
        }
    }

    public void setOnSelectionListener(OnSelectionHandler handler) {
        listeners.add(new OnSelectionListener(handler));
    }

    // Event Handlers
    public interface OnSelectionHandler {
        public void onSelection(SelectionEvent event);
    }

    public final class SelectionEvent {
        public GeometricObjectType objectType = null;

        public SelectionEvent(GeometricObjectType objectType) {
            this.objectType = objectType;
        }

    }

    private final class OnSelectionListener implements OnSelectionHandler {

        private OnSelectionHandler handler = null;

        private OnSelectionListener(OnSelectionHandler handler) {
            this.handler = handler;
        }

        public void onSelection(SelectionEvent event) {
            handler.onSelection(event);
        }

    }

}
