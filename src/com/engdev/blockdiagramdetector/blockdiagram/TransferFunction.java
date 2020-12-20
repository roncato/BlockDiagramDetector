/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.blockdiagram;

import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import com.engdev.blockdiagramdetector.drawable.LabelDrawable;
import com.engdev.blockdiagramdetector.drawable.LayerListDrawable;
import com.engdev.blockdiagramdetector.drawable.LineDrawable;

/**
 * Implements a Transfer function component
 *
 * @author Lucas Batista
 */
public class TransferFunction extends BlockDiagramComponent {

    private static final int DIV_LINE_OFFSET = 25;
    private static final int NUM_OFFSET = 25;
    private static final int DEN_OFFSET = 20;

    private int borderWidth = 5;
    private ShapeDrawable outerRect = null;
    private ShapeDrawable innerRect = null;
    private LabelDrawable numLabel = null;
    private LabelDrawable denLabel = null;
    private LineDrawable divLine = null;

    public TransferFunction() {
        init();
    }

    private void init() {
        initDrawing();
    }

    private void initDrawing() {
        drawing = new LayerListDrawable();
        buildDrawing();
    }

    private void buildDrawing() {
        LayerListDrawable drawing = (LayerListDrawable) this.drawing;
        outerRect = new ShapeDrawable(new RectShape());
        innerRect = new ShapeDrawable(new RectShape());
        numLabel = new LabelDrawable("n(s)");
        denLabel = new LabelDrawable("d(s)");
        divLine = new LineDrawable();

        setBorderColor(0xFF000000);
        setFillingColor(0xFFFFFFFF);
        setTextColor(0xFF000000);

        // Add to collection
        drawing.add(outerRect);
        drawing.add(innerRect);
        drawing.add(divLine);
        drawing.add(numLabel);
        drawing.add(denLabel);

    }

    @Override
    public void onResize() {
        invalidate();
    }

    @Override
    public void onMove() {
        invalidate();
    }

    @Override
    public void invalidate() {

        int textSize = (int) numLabel.getTextSize();
        int numTextWidth = (int) numLabel.getPaint().measureText(numLabel.getText());
        int denTextWidth = (int) denLabel.getPaint().measureText(denLabel.getText());
        int textWidth = (int) Math.max(numTextWidth, denTextWidth);
        int labelX = 0;
        int labelY = 0;
        Rect rect = drawing.getBounds();

        // Rectangle boundaries
        outerRect.setBounds(rect.left, rect.top, rect.left + rect.width(), rect.top + rect.height());
        innerRect.setBounds(rect.left + borderWidth, rect.top + borderWidth,
                rect.left + rect.width() - borderWidth, rect.top + rect.height() - borderWidth);

        // DivisionLine
        divLine.setBounds(rect.left + (rect.width() - textWidth) / 2 - DIV_LINE_OFFSET,
                rect.top + rect.height() / 2, rect.left + rect.width() / 2 + DIV_LINE_OFFSET + textWidth / 2, rect.top + rect.height() / 2);

        // Numerator label
        labelX = rect.left + (rect.width() - numTextWidth) / 2;
        labelY = rect.top + rect.height() / 2 - NUM_OFFSET;
        numLabel.setBounds(labelX, labelY, labelX + textSize, labelY + textSize);

        // Denominator label
        labelX = rect.left + (rect.width() - denTextWidth) / 2;
        labelY = rect.top + rect.height() / 2 + textSize + DEN_OFFSET;
        denLabel.setBounds(labelX, labelY, labelX + textSize, labelY + textSize);

    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public void setBorderColor(int color) {
        outerRect.getPaint().setColor(color);
    }

    public void setFillingColor(int color) {
        innerRect.getPaint().setColor(color);
    }

    public void setTextColor(int color) {
        numLabel.getPaint().setColor(color);
        denLabel.getPaint().setColor(color);
        divLine.getPaint().setColor(color);
    }

    public String getNumerator() {
        return numLabel.getText();
    }

    public void setNumerator(String text) {
        numLabel.setText(text);
    }

    public String getDenominator() {
        return denLabel.getText();
    }

    public void setDenominator(String text) {
        denLabel.setText(text);
    }

}
