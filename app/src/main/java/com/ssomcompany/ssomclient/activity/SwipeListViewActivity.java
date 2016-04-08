package com.ssomcompany.ssomclient.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ssomcompany.ssomclient.common.OnSwipeDetectListener;
import com.ssomcompany.ssomclient.common.Util;

public abstract class SwipeListViewActivity extends BaseActivity {
    private ListView list;
    private int REL_SWIPE_MIN_HORIZON_DISTANCE;
    private int REL_SWIPE_MAX_VERTICAL_DISTANCE;
    private int REL_SWIPE_THRESHOLD_VELOCITY;
    private Action mSwipeDetected = Action.None;

    public enum Action {
        LR, // Left to Right
        RL, // Right to Left
        TB, // Top to bottom
        BT, // Bottom to Top
        None // when no action was detected
    }

    public boolean swipeDetected() {
        return mSwipeDetected == Action.RL || mSwipeDetected == Action.LR;
    }

    public boolean scrollDetected() {
        return mSwipeDetected == Action.TB || mSwipeDetected == Action.BT;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    /**
     *
     * @return ListView
     */
    public abstract ListView getListView();

    /**
     * @param position
     *            which item position is swiped
     */
    public abstract void getFlingItem(int position);

    /**
     * @param distanceX
     *            x distance for scrolled item in pixels
     * @param position
     *            the listView's item position
     */
    public abstract void getScrollItem(float distanceX, int position);

    /**
     * For single tap/Click
     *
     * @param adapter
     *            the listView's adapter gotten by getListView()
     * @param position
     *            the listView's item position
     */
    public abstract void onItemClickListener(ListAdapter adapter, int position);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        REL_SWIPE_MIN_HORIZON_DISTANCE = Util.convertDpToPixel(30);
        REL_SWIPE_MAX_VERTICAL_DISTANCE = Util.convertDpToPixel(15);
        REL_SWIPE_THRESHOLD_VELOCITY = Util.convertDpToPixel(100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        list = getListView();
        if (list == null) {
            new Throwable("Listview not set exception");
        }

        final GestureDetector gestureDetector = new GestureDetector(SwipeListViewActivity.this, new MyGestureDetector());
//        final OnSwipeDetectListener swipeDetector = new OnSwipeDetectListener();
//        list.setOnTouchListener(swipeDetector);
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (swipeDetector.swipeDetected()){
//                    // do the onSwipe action
//                    Log.d(TAG, "swipeDetected() : " + position);
//                } else {
//                    // do the onItemClick action
//                    Log.d(TAG, "item clicked : " + position);
//                }
//            }
//        });
        list.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void myOnItemClick(int position) {
        if (position < 0)
            return;
        onItemClickListener(list.getAdapter(), position);
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private int temp_position = -1;
        private int total_distance = 0;

        // Detect a single-click and call my own handler.
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int pos = list.pointToPosition((int) e.getX(), (int) e.getY());
            myOnItemClick(pos);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDown called!");
            mSwipeDetected = Action.None;
            temp_position = list.pointToPosition((int) e.getX(), (int) e.getY());
            total_distance = 0;
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            total_distance += distanceX;
            float deltaX = e1.getX() - e2.getX();
            float deltaY = e1.getY() - e2.getY();
//            int pos = list.pointToPosition((int) e1.getX(), (int) e2.getY());

            if (!scrollDetected() && Math.abs(deltaX) > REL_SWIPE_MIN_HORIZON_DISTANCE) {
                if (deltaX > 0)
                    mSwipeDetected = Action.RL;
                if (deltaX < 0)
                    mSwipeDetected = Action.LR;

                getScrollItem(total_distance, temp_position);
                return true;
            } else if (!swipeDetected() && Math.abs(deltaY) > REL_SWIPE_MAX_VERTICAL_DISTANCE) {
                Log.d(TAG, "vertical scrolled!");
                if (deltaY < 0) {
                    mSwipeDetected = Action.TB;
                    return false;
                }
                if (deltaY > 0) {
                    mSwipeDetected = Action.BT;
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
//            if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_VERTICAL_DISTANCE)
//                return false;
            Log.d(TAG, "onFling called!");

            // swipe right to left
            if (e1.getX() - e2.getX() > REL_SWIPE_MIN_HORIZON_DISTANCE
                    && Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {

//                int pos = list.pointToPosition((int) e1.getX(), (int) e2.getY());

//                if (pos >= 0 && temp_position == pos)
                getFlingItem(temp_position);
                return false;
            }
            // swipe left to right
//            else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_HORIZON_DISTANCE
//                    && Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
//
//                int pos = list
//                        .pointToPosition((int) e1.getX(), (int) e2.getY());
//                if (pos >= 0 && temp_position == pos)
//                    getFlingItem(pos);
//
//            }
            return true;
        }
    }
}
