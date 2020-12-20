package com.engdev.blockdiagramdetector.state;

public final class BlockDiagramParserState implements State {

    private StateType stateType;

    /**
     * @param stateType
     */
    public BlockDiagramParserState(StateType stateType) {
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

    public static enum StateType {
        Undetected,
        Tracing,
        Traced,
        Parsing,
        PartialRecognized,
        Parsed
    }

}
