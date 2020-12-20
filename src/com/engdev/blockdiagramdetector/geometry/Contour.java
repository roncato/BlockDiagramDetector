/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.geometry;

import android.graphics.Rect;
import com.engdev.blockdiagramdetector.math.MathUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * A collection of Links.
 *
 * @author Lucas Batista
 */
public final class Contour extends Observable {

    public final static float SQRT2 = 1.414213562F;
    public final static float PERIMETER_FACTOR = 0.95F;
    private Point startPoint = null;
    private List<Byte> codes = null;
    private Rect bounds = null;
    private MarshDirection marshDirection = MarshDirection.CounterClockwise;

    public Contour(Point startPoint, MarshDirection marshDirection) {
        this.startPoint = new Point(startPoint.x, startPoint.y);
        codes = new ArrayList<Byte>();
        this.marshDirection = marshDirection;
    }

    public Contour(Point startPoint, List<Byte> codes, MarshDirection marshDirection) {
        this(startPoint, marshDirection);
        this.codes = codes;
    }

    public static int toInteger(ChainCode c) {
        int index = 0;
        ChainCode[] values = ChainCode.values();
        for (ChainCode code : values) {
            if (code == c)
                return index;
            index++;
        }
        return index;
    }

    public static ChainCode toChainCode(int index) {
        ChainCode[] values = ChainCode.values();
        if (index >= values.length && index < 0)
            throw new IllegalArgumentException();
        return values[index];
    }

    /**
     * Returns the derivative of two Chain codes
     *
     * @param code1
     * @param code2
     * @return
     */
    public static ChainCode derivate(ChainCode code1, ChainCode code2) {
        int c1 = toInteger(code1);
        int c2 = toInteger(code2);
        int derivate = 0;

        while (true) {
            if (++c1 % 8 == c2)
                return toChainCode(derivate);
            derivate++;
        }
    }

    /**
     * Returns the derivative of two Chain codes
     *
     * @param code1
     * @param code2
     * @return
     */
    public static byte derivate(short code1, short code2) {
        byte derivate = 0;
        while (true) {
            if (++code1 % 8 == code2)
                return derivate;
            derivate++;
        }
    }

    /**
     * Gets Chain based on prev and current point.
     * y axis is pointing downwards and x points to the right
     *
     * @param fromPoint
     * @param toPoint
     * @return
     */
    public static ChainCode getChainCodeEnum(Point fromPoint, Point toPoint) {
        if (toPoint.x == fromPoint.x) {
            if (toPoint.y > fromPoint.y)
                return ChainCode.South;
            else if (toPoint.y < fromPoint.y)
                return ChainCode.North;
            else
                return ChainCode.Datum;
        } else if (toPoint.x < fromPoint.x) {
            if (toPoint.y > fromPoint.y)
                return ChainCode.SouthWest;
            else if (toPoint.y < fromPoint.y)
                return ChainCode.NorthWest;
            else
                return ChainCode.West;
        } else {
            if (toPoint.y > fromPoint.y)
                return ChainCode.SouthEast;
            else if (toPoint.y < fromPoint.y)
                return ChainCode.NorthEast;
            else
                return ChainCode.East;
        }
    }

    /**
     * Gets chain code from on point to the other
     *
     * @param fromPoint
     * @param toPoint
     * @return
     */
    public static byte getChainCode(Point fromPoint, Point toPoint) {
        if (toPoint.x == fromPoint.x) {
            if (toPoint.y > fromPoint.y)
                return 6;
            else if (toPoint.y < fromPoint.y)
                return 2;
            else
                return 8;
        } else if (toPoint.x < fromPoint.x) {
            if (toPoint.y > fromPoint.y)
                return 5;
            else if (toPoint.y < fromPoint.y)
                return 3;
            else
                return 4;
        } else {
            if (toPoint.y > fromPoint.y)
                return 7;
            else if (toPoint.y < fromPoint.y)
                return 1;
            else
                return 0;
        }
    }

    /**
     * Returns a new built derivative contour or the original.
     *
     * @param contour
     * @return
     */
    public static Contour derivate(Contour contour) {
        Contour derivative = new Contour(contour.startPoint, contour.getMarshDirection());
        List<Byte> codes = contour.getCodes();
        short c1 = -1;
        short c2 = -1;

        for (int i = 0; i < contour.size() - 2; i++) {
            c1 = codes.get(i);
            c2 = codes.get(i + 1);
            derivative.addChainCode(derivate(c1, c2));
        }

        c1 = codes.get(codes.size() - 1);
        c2 = codes.get(0);
        derivative.addChainCode(derivate(c1, c2));

        return derivative;
    }

