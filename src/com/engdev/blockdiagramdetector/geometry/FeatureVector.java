/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.geometry;

import java.util.Set;

/**
 * Data Structure of a feature vector
 *
 * @author Engineer
 */
public class FeatureVector {

    private Track[] tracks = null;
    private int numberOfTracks = 0;
    private int numberOfSectors = 0;
    public FeatureVector(int numberOfTracks, int numberOfSectors) {
        this.numberOfTracks = numberOfTracks;
        this.numberOfSectors = numberOfSectors;
        buildTracks();
    }

    /**
     * Get the EculidianDistance between two Feature vectors
     *
     * @param r1
     * @param r2
     * @return
     */
    public static float getEculidianDistance(FeatureVector v1, FeatureVector v2) {

        if (v1.getNumberOfTracks() != v2.getNumberOfTracks())
            throw new IllegalArgumentException("Attempt Eculiadian distance calculation with different number of tracks.");

        if (v1.getNumberOfSectors() != v2.getNumberOfSectors())
            throw new IllegalArgumentException("Attempt Eculiadian distance calculation with different number of sectors");

        float rels1 = 0;
        float rels2 = 0;
        float diff = 0;
        float distance = 0;
        for (int i = 0; i < v1.getNumberOfTracks(); i++) {
            for (int j = 0; j < v1.getNumberOfSectors(); j++) {
                for (byte k = 0; k < 8; k++) {
                    rels1 = v1.getTracks()[i].getSectors()[j].getRelations()[k];
                    rels2 = v1.getTracks()[i].getSectors()[j].getRelations()[k];
                    diff = rels1 - rels2;
                    diff *= diff;
                    distance += diff;
                }
            }
        }
        return distance;
    }

    private void buildTracks() {
        tracks = new Track[numberOfTracks];
        for (int i = 0; i < numberOfTracks; i++)
            tracks[i] = new Track(i, numberOfSectors);
    }

    public void extractFeatures(Region region) {
        Set<Point> points = region.getPoints();
        Point centroid = region.centroid();
        float maxRadius = region.getMaxRadius();
        int trackIndex = 0;
        int sectorIndex = 0;
        byte relation = 0;

        // Resets
        for (Point point : points) {
            trackIndex = Point.getTrackIndex(centroid, point, maxRadius, numberOfTracks);
            sectorIndex = Point.getSectorIndex(centroid, point, numberOfSectors);
            tracks[trackIndex].getSectors()[sectorIndex].resetRelations();
        }

        // Populates
        for (Point point : points) {
            trackIndex = Point.getTrackIndex(centroid, point, maxRadius, numberOfTracks);
            sectorIndex = Point.getSectorIndex(centroid, point, numberOfSectors);
            relation = Point.relation(point, points);
            tracks[trackIndex].getSectors()[sectorIndex].getRelations()[relation]++;
        }
    }

    public float getEculidianDistance(FeatureVector vector) {
        return getEculidianDistance(this, vector);
    }

    public Track[] getTracks() {
        return tracks;
    }

    public int getNumberOfTracks() {
        return numberOfTracks;
    }

    public int getNumberOfSectors() {
        return numberOfSectors;
    }

}
