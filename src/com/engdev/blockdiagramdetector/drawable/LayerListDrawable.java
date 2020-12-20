/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Implements a list collection of drawables
 *
 * @author Lucas Batista
 */
public class LayerListDrawable extends Drawable {

    protected List<Drawable> layers = null;

    public LayerListDrawable() {
        layers = new ArrayList<Drawable>();
    }

    public boolean add(Drawable object) {
        return layers.add(object);
    }

    public boolean addAll(Collection<? extends Drawable> collection) {
        return layers.addAll(collection);
    }

    public void clear() {
        layers.clear();
    }

    public boolean contains(Drawable object) {
        return layers.contains(object);
    }

    public boolean containsAll(Collection<Drawable> arg0) {
        return layers.containsAll(arg0);
    }

    public boolean isEmpty() {
        return layers.isEmpty();
    }

    public Iterator<Drawable> iterator() {
        return layers.iterator();
    }

    public boolean remove(Drawable object) {
        return layers.remove(object);
    }

    public boolean removeAll(Collection<Drawable> collection) {
        return layers.removeAll(collection);
    }

    public int size() {
        return layers.size();
    }

    public Drawable[] toArray() {
        Drawable[] aLayers = new Drawable[layers.size()];
        for (int i = 0; i < layers.size(); i++)
            aLayers[i] = layers.get(i);
        return aLayers;
    }

    @Override
    public void draw(Canvas canvas) {
        for (Drawable d : layers)
            d.draw(canvas);
    }

    @Override
    public int getOpacity() {
        int opacity = 0;
        for (Drawable d : layers)
            opacity += d.getOpacity();
        return opacity;
    }

    @Override
    public void setAlpha(int alpha) {
        for (Drawable d : layers)
            d.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        for (Drawable d : layers)
            d.setColorFilter(cf);
    }
}