    /**
     * Wheater a contour is contained on the other
     *
     * @param c1
     * @param c2
     * @return
     */
    public static boolean isContained(Contour c1, Contour c2) {

        Rect bounds1 = c1.getBoundingBox();
        Rect bounds2 = c2.getBoundingBox();

        return bounds2.left >= bounds1.left && bounds2.right <= bounds1.right
                && bounds2.top >= bounds1.top && bounds2.bottom <= bounds1.bottom;

    }

    /**
     * Gets point from Chain code
     *
     * @param fromPoint
     * @param code
     * @return
     */
    public static Point getPoint(Point fromPoint, short code) {
        Point p = null;
        switch (code) {
            case 0:
                p = new Point(fromPoint.x + 1, fromPoint.y);
                break;
            case 1:
                p = new Point(fromPoint.x + 1, fromPoint.y - 1);
                break;
            case 2:
                p = new Point(fromPoint.x, fromPoint.y - 1);
                break;
            case 3:
                p = new Point(fromPoint.x - 1, fromPoint.y - 1);
                break;
            case 4:
                p = new Point(fromPoint.x - 1, fromPoint.y);
                break;
            case 5:
                p = new Point(fromPoint.x - 1, fromPoint.y + 1);
                break;
            case 6:
                p = new Point(fromPoint.x, fromPoint.y + 1);
                break;
            case 7:
                p = new Point(fromPoint.x + 1, fromPoint.y + 1);
                break;
            default:
                p = new Point(fromPoint.x, fromPoint.y);
        }
        return p;
    }

    public static int getIndex(int x, int y, int width) {
        return x + y * width;
    }

    /**
     * Wheater the point is contained or not.
     *
     * @param p
     * @return
     */
    public static boolean isContained(Point p, List<Point> points) {
        return points.contains(p);
    }

    /**
     * Computes the perimeter of a region from its chain code.
     *
     * @param codes
     * @return
     */
    public static float perimeter(List<Byte> codes) {
        float p = 0;

        for (byte code : codes) {
            if (code % 2F == 0)
                p++;
            else
                p += SQRT2;
        }
        return p * PERIMETER_FACTOR;
    }

    /**
     * Calculates are from its chain codes. Uses Pick's theorem.
     * The points harvesting may make it a little inefficient.
     *
     * @param codes
     * @return
     */
    public static float areaPick(Contour c) {
        float area = c.getContainedPoints().size();
        List<Byte> codes = c.getCodes();
        area += codes.size() / 2 - 1;
        return area;
    }

    /**
     * Caculates the area of a polygon from its chain code
     *
     * @param c
     * @return
     */
    public static float area(List<Byte> codes, MarshDirection direction) {
        if (direction == MarshDirection.Clockwise)
            return areaClockwise(codes);
        else
            return areaCounterclockwise(codes);
    }

    /**
     * Calculates are from its chain codes. Assumes extraction was clock wise.
     *
     * @param codes
     * @return
     */
    private static float areaClockwise(List<Byte> codes) {

		/* Base is always 1 so just need the value of y.
		   Approximates the area based on the middle value of point for
		   for diagonals.
		*/

        int y = 0;
        float area = 0F;

        for (byte code : codes) {
            switch (code) {
                case 0:
                    area += y;
                    break;
                case 1:
                    area += (y + 0.5);
                    y++;
                    break;
                case 2:
                    y++;
                    break;
                case 3:
                    area -= (y + 0.5);
                    y++;
                    break;
                case 4:
                    area -= y;
                    break;
                case 5:
                    area -= (y - 0.5);
                    y--;
                    break;
                case 6:
                    y--;
                    break;
                case 7:
                    area += (y - 0.5);
                    y--;
                    break;
            }
        }
        return area;
    }

