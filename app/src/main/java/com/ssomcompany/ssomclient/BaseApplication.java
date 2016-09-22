
package com.ssomcompany.ssomclient;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.onesignal.OneSignal;
import com.onesignal.OneSignalDbHelper;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.push.SsomNotiOpenedHandler;

import java.util.concurrent.atomic.AtomicInteger;

public class BaseApplication extends Application implements ActivityLifecycleCallbacks {

    public String TAG = this.getClass().getSimpleName();

    private static BaseApplication mInstance;

    private static AtomicInteger activityCount = null;
    private Activity mCurrentActivity;

    private Configuration mOldConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.WARN);

        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new SsomNotiOpenedHandler())
                .autoPromptLocation(true)
                .init();

        mInstance = this;
        Log.d(TAG, TAG + " Created!!");

        mOldConfig = new Configuration(getResources().getConfiguration());

        try {
            activityCount = new AtomicInteger(0);
            registerActivityLifecycleCallbacks(this);
        } catch (Exception e) {
            Log.e(TAG, "error occurred while init application data", e);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.w(TAG, TAG + " terminated!!");
        try {
            unregisterActivityLifecycleCallbacks(this);
            mInstance = null;
        } catch (Exception e) {
            Log.e(TAG, "error occurred while terminate application", e);
        }
    }

    public static synchronized BaseApplication getInstance() {
        if (mInstance == null) {
            mInstance = new BaseApplication();
        }
        return mInstance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        int totalActivityCount = activityCount.incrementAndGet();
        Log.v(TAG, "target : " + (null == activity ? "null" : activity.getClass().getSimpleName()) + ", total activiy : " + totalActivityCount);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        int totalActivityCount = activityCount.decrementAndGet();
        Log.v(TAG, "target : " + (null == activity ? "null" : activity.getClass().getSimpleName()) + ", total activiy : " + totalActivityCount);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.v(TAG, "target : " + (null == activity ? "null" : activity.getClass().getSimpleName()));
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.v(TAG, "target : " + (null == activity ? "null" : activity.getClass().getSimpleName()));

        this.mCurrentActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.v(TAG, "target : " + (null == activity ? "null" : activity.getClass().getSimpleName()));
        this.mCurrentActivity = null;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.v(TAG, "target : " + (null == activity ? "null" : activity.getClass().getSimpleName()));
    }

    // Avoid prefixing parameters by in, out or inOut. Uses Javadoc to document this behavior.
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle stateToOut) {
        Log.v(TAG, "target : " + (null == activity ? "null" : activity.getClass().getSimpleName()));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d(TAG, "(old)" + this.mOldConfig);
        Log.d(TAG, "(new)" + newConfig);

        if (null == this.mOldConfig || ActivityInfo.CONFIG_LOCALE == (ActivityInfo.CONFIG_LOCALE & newConfig.diff(this.mOldConfig))) {
            if (null != this.mOldConfig) {
                Log.d(TAG, "locale changed");
            } else {
                Log.d(TAG, "previous status is empty");
            }
        }

        this.mOldConfig = new Configuration(newConfig);
    }

    public Activity getCurrentActivity() {
        return this.mCurrentActivity;
    }

    /**
     * Clears all application data, resetting its state back to initial install state
     */
    public void masterReset() {
        Log.v(TAG, "master reset called");
    }
}
