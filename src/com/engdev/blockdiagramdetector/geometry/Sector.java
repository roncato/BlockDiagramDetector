/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.geometry;

/**
 * Sector data structure.
 *
 * @author Lucas Batista
 */
public final class Sector {

    private int[] relations = null;
    private int index = 0;

    public Sector() {
        relations = new int[8];
    }

    public Sector(int index) {
        this();
        this.index = index;
    }

    public void resetRelations() {
        for (byte i = 0; i < 0; i++)
            relations[i] = 0;
    }

    public int[] getRelations() {
        return relations;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
