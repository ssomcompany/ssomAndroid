
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
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.common.AdvancedHandler;
import com.ssomcompany.ssomclient.common.LocationTracker;
import com.ssomcompany.ssomclient.common.SsomPreferences;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.push.MessageManager;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseActivity extends AppCompatActivity {
    protected static final String TAG = BaseActivity.class.getSimpleName();

    private static final String KEY_ACTIVITY_CREATE_TIME = "activity_create_time";
    private static final String PROGRESS_DIALOG_TAG = "WAIT";
    private CommonDialog mProgressDialog = null;
    private static ActivityManager mActivityManager;

    protected static LocationTracker locationTracker;
    private int progressCount = 0;
    private MessageManager messageManager;
    private long activityCreatedTime;

    private SsomPreferences session;
    private Vibrator vibrator;

    private final AtomicBoolean paused = new AtomicBoolean(false);
    protected final AdvancedHandler aHandler = new AdvancedHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        }

        if (null != savedInstanceState) {
            this.activityCreatedTime = savedInstanceState.getLong(KEY_ACTIVITY_CREATE_TIME);
        }

        if(session == null) {
            session = new SsomPreferences(BaseApplication.getInstance(), SsomPreferences.LOGIN_PREF);
        }

        if(vibrator == null) {
            vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
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
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
            } else if (MessageManager.BROADCAST_MESSAGE_RECEIVED_PUSH.equals(action) ||
                    MessageManager.BROADCAST_MESSAGE_OPENED_PUSH.equals(action)) {
                receivedPushMessage(intent);
            }
        }

    };

    protected void receivedPushMessage(Intent intent) {
        Log.d(TAG, "receivedPushMessage called");
        runVibrator();
    }

    public void runVibrator() {
        vibrator.vibrate(100);
    }

    // this method must be overwritten when use notification count on the activity
    protected void setMessageCount(String msgCount) {
        Log.d(TAG, "setMessageCount : " + msgCount);
    }

    public boolean isShowingProgressDialog() {
        return mProgressDialog != null && mProgressDialog.getFragmentManager() != null && mProgressDialog.isShowing();
    }

    private OnCancelListener basicCancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            Log.d(TAG, "nothing to dismiss");
        }
    };

    /**
     * Show progress dialog(Cancel by Back key)
     */
    public void showProgressDialog() {
        showProgressDialog(false);
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

        if (Util.isMessageCountCheckingExcludeActivity(this)) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(MessageManager.BROADCAST_MESSAGE_COUNT_CHANGE);
            filter.addAction(MessageManager.BROADCAST_MESSAGE_RECEIVED_PUSH);
            filter.addAction(MessageManager.BROADCAST_MESSAGE_OPENED_PUSH);

            LocalBroadcastManager.getInstance(BaseApplication.getInstance())
                    .registerReceiver(this.mLocalReceiver, filter);
        }
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

    public void showErrorMessage() {
        UiUtils.makeToastMessage(this, getString(R.string.error_occurred));
    }

    public SsomPreferences getSession() {
        return session;
    }

    public String getUserId() {
        return getSession().getString(SsomPreferences.PREF_SESSION_USER_ID, "");
    }

    public void setSessionInfo(String token, String userId, String todayImageUrl, int heartCount) {
        getSession().put(SsomPreferences.PREF_SESSION_TOKEN, token);
        getSession().put(SsomPreferences.PREF_SESSION_USER_ID, userId);
        getSession().put(SsomPreferences.PREF_SESSION_TODAY_IMAGE_URL, todayImageUrl);
        getSession().put(SsomPreferences.PREF_SESSION_HEART, heartCount);
    }

    public String getTodayImageUrl() {
        return getSession().getString(SsomPreferences.PREF_SESSION_TODAY_IMAGE_URL, "");
    }

    public int getUnreadCount() {
        return getSession().getInt(SsomPreferences.PREF_SESSION_UNREAD_COUNT, 0);
    }

    public int getHeartCount() {
        return getSession().getInt(SsomPreferences.PREF_SESSION_HEART, 0);
    }
}
