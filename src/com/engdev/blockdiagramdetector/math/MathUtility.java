/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.math;

import android.graphics.Rect;
import com.engdev.blockdiagramdetector.geometry.Point;

import java.util.*;

/**
 * @author Lucas Batista
 */
public abstract class MathUtility {

    public static int clamp(int value, int low, int high) {
        return Math.max(Math.min(value, high), low);
    }

    public static double toDouble(String s) {
        if (s.length() == 0)
            return 0;
        else
            return Double.parseDouble(s);
    }

    public static int toInteger(String s) {
        if (s.length() == 0)
            return 0;
        else
            return Integer.parseInt(s);
    }

    public static float toDegrees(float rad) {
        return (float) (180 * rad / Math.PI);
    }

    public static float toRadians(float degrees) {
        return (float) (degrees * Math.PI / 180);
    }

    /**
     * Get all contained points. Use this with care as it goes through all points each time.
     * for memory efficiency.
     *
     * @return
     */
    public static List<Point> getContainedPoints(List<Point> vertices, Rect bounds) {
        List<Point> points = new ArrayList<Point>();
        Point point = new Point();
        for (int y = bounds.top; y <= bounds.bottom; y++) {
            for (int x = bounds.left; x <= bounds.right; x++) {
                point.x = x;
                point.y = y;
                if (PNPoly(vertices, point))
                    points.add(point.clone());
            }
        }
        return points;
    }

    /**
     * Finds whether a point is inside a polygon.
     * PNPoly by W. Randolph Franklin
     *
     * @param vertices
     * @param point
     * @return
     */
    public static boolean PNPoly(List<Point> vertices, Point point) {
        Point v1 = null;
        Point v2 = null;
        boolean flag = false;

        for (int i = 0, j = vertices.size() - 1; i < vertices.size(); j = i++) {
            v1 = vertices.get(i);
            v2 = vertices.get(j);
            if ((v1.y > point.y) != (v2.y > point.y)
                    && (point.x < (v2.x - v1.x) * (point.y - v1.y) / (v2.y - v1.y) + v2.x))
                flag = !flag;
        }

        return flag;
    }

    /**
     * Get all contained points. Use this with care as it goes through all points each time.
     * for memory efficiency. Works for convex simple polygons.
     *
     * @return
     */
    public static List<Point> getContainedPointsConvex(List<Point> contourPoints, Rect bounds) {

        List<Point> points = new ArrayList<Point>();
        List<Point> buffer = new ArrayList<Point>();
        Point point = new Point();

        for (Point p : contourPoints) {
            point.y = p.y;
            buffer.clear();
            for (int x = p.x + 1; x <= bounds.right; x++) {
                point.x = x;
                if (contourPoints.contains(point)) {
                    points.addAll(buffer);
                    break;
                }
                buffer.add(new Point(point.x, point.y));
            }
        }
        return points;
    }

    /**
     * Calculates the are of a polygon from Gauss formula.
     * Chain code independent
     *
     * @param c
     * @return
     */
    public static float area(List<Point> points) {

        float area = 0;

        if (points.size() > 0) {
            for (int i = 0, j = 1; j < points.size() - 1; i++, j++)
                area += points.get(i).x * points.get(j).y - points.get(j).x * points.get(i).y;
            area += points.get(points.size() - 1).x * points.get(0).y - points.get(0).x * points.get(points.size() - 1).y;
            area = Math.abs(area) / 2;
        }
        return area;
    }

    /**
     * Returns truth value whether a pint is contained in the bounds of a rectangle
     *
     * @param p
     * @param bounds
     * @return
     */
    public static boolean isContained(Point p, Rect bounds) {
        return p.x >= bounds.left && p.x <= bounds.right
                && p.y >= bounds.top && p.y <= bounds.bottom;
    }

    /**
     * Returns a new set with two points set added
     *
     * @param points1
     * @param points2
     * @return
     */
    public static Set<Point> add(Set<Point> points1, Set<Point> points2) {
        Set<Point> points = new HashSet<Point>(points1);
        points.addAll(points2);
        return points;
    }

    /**
     * Returns a new set with the subtraction of two points
     *
     * @param points1
     * @param points2
     * @return
     */
    public static Set<Point> subtract(Set<Point> points1, Set<Point> points2) {
        Set<Point> points = new HashSet<Point>(points1);
        points.removeAll(points2);
        return points;
    }

