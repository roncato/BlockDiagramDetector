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
 * Track Data Structure
 *
 * @author Lucas Batista
 */
public class Track {

    private final int numberOfSectors;

    private Sector[] sectors;
    private int index = 0;

    public Track(int index, int numberOfSectors) {
        this.index = index;
        this.numberOfSectors = numberOfSectors;
        buildSectors();
    }

    public Track(int numberOfSectors) {
        this(0, numberOfSectors);
    }

    private void buildSectors() {
        sectors = new Sector[numberOfSectors];
        for (int i = 0; i < numberOfSectors; i++)
            sectors[i] = new Sector(i);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Sector[] getSectors() {
        return sectors;
    }

}
