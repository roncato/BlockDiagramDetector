/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.tracer;

import android.graphics.Rect;
import com.engdev.blockdiagramdetector.exceptions.PerceptronException;
import com.engdev.blockdiagramdetector.geometry.Contour;
import com.engdev.blockdiagramdetector.geometry.Point;
import com.engdev.blockdiagramdetector.imageprocessing.EdgeMap;

import java.util.List;

/**
 * Abstract class that defines a perceptron
 *
 * @author Lucas Batista
 */
public abstract class ContourTracer implements Tracer {

    protected EdgeMap map = null;
    protected EdgeMap exploredMap = null;

    ;
    protected Point startPoint = null;
    protected Point currPoint = null;
    protected Orientation orientation = Orientation.South;
    protected int exploredColor = 0xFFCCCCCC;
    protected Contour contour = null;
    protected Rect bounds = null;
    protected Orientation startOrientation = null;
    protected TracingResult result = null;

    /**
     * Percepts contour
     *
     * @throws PerceptronException
     */
    public abstract TracingResult trace();

    /**
     * Contour object. Returns true if successful
     *
     * @return
     */
    protected abstract boolean contour();

    /**
     * This method returns true if the successful criterion has been satisfied
     * The default is Jacob's stopping criterion
     *
     * @return
     */
    protected boolean isSuccessfulStopCriterion() {
        return currPoint.equals(startPoint) && orientation == startOrientation;
    }

    /**
     * This method returns true if the unsuccessful criterion has been satisfied
     *
     * @return
     */
    protected boolean isUnSuccessfulStopCriterion() {
        return contour.size() == map.area();
    }

    /**
     * Scans until it finds the first point
     *
     * @param bounds
     * @return
     */
    protected Point scan(Rect bounds) {
        for (int x = bounds.left; x < bounds.right; x++) {
            for (int y = bounds.top; y < bounds.bottom; y++) {
                if (map.isBlack(x, y)) {
                    startOrientation = orientation;
                    return new Point(x, y);
                }
                orientation = Orientation.South;
            }
            orientation = Orientation.East;
        }
        return null;
    }

    /**
     * Moves perceptron foward
     */
    protected void moveFoward() {
        switch (orientation) {
            case East:
                if (currPoint.x < map.getWidth() - 1)
                    currPoint.x += 1;
                break;
            case North:
                if (currPoint.y > 0)
                    currPoint.y -= 1;
                break;
            case West:
                if (currPoint.x > 0)
                    currPoint.x -= 1;
                break;
            case South:
                if (currPoint.y < map.getHeight() - 1)
                    currPoint.y += 1;
                break;
        }
    }

    /**
     * Moves perceptron back and inverse direction
     */
    protected void moveBack() {

        switch (orientation) {
            case East:
                if (currPoint.x > 0)
                    currPoint.x -= 1;
                orientation = Orientation.West;
                break;
            case North:
                if (currPoint.y < map.getHeight() - 1)
                    currPoint.y += 1;
                orientation = Orientation.South;
                break;
            case West:
                if (currPoint.x < map.getWidth() - 1)
                    currPoint.x += 1;
                orientation = Orientation.East;
                break;
            case South:
                if (currPoint.y > 0)
                    currPoint.y -= 1;
                orientation = Orientation.North;
                break;
        }
    }

    /**
     * Turns perceptron right and assign orientation
     */
    protected void turnRight() {
        switch (orientation) {
            case East:
                if (currPoint.y < map.getHeight() - 1)
                    currPoint.y += 1;
                orientation = Orientation.South;
                break;
            case North:
                if (currPoint.x < map.getWidth() - 1)
                    currPoint.x += 1;
                orientation = Orientation.East;
                break;
            case West:
                if (currPoint.y > 0)
                    currPoint.y -= 1;
                orientation = Orientation.North;
                break;
            case South:
                if (currPoint.x > 0)
                    currPoint.x -= 1;
                orientation = Orientation.West;
                break;
        }
    }

    /**
     * Turns perceptron left and assign orientation
     */
    protected void turnLeft() {
        switch (orientation) {
            case East:
                if (currPoint.y > 0)
                    currPoint.y -= 1;
                orientation = Orientation.North;
                break;
            case North:
                if (currPoint.x > 0)
                    currPoint.x -= 1;
                orientation = Orientation.West;
                break;
            case West:
                if (currPoint.y < map.getHeight() - 1)
                    currPoint.y += 1;
                orientation = Orientation.South;
                break;
            case South:
                if (currPoint.x < map.getWidth() - 1)
                    currPoint.x += 1;
                orientation = Orientation.East;
                break;
        }
    }

