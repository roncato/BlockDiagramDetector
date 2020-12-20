/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.state;

/**
 * Implements an activity state object class
 *
 * @author Lucas Batista
 */
public class ActivityState implements State {

    private StateType stateType;

    ;

    public ActivityState(StateType stateType) {
        this.stateType = stateType;
    }

    /**
     * Gets state type the state is current in.
     *
     * @return
     */
    public StateType getStateType() {
        return stateType;
    }

    /**
     * Sets state type
     *
     * @param stateType
     */
    public void setStateType(StateType stateType) {
        this.stateType = stateType;
    }

    public enum StateType {
        Running,
        Resumed,
        Paused,
        Stopped,
        Killed
    }

}
