/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.database;

import java.util.Map;

/**
 * Data structure for statistics collection
 *
 * @author Lucas Batista
 */
public class ObjectStatistics {
    public String id = null;
    public String text = null;
    public Map<String, Statistics> randomVars = null;

}
