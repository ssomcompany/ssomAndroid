
package com.ssomcompany.ssomclient.common;

import android.os.Handler;
import android.util.Log;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Message Handler that supports buffering up of runnable when the activity is paused. Buffered runnable will be executed when the activity is
 * resumed.
 *
 * @author aaron.choi
 */
public class AdvancedHandler extends Handler {

    private static final String TAG = AdvancedHandler.class.getSimpleName();

    /**
     * Runnable Queue Buffer
     */
    private final Vector<Runnable> mRunnableQueue = new Vector<>();

    /**
     * Flag indicating the state of activity
     */
    private final AtomicBoolean mIsPaused = new AtomicBoolean(true);

    /**
     * the activity to use this class should call this function in onPause() or onStop()
     */
    public final void pause() {
        Log.d(TAG, "::AdvancedHandler::pause");
        this.mIsPaused.set(true);
    }

    /**
     * the activity to use this class should call this function in onResume() or onStart()
     */
    public final void resume() {
        Log.d(TAG, "::AdvancedHandler::resume");

        this.mIsPaused.set(false);

        while (0 < this.mRunnableQueue.size()) {
            final Runnable r = this.mRunnableQueue.remove(0);
            post(r);
            Log.d(TAG, "::AdvancedHandler::resume: executed : " + r.toString());
        }
    }

    /**
     * If current activity status is on resume, it will execute with no delay. Otherwise, the Runnable r is added to the runnable queue and will run
     * after activity resume.
     *
     * @param r The Runnable that will be executed.
     * @return Returns true if the Runnable was executed with no delay. otherwise returns false.
     */
    public final boolean postAdvanceDelayed(Runnable r) {

        if (this.mIsPaused.get()) {
            this.mRunnableQueue.add(r);

            Log.i(TAG, "::AdvancedHandler::postAdvanceDelayed: added : " + r.toString());

            return false;
        } else {
            post(r);

            Log.i(TAG, "::AdvancedHandler::postAdvanceDelayed: executed with no delay : " + r.toString());
            return true;
        }
    }

    /**
     * Causes the Runnable r to be added to the message queue, to be run after the specified amount of time elapses. The runnable will be run on the
     * thread to which this handler is attached. The time-base is android.os.SystemClock.uptimeMillis. Time spent in deep sleep will add an additional
     * delay to execution. And if current activity status is on resume, it will execute with no delay. Otherwise, the Runnable r is added to the
     * runnable queue and will run after activity resume.
     *
     * @param r The Runnable that will be executed.
     * @param delayMillis The delay(in milliseconds) until the runnable will be executed.
     * @return Returns true if the Runnable was successfully placed in to the message queue. Returns false on failure, usually because the looper
     *         processing the message queue is exiting.
     */
    public final boolean postAdvanceDelayed(final Runnable r, long delayMillis) {

        Runnable dispatchRunnable = new Runnable() {

            @Override
            public void run() {
                postAdvanceDelayed(r);
            }
        };

        return postDelayed(dispatchRunnable, delayMillis);
    }

    /**
     * Remove any pending posts of Runnable r that are in the message queue.
     */
    public final void removeAdvanceCallback(Runnable r) {
        removeCallbacks(r);
        this.mRunnableQueue.remove(r);
    }
}
