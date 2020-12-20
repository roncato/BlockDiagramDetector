/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.geometry;

import android.graphics.Bitmap;
import android.graphics.Rect;
import com.engdev.blockdiagramdetector.imageprocessing.ImageUtility;
import com.engdev.blockdiagramdetector.math.MathUtility;

import java.util.*;

/**
 * Represents a geometric region. It is immutable although operation on points can still be performed.
 * Future releases will make this region mutable.
 *
 * @author Lucas Batista
 */
public final class Region implements Observer {

    public final static int DEFAULT_NUMBER_OF_TRACKS = 6;
    public final static int DEFAULT_NUMBER_OF_SECTORS = 4;
    private Contour contour = null;
    private List<Region> innerRegions = null;
    private Set<Point> points = null;
    private boolean isEmptyRegion = false;
    private RegionState state = null;
    private FeatureVector featureVector = null;

    private Region(Contour contour) {
        this.contour = contour;
        this.innerRegions = new ArrayList<Region>();
        state = RegionState.Partial;
    }

    /**
     * To be implemented. Fuses two region together and returns a new region.
     *
     * @param region1
     * @param region2
     * @return
     */
    public static Region fusion(Region region1, Region region2) {
        return null;
    }

    /**
     * To be implemented. Subtract two regions together and returns a new region.
     *
     * @param region1
     * @param region2
     * @return
     */
    public static Region substract(Region region1, Region region2) {
        return null;
    }

    /**
     * To be implemented. Returns a the intersection of two regions.
     *
     * @param region1
     * @param region2
     * @return
     */
    public static Region intersect(Region region1, Region region2) {
        return null;
    }

    /**
     * Checks weather a region is contained on the other one.
     *
     * @param r1
     * @param r2
     * @return
     */
    public static boolean isContained(Region r1, Region r2) {
        return Contour.isContained(r1.getContour(), r2.getContour());
    }

