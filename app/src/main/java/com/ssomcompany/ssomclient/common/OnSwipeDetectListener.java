package com.ssomcompany.ssomclient.common;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeDetectListener implements View.OnTouchListener {
    private static final String TAG = OnSwipeDetectListener.class.getSimpleName();

    public enum Action {
        LR, // Left to Right
        RL, // Right to Left
        TB, // Top to bottom
        BT, // Bottom to Top
        None // when no action was detected
    }

    private static final int MIN_HORIZON_DISTANCE = 100;
    private static final int MIN_VERTICAL_DISTANCE = 50;
    private float downX, downY;
    private Action mSwipeDetected = Action.None;

    public boolean swipeDetected() {
        return mSwipeDetected == Action.RL || mSwipeDetected == Action.LR;
    }

    public boolean scrollDetected() {
        return mSwipeDetected == Action.TB || mSwipeDetected == Action.BT;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                mSwipeDetected = Action.None;
                return false; // allow other events like Click to be processed
            }
            case MotionEvent.ACTION_MOVE: {
                float upX = event.getX();
                float upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // horizontal swipe detection
                if (!scrollDetected() && Math.abs(deltaX) > MIN_HORIZON_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        Log.d(TAG, "Swipe Left to Right");
                        mSwipeDetected = Action.LR;
                        return true;
                    }
                    if (deltaX > 0) {
                        Log.d(TAG, "Swipe Right to Left");
                        mSwipeDetected = Action.RL;
                        return true;
                    }
                }
                else if (!swipeDetected() && Math.abs(deltaY) > MIN_VERTICAL_DISTANCE) { // vertical swipe detection
                    // top or down
                    if (deltaY < 0) {
                        Log.d(TAG, "Swipe Top to Bottom");
                        mSwipeDetected = Action.TB;
                        return false;
                    }
                    if (deltaY > 0) {
                        Log.d(TAG, "Swipe Bottom to Top");
                        mSwipeDetected = Action.BT;
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
