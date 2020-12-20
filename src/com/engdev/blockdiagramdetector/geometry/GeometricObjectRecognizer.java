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
import com.engdev.blockdiagramdetector.database.Database;
import com.engdev.blockdiagramdetector.database.ObjectStatistics;
import com.engdev.blockdiagramdetector.database.Statistics;
import com.engdev.blockdiagramdetector.geometry.Arrow.PointerDirection;
import com.engdev.blockdiagramdetector.geometry.GeometricObject.GeometricObjectType;
import com.engdev.blockdiagramdetector.math.MathUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Geometric Object recognizer. Abstract Factory class that recognizes and builds geometric objects from their regions.
 *
 * @author Engineer
 */
public abstract class GeometricObjectRecognizer {

    private final static int NOISE_AREA_THRESHOLD = 10;
    private final static int NOISE_PERIMETER_THRESHOLD = 5;

    public static GeometricObject createObject(Region region, GeometricObjectType objectType) {
        GeometricObject object = null;
        Rect bounds = region.getContour().getBoundingBox();
        Point centroid = region.centroid();
        Point startPoint = null;
        Point endPoint = null;
        switch (objectType) {
            case Rectangle:
                object = new Rectangle(bounds);
                break;
            case Ellipse:
                object = new Ellipse(bounds);
                break;
            case Bar:
                object = new Bar(bounds);
                break;
            case LetterS:
                object = new Character(bounds, "s", centroid);
                break;
            case Number0:
                object = new Character(bounds, "0", centroid);
                break;
            case Number1:
                object = new Character(bounds, "1", centroid);
                break;
            case Number2:
                object = new Character(bounds, "2", centroid);
                break;
            case Number3:
                object = new Character(bounds, "3", centroid);
                break;
            case Number4:
                object = new Character(bounds, "4", centroid);
                break;
            case Number5:
                object = new Character(bounds, "5", centroid);
                break;
            case Number6:
                object = new Character(bounds, "6", centroid);
                break;
            case Number7:
                object = new Character(bounds, "7", centroid);
                break;
            case Number8:
                object = new Character(bounds, "8", centroid);
                break;
            case Number9:
                object = new Character(bounds, "9", centroid);
                break;
            case SummationOperator:
                object = new Character(bounds, "+", centroid);
                break;
            case SubtractionOperator:
                object = new Character(bounds, "-", centroid);
                break;
            case EastArrow:
                startPoint = new Point(bounds.left, bounds.top + bounds.height() / 2);
                endPoint = new Point(bounds.right, bounds.top + bounds.height() / 2);
                object = new Arrow(PointerDirection.East, startPoint, endPoint);
                break;
            case NorthEast:
                startPoint = new Point(bounds.left, bounds.bottom);
                endPoint = new Point(bounds.right, bounds.top);
                object = new Arrow(PointerDirection.East, startPoint, endPoint);
                break;
            case NorthArrow:
                startPoint = new Point(bounds.left + bounds.width() / 2, bounds.bottom);
                endPoint = new Point(bounds.left + bounds.width() / 2, bounds.top);
                object = new Arrow(PointerDirection.North, startPoint, endPoint);
                break;
            case NorthWest:
                startPoint = new Point(bounds.right, bounds.bottom);
                endPoint = new Point(bounds.left, bounds.top);
                object = new Arrow(PointerDirection.East, startPoint, endPoint);
                break;
            case WestArrow:
                startPoint = new Point(bounds.right, bounds.top + bounds.height() / 2);
                endPoint = new Point(bounds.left, bounds.top + bounds.height() / 2);
                object = new Arrow(PointerDirection.West, startPoint, endPoint);
                break;
            case SouthWest:
                startPoint = new Point(bounds.right, bounds.top);
                endPoint = new Point(bounds.left, bounds.bottom);
                object = new Arrow(PointerDirection.East, startPoint, endPoint);
                break;
            case SouthArrow:
                startPoint = new Point(bounds.left + bounds.width() / 2, bounds.top);
                endPoint = new Point(bounds.left + bounds.width() / 2, bounds.bottom);
                object = new Arrow(PointerDirection.South, startPoint, endPoint);
                break;
            case SouthEast:
                startPoint = new Point(bounds.left, bounds.top);
                endPoint = new Point(bounds.right, bounds.bottom);
                object = new Arrow(PointerDirection.East, startPoint, endPoint);
                break;
            default:
        }
        return object;
    }

