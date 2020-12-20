/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import com.engdev.blockdiagramdetector.state.ActivityState;
import com.engdev.blockdiagramdetector.state.ActivityState.StateType;
import com.engdev.blockdiagramdetector.util.Buffer;

public abstract class AbstractActivity extends Activity {

    /**
     * Number of buffered states
     */
    public static final int BUFFERED_N_STATES = 50;
    protected Buffer<ActivityState> states = new Buffer<ActivityState>(BUFFERED_N_STATES);

    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setState(ActivityState state) {
        states.add(state);
        onActivityStateChange(state);
    }

    public void setAutoOrientationEnabled(boolean enabled) {
        Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
    }

    @Override
    public void onStop() {
        super.onStop();
        setState(new ActivityState(StateType.Stopped));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setState(new ActivityState(StateType.Killed));
    }

    @Override
    public void onPause() {
        super.onPause();
        setState(new ActivityState(StateType.Paused));
    }

    @Override
    public void onResume() {
        super.onResume();
        setState(new ActivityState(StateType.Resumed));
        setState(new ActivityState(StateType.Running));
    }

    /**
     * Handles paused event
     *
     * @param state
     */
    public abstract void onActivityStateChange(ActivityState state);

    public Buffer<ActivityState> getStates() {
        return states;
    }

}