    /**
     * Creates a bitmap of the region
     *
     * @param region
     * @param color
     * @return
     */
    public static Bitmap createImageBitmap(Region region, int color) {

        Rect bounds = region.getContour().getBoundingBox();
        int[] pixels = new int[(bounds.width() + 1) * (bounds.height() + 1)];
        Set<Point> points = region.getPoints();
        int index = 0;
        for (Point point : points) {
            int x = point.x - bounds.left;
            int y = point.y - bounds.top;
            index = ImageUtility.getIndex(x, y, bounds.width());
            pixels[index] = color;
        }

        return Bitmap.createBitmap(pixels, bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
    }

    /**
     * Creates enclosing region
     *
     * @return
     */
    public static Region createRegion(Contour contour) {
        return new Region(contour.clone());
    }

    /**
     * Builds Region points.
     */
    private void buildPoints() {
        points = new HashSet<Point>(contour.getPoints());
        state = RegionState.Built;
    }

    /**
     * Fetch points from contour is case the cache is invalid
     */
    private void fetchPoints() {
        if (state == RegionState.Partial)
            buildPoints();
    }

    /**
     * Get Points from the region. Lazy method will defer points filling until it is empty
     *
     * @return
     */
    public Set<Point> getPoints() {
        if (state == RegionState.Partial) {
            buildPoints();
            for (Region region : innerRegions) {
                Set<Point> intersectionPoints = MathUtility.intersection(points, region.getPoints());
                points.removeAll(intersectionPoints);
            }
        }
        return points;
    }

    /**
     * Returns the perimeter of the region
     *
     * @return
     */
    public float perimeter() {
        return contour.perimeter();
    }

    /**
     * Returns the area of the region
     *
     * @return
     */
    public float area() {
        fetchPoints();
        float area = points.size();
        for (Region region : innerRegions) {
            if (region.isEmptyRegion())
                area -= region.area();
        }
        return area;
    }

    /**
     * Calculates the central moment of the region of the contour.
     * It fist calculate the centroid and puts the coordinate origin at it.
     *
     * @param p
     * @param q
     * @return
     */
    public float centralMoment(int p, int q) {
        fetchPoints();
        return MathUtility.centralMoment(p, q, points, area());
    }

    /**
     * Calculates the normal central moment of the region of the contour i.e. the
     * central moment normalized by its region area.
     * It fist calculate the centroid and puts the coordinate origin at it.
     *
     * @param p
     * @param q
     * @return
     */
    public double normalCentralMoment(int p, int q) {
        fetchPoints();
        return MathUtility.normalCentralMoment(p, q, points, area());
    }

    /**
     * Returns an array with the seven moments of Hu.
     *
     * @return
     */
    public double[] huMoments() {
        return MathUtility.huMoments(points, area());
    }

    /**
     * Calculates the orientation of the region of the contour.
     *
     * @return
     */
    public float orientation() {
        fetchPoints();
        return MathUtility.orientation(points, area());
    }

    /**
     * Measure of compactness. Informs the difference between the perimeter and the area of
     * the region.
     *
     * @return
     */
    public float compactness() {
        return (float) (Math.pow(perimeter(), 2) / area());
    }

    /**
     * Calculates the circularity of the region
     *
     * @return
     */
    public float circularity() {
        return (float) (4 * Math.PI * area() / Math.pow(perimeter(), 2));
    }

    /**
     * The centroid coordinates of the region
     *
     * @return
     */
    public Point centroid() {
        fetchPoints();
        float area = area();
        float centroidX = MathUtility.moment(1, 0, points) / area;
        float centroidY = MathUtility.moment(0, 1, points) / area;
        return new Point((int) centroidX, (int) centroidY);
    }

    /**
     * Calculates the moment of the region made up by the contour.
     * The centroid can be derived from the moment by dividing it by the area.
     *
     * @param p      X exponent
     * @param q      Y exponent
     * @param points
     * @return
     */
    public float moment(int p, int q) {
        fetchPoints();
        return MathUtility.moment(p, q, points);
    }

    /**
     * Adds a inner region
     *
     * @param region
     * @return
     */
    public boolean addInnerRegion(Region region) {
        state = RegionState.Partial;
        return innerRegions.add(region);
    }

    /**
     * Remove a inner region
     *
     * @param region
     * @return
     */
    public boolean removeInnerRegion(Region region) {
        state = RegionState.Partial;
        return innerRegions.remove(region);
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        if (arg0.equals(contour))
            state = RegionState.Partial;
    }

    /**
     * Returns the Euler number of the region
     *
     * @return
     */
    public int eulerNumber() {
        return 1 - getNumberOfHoles();
    }

    /**
     * Number or empty regions
     *
     * @return
     */
    public int getNumberOfHoles() {
        int nHoles = 0;
        for (Region region : innerRegions) {
            if (region.isEmptyRegion())
                nHoles++;
        }
        return nHoles;
    }

    /**
     * Calculates the eign values of a region. Index 0 is the horizontal axis and 1 is the vertical.
     *
     * @param points
     * @param area
     * @return
     */
    public float[] majorAxis() {
        float[] axis = new float[2];
        float area = area();
        float[] eignValues = MathUtility.eignValues(points, area);
        axis[0] = (float) (2 * Math.sqrt(eignValues[0] / area));
        axis[1] = (float) (2 * Math.sqrt(eignValues[1] / area));
        return axis;
    }

    /**
     * Calculates the eccentricity of the region
     *
     * @return
     */
    public float eccentricity() {
        fetchPoints();
        return MathUtility.eccentricity(points, area());
    }

    public float getMaxRadius() {
        return MathUtility.findMaxRadius(contour.getContourPoints(), centroid());
    }

    @Override
    public Region clone() {
        return new Region(contour.clone());
    }

    @Override
    public boolean equals(Object object) {

        boolean equals = true;

        if (object instanceof Region) {
            Set<Point> thisPoints = getPoints();
            Set<Point> thatPoints = ((Region) object).getPoints();
            if (thisPoints.size() == thatPoints.size()) {
                for (Point p : thisPoints)
                    equals &= thatPoints.contains(p);
            } else
                equals = false;
        } else
            equals = false;

        return equals;
    }

    /**
     * Gets the reference of its contour. Any change on its contour will cascated back to the region.
     *
     * @return
     */
    public Contour getContour() {
        return contour;
    }

    /**
     * Gets inner region
     *
     * @return
     */
    public List<Region> getInnerRegions() {
        return innerRegions;
    }

    /**
     * Sets inner regions
     *
     * @param regions
     */
    public void setInnerRegions(List<Region> regions) {
        this.innerRegions = regions;
        state = RegionState.Partial;
    }

    /**
     * Truth value whether this is an empty region
     *
     * @return
     */
    public boolean isEmptyRegion() {
        return isEmptyRegion;
    }

    /**
     * Sets whether this region is a empty region
     *
     * @param empty
     */
    public void setEmptyRegion(boolean empty) {
        this.isEmptyRegion = empty;
    }

    /**
     * Returns the feature vector for the region
     *
     * @return
     */
    public FeatureVector getFeatureVector() {
        if (featureVector == null) {
            featureVector = new FeatureVector(DEFAULT_NUMBER_OF_TRACKS, DEFAULT_NUMBER_OF_SECTORS);
            featureVector.extractFeatures(this);
        } else if (state == RegionState.Partial)
            featureVector.extractFeatures(this);

        return featureVector;
    }

    /**
     * Get Eculidian distance between this and that region
     *
     * @param region
     * @return
     */
    public float getEculidianDistance(Region region) {
        return getFeatureVector().getEculidianDistance(region.getFeatureVector());
    }

    @Override
    public String toString() {
        Rect bounds = contour.getBoundingBox();
        Point centroid = centroid();
        return "{" + bounds.toString() + ", " + centroid + "}";
    }

    public static enum RegionState {
        Built,
        Partial
    }

}
