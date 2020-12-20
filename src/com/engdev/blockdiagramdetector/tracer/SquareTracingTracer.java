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
import com.engdev.blockdiagramdetector.geometry.Contour;
import com.engdev.blockdiagramdetector.geometry.Contour.MarshDirection;
import com.engdev.blockdiagramdetector.geometry.Point;
import com.engdev.blockdiagramdetector.imageprocessing.EdgeMap;
import com.engdev.blockdiagramdetector.imageprocessing.ImageUtility;

/**
 * Implements a contour perceptron for Square Tracing algorithm.
 * This perceptron is able to trace 4-connected patterns
 *
 * @author Lucas Batista
 */
public class SquareTracingTracer extends ContourTracer {

    private Point fromPoint = null;

    public SquareTracingTracer(EdgeMap map, Rect bounds) {
        this.map = map;
        this.bounds = bounds;
        exploredColor = ImageUtility.randomColor();
    }

    public SquareTracingTracer(EdgeMap map) {
        this(map, new Rect(0, 0, map.getWidth() - 1, map.getHeight() - 1));
    }

    @Override
    public TracingResult trace() {

        // If explored map is null create one from map
        if (exploredMap == null)
            exploredMap = map.clone();

        // Finds start point
        startPoint = scan(bounds);

        if (startPoint == null)
            return TracingResult.NotAbleToFindStartPoint;

        // Creates contour
        contour = new Contour(startPoint, MarshDirection.CounterClockwise);

        // Assigns current Point
        currPoint = new Point(startPoint.x, startPoint.y);

        // Adds to Contour
        contour.addChainCode(Contour.getChainCode(currPoint, currPoint));

        // Moves right
        turnRight();

        // Starts the contour
        if (!contour())
            return TracingResult.NotAbleToContour;

        // Sets explored pheromone
        exploredMap.setPixel(currPoint.x, currPoint.y, exploredColor);

        // Clean Neighbor pixels in the contour
        cleanExploredPath(contour);

        // Change Contour code for initial point
        byte code = Contour.getChainCode(fromPoint, startPoint);
        contour.getCodes().set(0, code);

        return TracingResult.Successful;

    }

    @Override
    protected boolean contour() {

        int pixel = map.getPixel(currPoint.x, currPoint.y);
        fromPoint = currPoint;

        while (true) {
            if (map.isBlack(pixel)) {

                // Sets explored pheromone
                exploredMap.setPixel(currPoint.x, currPoint.y, exploredColor);

                // Adds to Contour
                contour.addChainCode(Contour.getChainCode(fromPoint, currPoint));

                // Saves position
                fromPoint = new Point(currPoint.x, currPoint.y);

                // turn perceptron
                turnRight();

            } else
                turnLeft();

            // Assigns next pixels
            pixel = map.getPixel(currPoint.x, currPoint.y);

            // Successful Stop Criterion
            if (isSuccessfulStopCriterion())
                return true;

            // Unsuccessful Stop Criterion
            if (isUnSuccessfulStopCriterion())
                return false;

        }

    }


}
