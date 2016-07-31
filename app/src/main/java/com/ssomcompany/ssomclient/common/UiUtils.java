package com.ssomcompany.ssomclient.common;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by AaronMac on 2016. 7. 27..
 */
public class UiUtils {

    private static final String TAG = UiUtils.class.getSimpleName();

    public static void makeToastMessage(Context context, String message) {
        Log.d(TAG, "toast start!");
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