    /**
     * Moves perceptron clockwise until it finds a black pixel
     */
    protected void moveClockwise() {

        int pixel = map.getPixel(currPoint.x, currPoint.y);

        if (map.isBlack(pixel))
            return;
        turnRight();

        pixel = map.getPixel(currPoint.x, currPoint.y);
        if (map.isBlack(pixel))
            return;
        turnRight();

        pixel = map.getPixel(currPoint.x, currPoint.y);
        if (map.isBlack(pixel))
            return;
        moveFoward();

        pixel = map.getPixel(currPoint.x, currPoint.y);
        if (map.isBlack(pixel))
            return;
        turnRight();

        pixel = map.getPixel(currPoint.x, currPoint.y);
        if (map.isBlack(pixel))
            return;
        moveFoward();

        pixel = map.getPixel(currPoint.x, currPoint.y);
        if (map.isBlack(pixel))
            return;
        turnRight();

        pixel = map.getPixel(currPoint.x, currPoint.y);
        if (map.isBlack(pixel))
            return;
        moveFoward();

        pixel = map.getPixel(currPoint.x, currPoint.y);
        if (map.isBlack(pixel))
            return;
        turnRight();

        pixel = map.getPixel(currPoint.x, currPoint.y);
        if (map.isBlack(pixel))
            return;
        turnRight();

    }

    /**
     * Move perceptron counter clockwise until it finds a black pixel
     */
    protected void moveCounterclockwise() {

        int pixel = map.getPixel(currPoint.x, currPoint.y);

        if (map.isBlack(pixel))
            return;
        turnLeft();

        pixel = map.getPixel(currPoint.x, currPoint.y);
        if (map.isBlack(pixel))
            return;
        turnLeft();

        pixel = map.getPixel(currPoint.x, currPoint.y);
        if (map.isBlack(pixel))
            return;
        moveFoward();

        pixel = map.getPixel(currPoint.x, currPoint.y);
        if (map.isBlack(pixel))
            return;
        turnLeft();

        pixel = map.getPixel(currPoint.x, currPoint.y);
        if (map.isBlack(pixel))
            return;
        moveFoward();

        pixel = map.getPixel(currPoint.x, currPoint.y);
        if (map.isBlack(pixel))
            return;
        turnLeft();

        pixel = map.getPixel(currPoint.x, currPoint.y);
        if (map.isBlack(pixel))
            return;
        moveFoward();

        pixel = map.getPixel(currPoint.x, currPoint.y);
        if (map.isBlack(pixel))
            return;
        turnLeft();

        pixel = map.getPixel(currPoint.x, currPoint.y);
        if (map.isBlack(pixel))
            return;
        turnLeft();

    }

    /**
     * Cleans left overs pixels on the countour path
     *
     * @param contour
     */
    protected void cleanExploredPath(Contour contour) {
        List<Point> points = contour.getContourPoints();
        for (Point p : points)
            markNeighbors(p, 1);
    }

    /**
     * Mark neighbors with color
     *
     * @param p
     * @param offSet
     */
    protected void markNeighbors(Point p, int offSet) {

        // Edges
        markPoint(p.x, p.y + offSet, exploredColor);
        markPoint(p.x, p.y - offSet, exploredColor);
        markPoint(p.x + offSet, p.y, exploredColor);
        markPoint(p.x - offSet, p.y, exploredColor);

        // Vertices
        markPoint(p.x + offSet, p.y + offSet, exploredColor);
        markPoint(p.x + offSet, p.y - offSet, exploredColor);
        markPoint(p.x - offSet, p.y + offSet, exploredColor);
        markPoint(p.x - offSet, p.y - offSet, exploredColor);

    }

    /**
     * Mark neighbors with value
     *
     * @param x
     * @param y
     */
    protected void markPoint(int x, int y, int color) {
        if (exploredMap.isBlack(x, y))
            exploredMap.setPixel(x, y, color);
    }

    public Contour getContour() {
        return contour;
    }

    public EdgeMap getExploredMap() {
        return exploredMap;
    }

    public void setExploredMap(EdgeMap exploredMap) {
        this.exploredMap = exploredMap;
    }

    public int getExploredColor() {
        return exploredColor;
    }

    public void setExploredColor(int exploredColor) {
        this.exploredColor = exploredColor;
    }

    public static enum TracingResult {
        NotAbleToFindStartPoint,
        NotAbleToContour,
        Successful
    }

    public static enum Orientation {
        East,
        North,
        West,
        South
    }

}