    /**
     * Regions classifier.
     *
     * @param region
     * @return
     */
    public static GeometricObject recognize(Region region) {
        GeometricObject object = null;

        if ((object = recognizeEllipse(region)) != null)
            return object;
        else if ((object = recognizeRectangle(region)) != null)
            return object;
        else if ((object = recognizeBar(region)) != null)
            return object;
        else if ((object = recognizeCharacter(region)) != null)
            return object;

        return object;
    }

    /**
     * Recognize rectangles.
     *
     * @param region
     * @return
     */
    public static Rectangle recognizeRectangle(Region region) {
        Rectangle rect = null;

        // Gets points and rotated them to be in level
        Set<Point> points = region.getPoints();
        points = MathUtility.rotate(points, -region.orientation());

        // Gets bounding box for rotated points
        Rect bounds = region.getContour().getBoundingBox();

        // Compactness
        float compactness = region.compactness();

        // Gets phi1 from hu vector;
        double[] hu = region.huMoments();

        // Gets circularity
        Map<String, Statistics> randomVars = Database.getObjectStatistics("rectangle").randomVars;

        // Threshold
        float threshold = Database.getStatisticsThreshold();

        // Determines whether parameters satisfy the acceptance interval for the joint pdf
        float z = (compactness - randomVars.get("compactness").mean) / (randomVars.get("compactness").stdev);
        boolean sentence = z >= -threshold && z <= threshold;
        z = (float) ((hu[0] - randomVars.get("phi1").mean) / randomVars.get("phi1").stdev);
        sentence = z >= -threshold && z <= threshold;


        if (sentence)
            rect = new Rectangle(bounds);

        return rect;
    }

    /**
     * Recognize rectangles.
     *
     * @param region
     * @return
     */
    public static Bar recognizeBar(Region region) {
        Bar bar = null;

        // Gets points and rotated them to be in level
        double[] hu = region.huMoments();

        // Compactness
        float compactness = region.compactness();

        // Gets circularity
        Map<String, Statistics> randomVars = Database.getObjectStatistics("bar").randomVars;

        // Threshold
        float threshold = Database.getStatisticsThreshold();

        // Determines whether parameters satisfy the acceptance interval for the joint pdf
        float z = (compactness - randomVars.get("compactness").mean) / (randomVars.get("compactness").stdev);
        boolean sentence = z >= -threshold && z <= threshold;
        z = (float) ((hu[0] - randomVars.get("phi1").mean) / randomVars.get("phi1").stdev);
        sentence = z >= -threshold && z <= threshold;

        if (sentence)
            bar = new Bar(region.getContour().getBoundingBox());

        return bar;
    }

    /**
     * Recognize ellipses.
     *
     * @param region
     * @return
     */
    public static Ellipse recognizeEllipse(Region region) {
        Ellipse ellipse = null;

        // Gets circularity
        float circularity = region.circularity();

        // Gets random vars
        Map<String, Statistics> randomVars = Database.getObjectStatistics("ellipse").randomVars;

        // Threshold
        float threshold = Database.getStatisticsThreshold();

        // Determines whether parameters satisfy the acceptance interval, meaning accptance of null hypothesis
        float z = (circularity - randomVars.get("circularity").mean) / (randomVars.get("circularity").stdev);
        boolean sentence = z >= -threshold && z <= threshold;

        if (sentence)
            ellipse = new Ellipse(new Rect(region.getContour().getBoundingBox()));

        return ellipse;
    }

    /**
     * Recognize characters by hypothesis test
     *
     * @param region
     * @return
     */
    public static Character recognizeCharacter(Region region) {

        Character character = null;

        // Gets statiscs data
        List<ObjectStatistics> charsStats = Database.getCharactersStatistics();

        // Gets points and rotated them to be in level
        Set<Point> points = region.getPoints();
        points = MathUtility.rotate(points, -region.orientation());

        // Gets bounding box
        Rect bounds = region.getContour().getBoundingBox();

        points = MathUtility.rotate(points, -region.orientation());
        Rect rotBounds = MathUtility.boundingBox(points);
        double[] hu = region.huMoments();
        Point centroid = region.centroid();
        float compactness = region.compactness();
        float circularity = region.circularity();
        float perimeter = region.perimeter();
        float radius = region.getMaxRadius() / perimeter;
        float filling = region.area() / (rotBounds.width() * rotBounds.height());

        List<ObjectStatistics> recognitions = new ArrayList<ObjectStatistics>();
        boolean sentence = false;

        for (ObjectStatistics charsStat : charsStats) {
            Map<String, Statistics> randomVars = charsStat.randomVars;
            sentence = testHypothesis(points, rotBounds, bounds, hu, centroid,
                    compactness, circularity, perimeter, radius, filling, randomVars);

            if (sentence)
                recognitions.add(charsStat);

        }

        if (recognitions.size() == 1)
            character = new Character(bounds, recognitions.get(0).text, centroid);
        else if (recognitions.size() > 1)
            character = recognizeCharacterByEculidian(region, recognitions);

        return character;
    }

