
package com.ssomcompany.ssomclient.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.common.AdvancedHandler;
import com.ssomcompany.ssomclient.common.LocationTracker;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.push.MessageManager;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseActivity extends AppCompatActivity {
    protected static final String TAG = BaseActivity.class.getSimpleName();

    private static final String PROGRESS_DIALOG_TAG = "WAIT";
    private CommonDialog mProgressDialog = null;
    private static ActivityManager mActivityManager;
    protected static LocationTracker locationTracker;

    private int progressCount = 0;
    private MessageManager messageManager;
    private long activityCreatedTime;
    private final AtomicBoolean paused = new AtomicBoolean(false);

    protected final AdvancedHandler aHandler = new AdvancedHandler();

    private static final String KEY_ACTIVITY_CREATE_TIME = "activity_create_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        }

        locationTracker = LocationTracker.getInstance();

        if (null != savedInstanceState) {
            this.activityCreatedTime = savedInstanceState.getLong(KEY_ACTIVITY_CREATE_TIME);
        }

        if (0 == this.activityCreatedTime) {
            this.activityCreatedTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle stateBundle) {
        super.onSaveInstanceState(stateBundle);

        stateBundle.putLong(KEY_ACTIVITY_CREATE_TIME, this.activityCreatedTime);
    }

    @Override
    protected void onStart() {
        super.onStart();

        messageManager = MessageManager.getInstance();

        if (Util.isMessageCountCheckingExcludeActivity(this)) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(MessageManager.BROADCAST_MESSAGE_COUNT_CHANGE);
            filter.addAction(MessageManager.BROADCAST_MESSAGE_RECEIVED_PUSH);

            LocalBroadcastManager.getInstance(BaseApplication.getInstance())
                    .registerReceiver(this.mLocalReceiver, filter);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);

//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);

//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void finish(boolean isBasicFinish) {
        if (isBasicFinish) {
            super.finish();
        } else {
            this.finish();
        }
    }

    /**
     * receiver notification count change
     */
    private final BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "::mLocalReceiver::onReceive:" + intent);

            String action = intent.getAction();

            if (MessageManager.BROADCAST_MESSAGE_COUNT_CHANGE.equals(action)) {
                int count = intent.getIntExtra(MessageManager.EXTRA_KEY_MESSAGE_COUNT, 0);

                String msText = null;
                int countMax = 99;
                int countMin = 0;
                if (count > countMax) {
                    msText = "99+";
                } else if (count > countMin) {
                    msText = Integer.toString(count);
                }

                setMessageCount(msText);
            } else if (MessageManager.BROADCAST_MESSAGE_RECEIVED_PUSH.equals(action)) {
                refreshMessageCount();
            }
        }

    };

    protected void refreshMessageCount() {
        messageManager.getMessageCount();
    }

    protected void setMessageCount(String msCount) {
        Log.d(TAG, "setMessageCount : " + msCount);
    }

    public boolean isShowingProgressDialog() {
        return mProgressDialog != null && mProgressDialog.getFragmentManager() != null && mProgressDialog.isShowing();
    }

    private OnCancelListener basicCancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            NetworkManager.getInstance().cancelPendingRequests(TAG);
        }
    };

    /**
     * Show progress dialog(Cancel by Back key)
     */
    public void showProgressDialog() {
        showProgressDialog(true, basicCancelListener);
    }

    /**
     * Show progress dialog
     *
     * @param isCancelable (Cancel by Back key)
     */
    public void showProgressDialog(boolean isCancelable) {
        showProgressDialog(isCancelable, isCancelable ? basicCancelListener : null);
    }

    /**
     * Show progress dialog
     *
     * @param isCancelable (Cancel by Back key)
     * @param cancelListener This listener will be invoked when the dialog is canceled.
     */
    public void showProgressDialog(boolean isCancelable, OnCancelListener cancelListener) {
        try {
            makeProgressDialog();
            if (mProgressDialog.getFragmentManager() == null) {
                mProgressDialog.setCancelable(isCancelable);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show(getFragmentManager(), PROGRESS_DIALOG_TAG);

                if (null != cancelListener) {
                    mProgressDialog.setCancelListener(cancelListener);
                }
            }
            progressCount++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Make base progress dialog
     */
    private void makeProgressDialog() {
        if (progressCount > 0) {
            return;
        }

        try {
            if (mProgressDialog == null) {
                mProgressDialog = CommonDialog.getInstance(CommonDialog.DIALOG_STYLE_SPINNER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Dismiss progress dialog
     */
    public void dismissProgressDialog() {
        progressCount--;
        if (progressCount > 0) {
            return;
        }
        progressCount = 0;
        try {
            if (mProgressDialog != null && mProgressDialog.getFragmentManager() != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set cancel listener in ProgressDialog
     *
     * @param cancel OnCancelListener
     */
    public void setProgressDialogCancel(OnCancelListener cancel) {
        makeProgressDialog();
        mProgressDialog.setCancelListener(cancel);
    }

    /**
     * Set dismiss listener in ProgressDialog
     *
     * @param dismiss OnDismissListener
     */
    public void setProgressDialogDismiss(OnDismissListener dismiss) {
        makeProgressDialog();
        mProgressDialog.setDismissListener(dismiss);
    }

    /**
     * Set key listener in ProgressDialog
     *
     * @param listener OnKeyListener
     */
    public void setProgressDialogKeyListener(OnKeyListener listener) {
        makeProgressDialog();
        mProgressDialog.setKeyListener(listener);
    }

    /**
     * @return fragment
     */
    public Fragment getLastFragmentInStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.executePendingTransactions();

        int size = fragmentManager.getBackStackEntryCount();

        if (size >= 0) {
            String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()).getName();
            return fragmentManager.findFragmentByTag(fragmentTag);
        } else {
            return null;
        }
    }

    /**
     * This makes toast message short time
     * @param resId string resources id
     */
    public void showToastMessageShort(int resId) {
        Toast.makeText(getApplicationContext(), getResources().getString(resId), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public long getActivityCreatedTime() {
        return activityCreatedTime;
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.paused.set(false);
        this.aHandler.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.paused.set(true);
        this.aHandler.pause();

        if (Util.isMessageCountCheckingExcludeActivity(this)) {
            LocalBroadcastManager.getInstance(BaseApplication.getInstance()).unregisterReceiver(mLocalReceiver);
        }
    }

    public boolean isPaused() {
        return this.paused.get();
    }
}
