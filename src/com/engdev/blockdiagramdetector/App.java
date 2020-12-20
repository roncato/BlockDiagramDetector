/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector;

import android.content.Intent;
import android.view.MotionEvent;
import com.engdev.blockdiagramdetector.state.AppState;

/**
 * App Interface
 *
 * @author Lucas Batista
 */
public interface App extends Runnable {

    /**
     * Handles activity Motion Event
     *
     * @param event Motion event
     * @param evt   Event Type
     */
    void handleActivityMotionEvent(MotionEvent event, EventType evt);

    ;

    /**
     * Handles activity Result
     *
     * @param event Motion event
     * @param evt   Event Type
     */
    void handleActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * Resumes state
     *
     * @param state
     */
    void setState(AppState state);

    // Enumerators
    public enum EventType {SingleTap, DoubleTap}

}
