/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.io;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import com.engdev.blockdiagramdetector.blockdiagram.BlockDiagramAST;
import com.engdev.blockdiagramdetector.geometry.Contour;
import com.engdev.blockdiagramdetector.geometry.FeatureVector;
import com.engdev.blockdiagramdetector.geometry.Point;
import com.engdev.blockdiagramdetector.geometry.Region;
import com.engdev.blockdiagramdetector.math.MathUtility;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Image utility class.
 *
 * @author Lucas Batista
 */
public class IOUtility {

    public static Bitmap getImage(Uri uri, ContentResolver content) throws IOException {
        InputStream fIn = content.openInputStream(uri);
        Bitmap image = BitmapFactory.decodeStream(fIn);
        fIn.close();
        return image;
    }

    public static Bitmap getImage(File file) throws IOException {
        FileInputStream fIn = new FileInputStream(file);
        Bitmap image = BitmapFactory.decodeStream(fIn);
        fIn.close();
        return image;
    }

    public static void writeFile(File file, byte[] buffer) throws IOException {
        FileOutputStream fOut = new FileOutputStream(file);
        fOut.write(buffer);
        fOut.close();
    }

    public static Bitmap getImage(String fileFullName) throws IOException {
        return getImage(new File(fileFullName));
    }

    public static void writeImageFile(File file, Bitmap bm, Bitmap.CompressFormat cf, int quality) throws IOException {
        FileOutputStream fOut = new FileOutputStream(file);
        bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        fOut.close();
    }

    public static void bmpToCSV(File file, Bitmap bm) throws IOException {
        FileWriter fWriter = new FileWriter(file);
        BufferedWriter fOut = new BufferedWriter(fWriter);
        String line = null;
        for (int y = 0; y < bm.getHeight(); y++) {
            line = "";
            for (int x = 0; x < bm.getWidth(); x++)
                line += bm.getPixel(x, y) + ",";
            fOut.write(line + "\n");
        }
        fOut.close();
    }

    public static void regionsToCSV(File file, Collection<Region> regions) throws IOException {
        String line = "CentroidX,CentroidY,Area,Perimeter,Normal MomentX,Normal MomentY,Orientation," +
                "Number Of Vertices,Rot Bounds Width,Rot Bounds Height,Euler Number," +
                "Eccentricity,Inner Regions,phi1,phi2,phi3,phi4,phi5,phi6,phi7,CxLocation,CyLocation," +
                "Longest Radius proportion,Left,Right,Top,Bottom";
        Point centroid = new Point();
        float area = 0;
        float perimeter = 0;
        double momentX = 0;
        double momentY = 0;
        float orientation = 0;
        float vertices = 0;
        Rect bounds = null;
        int eulerNumber = 0;
        float ecc = 0;
        int innerRegions = 0;
        double[] hu = null;
        float cxLocation = 0;
        float cyLocation = 0;
        float longestRadius = 0;
        Rect rotbounds = null;

        FileWriter fWriter;
        fWriter = new FileWriter(file);
        BufferedWriter fOut = new BufferedWriter(fWriter);

        fOut.write(line + "\n");
        for (Region region : regions) {
            centroid = region.centroid();
            area = region.area();
            perimeter = region.perimeter();
            momentX = region.normalCentralMoment(1, 0);
            momentY = region.normalCentralMoment(0, 1);
            orientation = MathUtility.toDegrees(region.orientation());
            vertices = region.getContour().size();
            bounds = region.getContour().getBoundingBox();
            cxLocation = (bounds.right - centroid.x) / (float) bounds.width();
            cyLocation = (bounds.bottom - centroid.y) / (float) bounds.height();
            Set<Point> points = region.getPoints();
            Collection<Point> rotation = MathUtility.rotate(points, area);
            rotbounds = MathUtility.boundingBox(rotation);
            eulerNumber = region.eulerNumber();
            ecc = region.eccentricity();
            innerRegions = region.getInnerRegions().size();
            hu = region.huMoments();
            longestRadius = region.getMaxRadius() / perimeter;

            line = centroid.x + "," + centroid.y + "," + area + "," + perimeter + ","
                    + momentX + "," + momentY + "," + orientation + "," + vertices + ","
                    + rotbounds.width() + "," + rotbounds.height() + "," + eulerNumber + ","
                    + ecc + "," + innerRegions + "," + hu[0] + "," + hu[1] + "," + hu[2] + "," + hu[3]
                    + "," + hu[4] + "," + hu[5] + "," + hu[6] + "," + cxLocation + "," + cyLocation +
                    ", " + longestRadius + "," + bounds.left + "," + bounds.right + "," + bounds.top + "," + bounds.bottom;

            fOut.write(line + "\n");
        }
        fOut.close();
    }

    public static void contoursToCSV(File file, Collection<Contour> contours) throws IOException {
        String line = "CoordX,CoordY,ChainCode";

        FileWriter fWriter;
        fWriter = new FileWriter(file);
        BufferedWriter fOut = new BufferedWriter(fWriter);
        fOut.write(line + "\n");
        for (Contour c : contours) {
            List<Point> points = c.getContourPoints();
            List<Byte> codes = c.getCodes();
            for (int i = 0; i < codes.size(); i++) {
                line = points.get(i).x + "," + points.get(i).y + "," + codes.get(i) + "\n";
                fOut.write(line);
            }
            fOut.write("\n");
        }
        fOut.close();
    }

    public static void astToHtml(File file, BlockDiagramAST root) throws IOException {
        FileWriter fWriter;
        fWriter = new FileWriter(file);
        BufferedWriter fOut = new BufferedWriter(fWriter);
        fOut.write(root.toHTML());
        fOut.close();
    }

    public static void featureVectorToCSV(File file, FeatureVector vector) throws IOException {
        FileWriter fWriter;
        fWriter = new FileWriter(file);
        BufferedWriter fOut = new BufferedWriter(fWriter);
        String line = "Track,Sector,Relation,Count";
        fOut.write(line + "\n");
        for (int i = 0; i < vector.getNumberOfTracks(); i++) {
            for (int j = 0; j < vector.getNumberOfSectors(); j++) {
                for (byte k = 0; k < 8; k++) {
                    line = i + "," + j + "," + k + "," + vector.getTracks()[i].getSectors()[j].getRelations()[k];
                    fOut.write(line + "\n");
                }
            }
        }
        fOut.close();
    }

    public static void featureVectorsToCSV(File file, Collection<FeatureVector> vectors) throws IOException {
        FileWriter fWriter;
        fWriter = new FileWriter(file);
        BufferedWriter fOut = new BufferedWriter(fWriter);
        String line = "Track,Sector,Relation,Count";
        fOut.write(line + "\n");
        for (FeatureVector vector : vectors) {
            for (int i = 0; i < vector.getNumberOfTracks(); i++) {
                for (int j = 0; j < vector.getNumberOfSectors(); j++) {
                    for (byte k = 0; k < 8; k++) {
                        line = i + "," + j + "," + k + "," + vector.getTracks()[i].getSectors()[j].getRelations()[k];
                        fOut.write(line + "\n");
                    }
                }
            }
        }
        fOut.close();
    }

    public static Document getXmlDOM(Context context, String fileName) throws ParserConfigurationException, SAXException, IOException {
        InputStream is = context.getAssets().open(fileName);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(is);
    }
}
