package com.ssomcompany.ssomclient.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.fragment.ChattingFragment;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

/**
 * Created by AaronMac on 2016. 7. 27..
 */
public class UiUtils {

    private static final String TAG = UiUtils.class.getSimpleName();

    public static void makeToastMessage(Context context, String message) {
        Log.d(TAG, "toast start!");
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void makeCommonDialog(Activity context, int buttonStyle, int title, int titleStyle,
                                        int message, int positiveBtnTitle, int negativeBtnTitle,
                                        final DialogInterface.OnClickListener positiveBtnListener,
                                        final DialogInterface.OnClickListener negativeBtnListener) {
        if (context.getResources() == null) {
            Log.e(TAG, "getResources() is null!!!!");
            return;
        }

        CommonDialog dialog = CommonDialog.getInstance(buttonStyle);
        dialog.setTitle(context.getString(title));
        if(titleStyle != 0) dialog.setTitleStyle(titleStyle);
        dialog.setMessage(context.getString(message));
        dialog.setPositiveButton(context.getString(positiveBtnTitle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(positiveBtnListener != null) positiveBtnListener.onClick(dialog, which);
            }
        });
        dialog.setNegativeButton(context.getString(negativeBtnTitle),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(negativeBtnListener != null) negativeBtnListener.onClick(dialog, which);
                    }
                });
        dialog.setAutoDismissEnable(true);
        dialog.show(context.getFragmentManager(), null);
    }
}
