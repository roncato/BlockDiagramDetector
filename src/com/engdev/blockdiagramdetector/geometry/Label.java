/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.geometry;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a string of character objects
 *
 * @author Engineer
 */
public final class Label extends GeometricObject {
    protected List<Character> characters = null;

    public Label() {
        characters = new ArrayList<Character>();
    }

    @Override
    public Point centroid() {
        // TODO Auto-generated method stub
        return null;
    }

}