    /**
     * Calculates are from its chain codes.
     * Assumes the data contour extraction was counter clockwise
     *
     * @param codes
     * @return
     */
    private static float areaCounterclockwise(List<Byte> codes) {

		/* Base is always 1 so just need the value of y.
		   Approximates the area based on the middle value of point for
		   for diagonals.
		*/

        int y = 0;
        float area = 0F;

        for (byte code : codes) {
            switch (code) {
                case 0:
                    area -= y;
                    break;
                case 1:
                    area -= (y + 0.5);
                    y++;
                    break;
                case 2:
                    y++;
                    break;
                case 3:
                    area += (y + 0.5);
                    y++;
                    break;
                case 4:
                    area += y;
                    break;
                case 5:
                    area += (y - 0.5);
                    y--;
                    break;
                case 6:
                    y--;
                    break;
                case 7:
                    area -= (y - 0.5);
                    y--;
                    break;
            }
        }
        return area;
    }

    public Point first() {
        return startPoint;
    }

    /**
     * Calculates parameter considering the edges
     *
     * @return
     */
    public float perimeter() {
        return perimeter(codes);
    }

    /**
     * The number of chain codes of the contour.
     *
     * @return
     */
    public int size() {
        return codes.size();
    }

    /**
     * Returns the area of the region made by the contour.
     *
     * @return
     */
    public float area() {
        return getPoints().size();
    }

    /**
     * Measure of the effort necessary to bend the contour.
     *
     * @return
     */
    public float bendingEnergy() {
        float energy = 0;
        for (Byte code : codes)
            energy += Math.pow(code, 2);
        return energy / perimeter();
    }

    public List<Point> getVertices() {
        List<Point> points = getContourPoints();
        List<Point> vertices = new ArrayList<Point>();
        for (int i = 0; i < size() - 2; i++) {
            if (codes.get(i) != codes.get(i + 1))
                vertices.add(points.get(i));
        }
        return vertices;
    }

    public boolean addChainCode(byte code) {
        return codes.add(code);
    }

    public short removeChainCode(int location) {
        return codes.remove(location);
    }

    public List<Byte> getCodes() {
        return codes;
    }

    public List<Point> getContourPoints() {
        Point p = null;
        Point fromPoint = new Point(startPoint.x, startPoint.y);
        List<Point> points = new ArrayList<Point>();

        // Adds clone of start point
        points.add(fromPoint);

        for (int i = 0; i < codes.size() - 2; i++) {
            p = getPoint(fromPoint, codes.get(i + 1));
            points.add(p);
            fromPoint = p;
        }
        points.add(getPoint(fromPoint, codes.get(codes.size() - 1)));
        return points;
    }

    /**
     * Get all contained points. Use this with care as it goes through all points each time.
     * for memory efficiency.
     *
     * @return
     */
    public List<Point> getContainedPoints() {
        return MathUtility.getContainedPoints(getContourPoints(), getBoundingBox());
    }

    public List<Point> getPoints() {
        List<Point> points = getContourPoints();
        points.addAll(getContainedPoints());
        return points;
    }

    public Rect getBoundingBox() {
        if (bounds == null)
            bounds = MathUtility.boundingBox(getContourPoints());
        return bounds;
    }

    /**
     * Whether the point is contained or not. This method uses getContainedPoints
     *
     * @param p
     * @return
     */
    public boolean isContained(Point point) {
        List<Point> points = getPoints();
        return isContained(point, points);
    }

    @Override
    public Contour clone() {
        List<Byte> codes = new ArrayList<Byte>();
        for (byte c : this.codes)
            codes.add(c);
        return new Contour(startPoint.clone(), codes, marshDirection);
    }

    @Override
    public boolean equals(Object contour) {
        boolean equals = false;

        if (contour instanceof Contour) {
            List<Point> thisPoints = getPoints();
            List<Point> thatPoints = ((Contour) contour).getPoints();
            if (thisPoints.size() == thatPoints.size()) {
                for (Point p : thisPoints)
                    equals &= thatPoints.contains(p);
            }
        }
        return equals;
    }

    public MarshDirection getMarshDirection() {
        return marshDirection;
    }

    public void setMarshDirection(MarshDirection marshDirection) {
        this.marshDirection = marshDirection;
    }

    public static enum ChainCode {
        East,            //0
        NorthEast,        //1
        North,            //2
        NorthWest,        //3
        West,            //4
        SouthWest,        //5
        South,            //6
        SouthEast,        //7
        Datum            //8
    }

    public static enum MarshDirection {
        Clockwise,
        CounterClockwise
    }

}
