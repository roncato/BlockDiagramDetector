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

/**
 * Represents a general geometric object
 *
 * @author Lucas Batista
 */
public abstract class GeometricObject {

    protected Rect bounds = null;

    public static GeometricObjectType getObjectType(GeometricObject object) {
        GeometricObjectType objectType = null;
        if (object instanceof Rectangle)
            objectType = GeometricObjectType.Rectangle;
        else if (object instanceof Ellipse)
            objectType = GeometricObjectType.Ellipse;
        else if (object instanceof Bar)
            objectType = GeometricObjectType.Bar;
        else if (object instanceof Character) {
            Character character = (Character) object;
            if (character.toString().equals("s"))
                objectType = GeometricObjectType.LetterS;
            else if (character.toString().equals("0"))
                objectType = GeometricObjectType.Number0;
            else if (character.toString().equals("1"))
                objectType = GeometricObjectType.Number1;
            else if (character.toString().equals("2"))
                objectType = GeometricObjectType.Number2;
            else if (character.toString().equals("3"))
                objectType = GeometricObjectType.Number3;
            else if (character.toString().equals("4"))
                objectType = GeometricObjectType.Number4;
            else if (character.toString().equals("5"))
                objectType = GeometricObjectType.Number5;
            else if (character.toString().equals("6"))
                objectType = GeometricObjectType.Number6;
            else if (character.toString().equals("7"))
                objectType = GeometricObjectType.Number7;
            else if (character.toString().equals("8"))
                objectType = GeometricObjectType.Number8;
            else if (character.toString().equals("9"))
                objectType = GeometricObjectType.Number9;
            else if (character.toString().equals("+"))
                objectType = GeometricObjectType.SummationOperator;
            else if (character.toString().equals("-"))
                objectType = GeometricObjectType.SubtractionOperator;
        }

        return objectType;
    }

    public abstract Point centroid();

    public Rect getBoundingBox() {
        return new Rect(bounds);
    }

    public GeometricObjectType getObjectType() {
        return getObjectType(this);
    }

    @Override
    public String toString() {
        GeometricObjectType type = getObjectType(this);
        if (type != null)
            return type.toString();
        else
            return super.toString();
    }

    public static enum GeometricObjectType {
        Rectangle,
        Ellipse,
        Bar,
        EastArrow,
        NorthEast,
        NorthArrow,
        NorthWest,
        WestArrow,
        SouthWest,
        SouthArrow,
        SouthEast,
        LetterS,
        Number0,
        Number1,
        Number2,
        Number3,
        Number4,
        Number5,
        Number6,
        Number7,
        Number8,
        Number9,
        SummationOperator,
        SubtractionOperator,
        Noise
    }

}
