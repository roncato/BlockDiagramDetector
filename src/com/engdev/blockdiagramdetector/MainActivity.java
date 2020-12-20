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
import android.os.Bundle;
import android.view.Menu;
import com.engdev.blockdiagramdetector.state.ActivityState;
import com.engdev.blockdiagramdetector.state.ActivityState.StateType;
import com.engdev.blockdiagramdetector.state.State;
import com.engdev.blockdiagramdetector.state.StateMachine;

public class MainActivity extends AbstractActivity {

    private AppBlockDiagramDetector app = null;
    private ActivityStateMachine stateMachine = new ActivityStateMachine();

    private void init() {
        (app = new AppBlockDiagramDetector(this)).run();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public AppBlockDiagramDetector getApp() {
        return app;
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
        setState(new ActivityState(StateType.Running));
    }

    @Override
    public void onActivityStateChange(ActivityState state) {
        stateMachine.onStateChange(state);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        app.handleActivityResult(requestCode, resultCode, data);
    }

    private class ActivityStateMachine extends StateMachine {

        @Override
        public void onStateChange(State state) {
            ActivityState st = (ActivityState) state;
            switch (st.getStateType()) {
                case Running:
                    handleRunningState();
                    break;
                case Paused:
                    handlePausedState();
                    break;
                case Stopped:
                    handleStoppedState();
                    break;
                case Killed:
                    handleKilledState();
                    break;
                default:
            }
        }

        private void handleKilledState() {
        }

        private void handleStoppedState() {
        }

        private void handlePausedState() {
        }

        private void handleRunningState() {
        }

    }

}