    /**
     * Tests hypothesis for given test random variables in comparison to trained random variables
     *
     * @param points
     * @param rotBounds
     * @param bounds
     * @param hu
     * @param centroid
     * @param compactness
     * @param circularity
     * @param perimeter
     * @param radius
     * @param filling
     * @param randomVars
     * @return
     */
    private static boolean testHypothesis(Set<Point> points,
                                          Rect rotBounds,
                                          Rect bounds,
                                          double[] hu,
                                          Point centroid,
                                          float compactness,
                                          float circularity,
                                          float perimeter,
                                          float radius,
                                          float filling,
                                          Map<String, Statistics> randomVars) {

        float z = 0;
        boolean sentence = false;
        float cxLocation = (bounds.right - centroid.x) / (float) bounds.width();
        float cyLocation = (bounds.bottom - centroid.y) / (float) bounds.height();

        // Threshold
        float threshold = Database.getStatisticsThreshold();

        if (randomVars.containsKey("phi1")) {
            z = (float) ((hu[0] - randomVars.get("phi1").mean) / randomVars.get("phi1").stdev);
            sentence = z >= -threshold && z <= threshold;
        }

        if (randomVars.containsKey("phi2")) {
            z = (float) ((hu[1] - randomVars.get("phi2").mean) / randomVars.get("phi2").stdev);
            sentence &= z >= -threshold && z <= threshold;
        }

        if (randomVars.containsKey("phi3")) {
            z = (float) ((hu[2] - randomVars.get("phi3").mean) / randomVars.get("phi3").stdev);
            sentence &= z >= -threshold && z <= threshold;
        }

        if (randomVars.containsKey("centroid_x")) {
            z = (float) ((cxLocation - randomVars.get("centroid_x").mean) / randomVars.get("centroid_x").stdev);
            sentence &= z >= -threshold && z <= threshold;
        }

        if (randomVars.containsKey("centroid_y")) {
            z = (float) ((cyLocation - randomVars.get("centroid_y").mean) / randomVars.get("centroid_y").stdev);
            sentence &= z >= -threshold && z <= threshold;
        }

        if (randomVars.containsKey("radius")) {
            z = (float) ((radius - randomVars.get("radius").mean) / randomVars.get("radius").stdev);
            sentence &= z >= -threshold && z <= threshold;
        }

        if (randomVars.containsKey("circularity")) {
            z = (circularity - randomVars.get("circularity").mean) / (randomVars.get("circularity").stdev);
            sentence = z >= -threshold && z <= threshold;
        }

        if (randomVars.containsKey("filling")) {
            z = (float) ((filling - randomVars.get("filling").mean) / randomVars.get("filling").stdev);
            sentence &= z >= -threshold && z <= threshold;
        }

        if (randomVars.containsKey("compactness")) {
            z = (float) ((compactness - randomVars.get("compactness").mean) / randomVars.get("compactness").mean);
            sentence &= z >= -threshold && z <= threshold;
        }

        return sentence;
    }

    /**
     * Recognize characters by Euculidian distance
     *
     * @param region
     * @return
     */
    private static Character recognizeCharacterByEculidian(Region region, List<ObjectStatistics> charsStats) {

        // Map of distances
        float minDistance = 1E9F;

        // Char Text
        String charText = null;

        //Get bounds
        Rect bounds = region.getContour().getBoundingBox();

        // Center of mass
        Point centroid = region.centroid();

        for (ObjectStatistics charsStat : charsStats) {
            Map<String, Statistics> randomVars = charsStat.randomVars;
            float distance = getEculidianDistance(region, randomVars);
            if (distance < minDistance) {
                minDistance = distance;
                charText = charsStat.text;
            }
        }

        return new Character(bounds, charText, centroid);

    }

