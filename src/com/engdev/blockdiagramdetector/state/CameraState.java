package com.engdev.blockdiagramdetector.state;

/**
 * Represents an Camera state
 *
 * @author Lucas Batista
 */
public class CameraState implements State {
    private StateType stateType;

    ;

    /**
     * @param stateType
     */
    public CameraState(StateType stateType) {
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

    @Override
    public String toString() {
        return stateType.toString();
    }

    /**
     * State machine state types
     */
    public enum StateType {
        Uninitialized,
        Open,
        Previewing,
        PictureTaken,
        Stopped,
        Released
    }

}