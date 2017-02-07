package com.ssomcompany.ssomclient.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.common.Util;
import com.ssomcompany.ssomclient.push.MessageManager;

/**
 * Created by AaronMac on 2017. 1. 2..
 */

public class RetainedStateFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (Util.isMessageCountCheckingExcludeFragment(this)) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(MessageManager.BROADCAST_HEART_COUNT_CHANGE);

            LocalBroadcastManager.getInstance(BaseApplication.getInstance())
                    .registerReceiver(this.mLocalReceiver, filter);
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
            if (MessageManager.BROADCAST_HEART_COUNT_CHANGE.equals(action)) {
                receivedHeartChange(intent);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Util.isMessageCountCheckingExcludeFragment(this)) {
            LocalBroadcastManager.getInstance(BaseApplication.getInstance()).unregisterReceiver(mLocalReceiver);
        }
    }

    protected void receivedHeartChange(Intent intent) {
        Log.d(TAG, "receivedHeartChange called");
    }
}
