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
 * Represents an App state
 *
 * @author Lucas Batista
 */
public class AppState implements State {
    private StateType stateType;

    ;

    /**
     * @param stateType
     */
    public AppState(StateType stateType) {
        setStateType(stateType);
    }

    /**
     * Returns State type
     *
     * @return state type
     */
    public StateType getStateType() {
        return stateType;
    }

    /**
     * Sets State type
     *
     * @param stateType
     */
    public void setStateType(StateType stateType) {
        this.stateType = stateType;
    }

    /**
     * State machine state types
     */
    public enum StateType {
        ScreenBlank,
        BrowsingImage,
        ImageLoaded,
        TracingObjects,
        RecognizingObjects,
        UserRecognition,
        BuildingBlockDiagram,
        BlockDiagramBuilt,
        ExportingBlockDiagram,
        SavingBlockDiagram,
        ResettingApp,
        PreviewingCamera,
        AcceptingImage
    }
}
