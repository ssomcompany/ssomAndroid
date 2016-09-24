package com.ssomcompany.ssomclient.widget;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class SsomToast extends Toast {

    private SsomToast(Context context) {
        super(context);
        setGravity(Gravity.TOP, 0, 50);
    }

    public static SsomToast getInstance(Context context) {
        return new SsomToast(context);
    }

    public static void makeText(Context context, String message) {
        makeText(context, message, LENGTH_SHORT).show();
    }
}
