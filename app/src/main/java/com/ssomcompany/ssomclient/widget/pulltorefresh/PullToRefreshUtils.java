package com.ssomcompany.ssomclient.widget.pulltorefresh;

import android.util.Log;

public class PullToRefreshUtils {

    static final String TAG = PullToRefreshUtils.class.getSimpleName();

    public static void warnDeprecation(String depreacted, String replacement) {
        Log.d(TAG, "You're using the deprecated " + depreacted + " attr, please switch over to " + replacement);
    }

}