    /**
     * Get Eculidian distance between a region and a set of random vars.
     *
     * @param region
     * @param randomVars
     * @return
     */
    private static float getEculidianDistance(Region region, Map<String, Statistics> randomVars) {

        Set<Point> points = region.getPoints();
        points = MathUtility.rotate(points, -region.orientation());
        Rect rotBounds = MathUtility.boundingBox(points);
        Rect bounds = region.getContour().getBoundingBox();
        double[] hu = region.huMoments();
        Point centroid = region.centroid();
        float compactness = region.compactness();
        float circularity = region.circularity();
        float diff = 0;
        float perimeter = region.perimeter();
        float cxLocation = (bounds.right - centroid.x) / (float) bounds.width();
        float cyLocation = (bounds.bottom - centroid.y) / (float) bounds.height();
        float radius = region.getMaxRadius() / perimeter;
        float distance = 0;
        float filling = region.area() / (rotBounds.width() * rotBounds.height());

        if (randomVars.containsKey("phi1")) {
            diff = (float) hu[0] - randomVars.get("phi1").mean;
            distance += Math.pow(diff, 2);
        }

        if (randomVars.containsKey("phi2")) {
            diff = (float) hu[1] - randomVars.get("phi2").mean;
            distance += Math.pow(diff, 2);
        }

        if (randomVars.containsKey("phi3")) {
            diff = (float) hu[2] - randomVars.get("phi3").mean;
            distance += Math.pow(diff, 2);
        }

        if (randomVars.containsKey("centroid_x")) {
            diff = (float) cxLocation - randomVars.get("centroid_x").mean;
            distance += Math.pow(diff, 2);
        }

        if (randomVars.containsKey("centroid_y")) {
            diff = (float) cyLocation - randomVars.get("centroid_y").mean;
            distance += Math.pow(diff, 2);
        }

        if (randomVars.containsKey("radius")) {
            diff = (float) radius - randomVars.get("radius").mean;
            distance += Math.pow(diff, 2);
        }

        if (randomVars.containsKey("circularity")) {
            diff = circularity - randomVars.get("circularity").mean;
            distance += Math.pow(diff, 2);
        }

        if (randomVars.containsKey("filling")) {
            diff = filling - randomVars.get("filling").mean;
            distance += Math.pow(diff, 2);
        }

        if (randomVars.containsKey("compactness")) {
            diff = compactness - randomVars.get("compactness").mean;
            distance += Math.pow(diff, 2);
        }

        return distance;
    }

    /**
     * Recognizes noise
     *
     * @param region
     * @return
     */
    public static boolean isNoize(Region region) {
        float area = region.area();
        return area <= NOISE_AREA_THRESHOLD && NOISE_PERIMETER_THRESHOLD <= 5;
    }

    /**
     * Whether it is a inner contour region
     *
     * @param outerRegion
     * @param innerRegion
     * @return
     */
    public static boolean isInnerContourRegion(Region outerRegion, Region innerRegion) {

        float outerArea = outerRegion.area();
        float innerArea = innerRegion.area();
        float outerPerimeter = outerRegion.perimeter();
        float innerPerimeter = innerRegion.perimeter();
        float areaDiff = Math.abs((outerArea - innerArea) / outerArea);
        float perimeterDiff = Math.abs((outerPerimeter - innerPerimeter) / outerPerimeter);

        // Gets random vars
        Map<String, Statistics> randomVars = Database.getObjectStatistics("inner_contour_region").randomVars;

        // Threshold
        float threshold = Database.getStatisticsThreshold();

        float z = (areaDiff - randomVars.get("filling_area").mean) / (randomVars.get("filling_area").stdev);
        boolean sentence = z >= -threshold && z <= threshold;
        z = (float) ((perimeterDiff - randomVars.get("filling_perimeter").mean) / randomVars.get("filling_perimeter").stdev);
        sentence = z >= -threshold && z <= threshold;

        return sentence;
    }

    /**
     * Whether it is a a division symbol inside the outer region
     *
     * @param outerRegion
     * @param innerRegion
     * @return
     */
    public static boolean isDivisionSymbol(Region outerRegion, Region innerRegion) {

        Rect bounds = outerRegion.getContour().getBoundingBox();
        Point outerCentroid = outerRegion.centroid();
        Point innerCentroid = innerRegion.centroid();
        float centroidYDiff = Math.abs(outerCentroid.y - innerCentroid.y) / (bounds.height() * 1.0F);
        float centroidXDiff = Math.abs(outerCentroid.x - innerCentroid.x) / (bounds.width() * 1.0F);

        // Gets random vars
        Map<String, Statistics> randomVars = Database.getObjectStatistics("division_symbol").randomVars;

        // Threshold
        float threshold = Database.getStatisticsThreshold();

        float z = (centroidYDiff - randomVars.get("centroid_y").mean) / (randomVars.get("centroid_y").stdev);
        boolean sentence = z >= -threshold && z <= threshold;
        z = (float) ((centroidXDiff - randomVars.get("centroid_x").mean) / randomVars.get("centroid_x").stdev);
        sentence = z >= -threshold && z <= threshold;

        return sentence;

    }
}
