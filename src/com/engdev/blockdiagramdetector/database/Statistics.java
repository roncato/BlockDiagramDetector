/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.database;

/**
 * Data structure for Statistics
 *
 * @author Lucas Batista
 */
public class Statistics {
    public float mean = 0;
    public float stdev = 0;

    public Statistics() {
    }

    public Statistics(float mean, float stdev) {
        this.mean = mean;
        this.stdev = stdev;
    }

    @Override
    public String toString() {
        return "{mean=" + mean + ", stdev=" + stdev + "}";
    }

}