    /**
     * Returns a new set result of the intersection of two points set
     *
     * @param points1
     * @param points2
     * @return
     */
    public static Set<Point> intersection(Set<Point> points1, Set<Point> points2) {
        Set<Point> points = new HashSet<Point>(points1);
        Set<Point> complement = new HashSet<Point>();
        for (Point point : points) {
            if (!points2.contains(point))
                complement.add(point);
        }
        points.removeAll(complement);
        return points;
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
    public static float moment(int p, int q, Collection<Point> points) {
        float moment = 0;

        // Area is one
        for (Point point : points)
            moment += Math.pow(point.x, p) * Math.pow(point.y, q);
        return moment;
    }

    /**
     * Calculates the orientation of a region.
     *
     * @return
     */
    public static float orientation(Collection<Point> points, float area) {
        return (float) (0.5 * Math.atan(2 * MathUtility.centralMoment(1, 1, points, area) / (MathUtility.centralMoment(2, 0, points, area) - MathUtility.centralMoment(0, 2, points, area))));
    }

    /**
     * Rotate a set of points by angle in rad. Clockwise is assumed to be positive direction.
     *
     * @param points
     * @param angle
     * @return
     */
    public static Set<Point> rotate(Collection<Point> points, float angle) {
        Set<Point> rotated = new HashSet<Point>();
        for (Point point : points) {
            rotated.add(new Point(
                            (int) Math.round(point.x * Math.cos(angle) - point.y * Math.sin(angle)),
                            (int) Math.round(point.x * Math.sin(angle) + point.y * Math.cos(angle))
                    )
            );
        }
        return rotated;
    }

    /**
     * Calculates the central moment of the region of the contour.
     * It fist calculate the centroid and puts the coordinate origin at it.
     *
     * @param p
     * @param q
     * @return
     */
    public static float centralMoment(int p, int q, Collection<Point> points, float area) {
        float centralMoment = 0;

        float centroidX = MathUtility.moment(1, 0, points) / area;
        float centroidY = MathUtility.moment(0, 1, points) / area;

        for (Point point : points)
            centralMoment += Math.pow(point.x - centroidX, p) * Math.pow(point.y - centroidY, q);
        return centralMoment;
    }

    /**
     * Calculates the eign values of a region. Index 0 is the horizontal axis and 1 is the vertical.
     *
     * @param points
     * @param area
     * @return
     */
    public static float[] eignValues(Collection<Point> points, float area) {
        float[] eignValues = new float[2];
        float mi11 = centralMoment(1, 1, points, area);
        float mi20 = centralMoment(2, 0, points, area);
        float mi02 = centralMoment(0, 2, points, area);
        eignValues[0] = (float) (mi20 + mi02 + Math.sqrt(Math.pow(mi20 - mi02, 2) + 4 * Math.pow(mi11, 2))) / 2;
        eignValues[1] = (float) (mi20 + mi02 - Math.sqrt(Math.pow(mi20 - mi02, 2) + 4 * Math.pow(mi11, 2))) / 2;
        return eignValues;
    }

    /**
     * Caculates the region or set of points eccentricity
     *
     * @param points
     * @param area
     * @return
     */
    public static float eccentricity(Collection<Point> points, float area) {
        float[] eignValues = eignValues(points, area);
        return eignValues[0] / eignValues[1];
    }

    /**
     * Calculates the eign values of a region. Index 0 is the horizontal axis and 1 is the vertical.
     *
     * @param points
     * @param area
     * @return
     */
    public static float[] majorAxis(Set<Point> points, float area) {
        float[] axis = new float[2];
        float[] eignValues = eignValues(points, area);
        axis[0] = (float) (2 * Math.sqrt(eignValues[0] / area));
        axis[1] = (float) (2 * Math.sqrt(eignValues[1] / area));
        return axis;
    }

    /**
     * Calculates parameter by Euclidean distance.
     *
     * @return
     */
    public static float perimeterEuclidean(List<Point> points) {
        float perimeter = 0;
        for (int i = 0; i < points.size() - 2; i++) {
            perimeter += Math.sqrt(Math.pow(points.get(i).x - points.get(i + 1).x, 2)
                    + Math.pow(points.get(i).y - points.get(i + 1).y, 2));
        }
        perimeter += Math.sqrt(Math.pow(points.get(points.size() - 1).x - points.get(0).x, 2)
                + Math.pow(points.get(points.size() - 1).y - points.get(0).y, 2));
        return perimeter;
    }

    /**
     * Returns enclosing rectangle (bounding box) of a list of points.
     *
     * @return
     */
    public static Rect boundingBox(Collection<Point> points) {
        Rect bounds = new Rect();
        int left = (int) 1E6;
        int right = (int) -1E6;
        int top = (int) 1E6;
        int bottom = (int) -1E6;

        for (Point p : points) {
            if (p.x < left)
                left = p.x;
            if (p.x > right)
                right = p.x;
            if (p.y < top)
                top = p.y;
            if (p.y > bottom)
                bottom = p.y;
        }
        bounds = new Rect(left, top, right, bottom);
        return bounds;
    }

    /**
     * Translates a set of points
     *
     * @param points
     * @param vector
     * @return
     */
    public static Collection<Point> translate(Set<Point> points, Point vector) {
        Set<Point> translated = new HashSet<Point>();
        for (Point point : points)
            translated.add(new Point(point.x + vector.x, point.y + vector.y));
        return translated;
    }

    /**
     * The centroid of a set of points
     *
     * @return
     */
    public static Point centroid(Collection<Point> points, float area) {
        float centroidX = MathUtility.moment(1, 0, points) / area;
        float centroidY = MathUtility.moment(0, 1, points) / area;
        return new Point((int) centroidX, (int) centroidY);
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
    public static double normalCentralMoment(int p, int q, Collection<Point> points, float area) {
        double norm = Math.pow(1 / area, (p + q + 2) / 2);
        return centralMoment(p, q, points, area) * norm;
    }

    /**
     * Returns an array with the seven moments of Hu.
     *
     * @return
     */
    public static double[] huMoments(Collection<Point> points, float area) {
        double[] hu = new double[7];

        // Normal central moments
        double nu20 = normalCentralMoment(2, 0, points, area);
        double nu02 = normalCentralMoment(0, 2, points, area);
        double nu11 = normalCentralMoment(1, 1, points, area);
        double nu30 = normalCentralMoment(3, 0, points, area);
        double nu03 = normalCentralMoment(0, 3, points, area);
        double nu21 = normalCentralMoment(2, 1, points, area);
        double nu12 = normalCentralMoment(1, 2, points, area);

        // Calculates the moments
        hu[0] = Math.abs(nu20 + nu02);

        hu[1] = Math.abs(Math.pow(nu20 - nu02, 2) + 4 * Math.pow(nu11, 2));

        hu[2] = Math.abs(Math.pow(nu30 - 3 * nu12, 2) + Math.pow(nu03 - 3 * nu21, 2));

        hu[3] = Math.abs(Math.pow(nu30 + nu12, 2) + Math.pow(nu03 + nu21, 2));

        hu[4] = Math.abs((nu30 - 3 * nu12) * (nu30 + nu12) * (Math.pow(nu30 + nu12, 2) - 3 * Math.pow(nu21 + nu03, 2))
                + (nu03 - 3 * nu21) * (nu03 + nu21) * (Math.pow(nu03 + nu21, 2) - 3 * Math.pow(nu12 + nu30, 2)));

        hu[5] = Math.abs((nu20 - nu02) * (Math.pow(nu30 + nu12, 2) - Math.pow(nu21 + nu03, 2)) + 4 * nu11 * (nu30 + nu12) * (nu03 + nu21));

        hu[6] = Math.abs((3 * nu21 - nu03) * (nu30 + nu12) * (Math.pow(nu30 + nu12, 2) - 3 * Math.pow(nu21 + nu03, 2)) +
                (nu30 - 3 * nu12) * (nu21 + nu03) * (Math.pow(nu03 + nu21, 2) - 3 * Math.pow(nu30 + nu12, 2)));

        return hu;
    }

    /**
     * Finds the max radius from a collection points
     *
     * @param centroid
     * @return
     */
    public static float findMaxRadius(Collection<Point> points, Point centroid) {
        float radius = 0;
        for (Point point : points) {
            float distance = Point.distance(centroid, point);
            if (distance > radius)
                radius = distance;
        }

        return radius;
    }
}
