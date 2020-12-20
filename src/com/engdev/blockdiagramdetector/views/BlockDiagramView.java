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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.engdev.blockdiagramdetector.blockdiagram.BlockDiagram;
import com.engdev.blockdiagramdetector.blockdiagram.BlockDiagramObject;
import com.engdev.blockdiagramdetector.io.IOUtility;

import java.io.IOException;

/**
 * Implements Block diagram view
 *
 * @author Lucas Batista
 */
public class BlockDiagramView extends View {
    private Context context = null;
    private BlockDiagram blockDiagram = null;
    private BitmapDrawable image = null;
    private float expansionFactor = 1.5F;
    private View matchViewSize = null;

    public BlockDiagramView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public BlockDiagramView(Context context, AttributeSet attr) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        image = new BitmapDrawable(context.getResources(), Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888));
        blockDiagram = new BlockDiagram();
    }

    public void add(BlockDiagramObject bdo, String name) {
        blockDiagram.add(bdo, name);
    }

    public BlockDiagramObject get(String name) {
        return blockDiagram.get(name);
    }

    public BitmapDrawable getImage() {
        return image;
    }

    public void setImage(BitmapDrawable image) {
        this.image = image;
    }

    public void setImage(Uri uri) throws IOException {
        Bitmap bm = IOUtility.getImage(uri, context.getContentResolver());
        setImage(bm);
    }

    public void setImage(Bitmap bm) {
        int imageWidth = bm.getWidth();
        int imageHeight = bm.getHeight();

        image = new BitmapDrawable(context.getResources(), bm);
        image.setBounds(0, 0, imageWidth, imageHeight);
        adjustSize(imageWidth, imageHeight);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable bg = getBackground();
        if (bg != null)
            bg.draw(canvas);
        image.draw(canvas);
        blockDiagram.draw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed)
            adjustSize(r - l, b - t);
    }

    @SuppressLint("NewApi")
    private void adjustSize(int oldWidth, int oldHeight) {

        // Children
        int width = (int) ((blockDiagram.getX() + blockDiagram.getWidth()) * expansionFactor);
        int height = (int) ((blockDiagram.getY() + blockDiagram.getHeight()) * expansionFactor);

        // Parent
        if (matchViewSize != null) {
            width = Math.max(width, matchViewSize.getWidth());
            height = Math.max(height, matchViewSize.getHeight());
        }

        width = Math.max(width, oldWidth);
        height = Math.max(height, oldHeight);

        LayoutParams params = getLayoutParams();
        params.width = width;
        params.height = height;

        // This is VERY important
        setLayoutParams(params);

        Drawable bg = getBackground();
        if (bg != null)
            bg.setBounds(0, 0, width, height);

    }

    public void setMatchViewSize(View v) {
        this.matchViewSize = v;
    }

    public float getExpansionFactor() {
        return expansionFactor;
    }

    public void setExpansionFactor(int expansionFactor) {
        this.expansionFactor = expansionFactor;
    }

    public void clear() {
        blockDiagram.clear();
        image = new BitmapDrawable(context.getResources(), Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888));
        invalidate();
    }

    public BlockDiagram getBlockDiagram() {
        return blockDiagram;
    }

}

