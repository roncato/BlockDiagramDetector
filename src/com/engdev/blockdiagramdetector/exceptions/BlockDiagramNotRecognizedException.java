/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.exceptions;

/**
 * Implements a block diagram not recognized exception
 *
 * @author Lucas Batista
 */
public class BlockDiagramNotRecognizedException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public BlockDiagramNotRecognizedException(String msg) {
        super(msg);
    }

}
