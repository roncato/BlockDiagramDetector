/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.geometry;

import com.engdev.blockdiagramdetector.math.MathUtility;

import java.util.Set;

public final class Point {
    public int x = 0;
    public int y = 0;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    public static float distance(Point p1, Point p2) {
        return (float) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public static byte relation(Point point, Set<Point> points) {
        Point neighboor = new Point();
        byte relation = 0;

        if (!points.contains(neighboor.setLocation(point.x, point.y - 1)))
            relation = 0;
        else if (!points.contains(neighboor.setLocation(point.x + 1, point.y - 1)))
            relation = 1;
        else if (!points.contains(neighboor.setLocation(point.x + 1, point.y)))
            relation = 2;
        else if (!points.contains(neighboor.setLocation(point.x + 1, point.y + 1)))
            relation = 3;
        else if (!points.contains(neighboor.setLocation(point.x, point.y + 1)))
            relation = 4;
        else if (!points.contains(neighboor.setLocation(point.x - 1, point.y + 1)))
            relation = 5;
        else if (!points.contains(neighboor.setLocation(point.x - 1, point.y)))
            relation = 6;
        else if (!points.contains(neighboor.setLocation(point.x - 1, point.y - 1)))
            relation = 7;

        return relation;
    }

    public static int getSectorIndex(Point centroid, Point point, int numberOfSectors) {

        if (numberOfSectors == 0)
            throw new IllegalArgumentException("Number of sectors cannot be zero.");

        float dx = point.x - centroid.x;
        float dy = point.y - centroid.y;
        float theta = 0;

        if (dx == 0 && dy == 0)
            theta = 0;
        else if (dx == 0 && dy > 0)
            theta = 90;
        else if (dx == 0 && dy < 0)
            theta = 270;
        else if (dx > 0 && dy == 0)
            theta = 0;
        else if (dx < 0 && dy == 0)
            theta = 180;
        else {

            // Gets angle
            theta = MathUtility.toDegrees((float) Math.abs(Math.atan(dy / dx)));

            // Finds quadrant
            if (dy > 0 && dx < 0)
                theta = 180 - theta;
            else if (dy < 0 && dx < 0)
                theta = 180 + theta;
            else if (dy < 0 && dx > 0)
                theta = 360 - theta;

        }

        // Sector delta theta and index
        int dTheta = 360 / numberOfSectors;
        int index = (int) Math.floor(theta / dTheta);

        return index;
    }

    public static int getTrackIndex(Point centroid, Point point, float maxRadius, int numberOfTracks) {

        if (numberOfTracks <= 0)
            throw new IllegalArgumentException("Number of tracks must be greater than 0.");

        float dR = maxRadius / numberOfTracks;
        float deltaR = distance(point, centroid);

        int index = (int) Math.floor(deltaR / dR);

        // the singularity of the max radius
        if (index == numberOfTracks)
            index--;

        return index;
    }

    public Point setLocation(Point point) {
        x = point.x;
        y = point.y;
        return this;
    }

    public Point setLocation(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public Point clone() {
        return new Point(x, y);
    }

    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point) {
            Point p = (Point) o;
            return p.x == x && p.y == y;
        } else
            return false;
    }

}
