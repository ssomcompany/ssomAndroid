package com.ssomcompany.ssomclient.network;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.widget.dialog.CommonDialog;

import java.io.File;

public class NetworkUtil {

    public static final String TAG = "NetworkManager";
    private static Object mLock = new Object();
    private static CommonDialog mNetworkErrorDialog;
    public static int HTTPS_DEFAULT_PORT = 443;

    public static String getSsomHostUrl() {
        String sb = "";
        sb += NetworkConstant.HTTP_SCHME;
        sb += NetworkConstant.HOST;

        return sb;
    }

//    wifi 필요한 기능 추가 시 적용
//    public static boolean isWifiOnline(Context context) {
//        try {
//            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//            State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//            if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
//                return true;
//            }
//
//        } catch (NullPointerException e) {
//            return false;
//        }
//
//        return false;
//    }

    public static boolean isMobileNetworkOnline(Context context) {
        try {
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
                return true;
            }

        } catch (NullPointerException e) {
            return false;
        }

        return false;
    }

    /**
     * 네트워크 연결상태 체크.
     *
     * @param context context
     * @return boolean
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void NetworkError(Context context) {
        NetworkError(context, 0, null, null);
    }

    public static void NetworkError(Context context, int resultCode) {
        NetworkError(context, resultCode, null, null);
    }

    public static void NetworkError(Context context, String message, DialogInterface.OnClickListener onClick) {
        NetworkError(context, 0, message, onClick);
    }

    public static void NetworkError(Context context, int resultCode, String message, DialogInterface.OnClickListener onClick) {
        Log.v(TAG, "::NetworkError");

        if (null == context || null == context.getResources()) {
            Log.w(BaseApplication.getInstance().TAG, "context or getResources is null. ignore action");
            return;
        }

        Log.i(TAG, "resultCode : " + resultCode);
        Log.i(TAG, "message : " + message);

        String dialogMessage = null;

        if (!TextUtils.isEmpty(message)) {
            dialogMessage = message;
        } else {
            dialogMessage = context.getResources().getString(R.string.an_unexpected_error_occur);
        }

        try {
            synchronized (mLock) {
                if (null != mNetworkErrorDialog) {
                    return;
                }
                mNetworkErrorDialog = CommonDialog.getInstance(CommonDialog.DIALOG_STYLE_ALERT_BUTTON);
                mNetworkErrorDialog.setTitle(R.string.notice);
                mNetworkErrorDialog.setMessage(dialogMessage);

                if (onClick == null) {
                    mNetworkErrorDialog.setPositiveButton(R.string.ok_upper, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            Log.d(TAG, "ok click...");
                        }
                    });

                } else {
                    mNetworkErrorDialog.setPositiveButton(R.string.ok_upper, onClick);
                }
                mNetworkErrorDialog.setDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mNetworkErrorDialog = null;
                    }
                });

                mNetworkErrorDialog.setCancelable(false);
                mNetworkErrorDialog.setCanceledOnTouchOutside(false);
                mNetworkErrorDialog.show(((Activity) context).getFragmentManager(), null);
            }
        } catch (

                Exception e)

        {
            Log.e(TAG, "create Dialog FAIL!", e);
        }

    }

    static boolean isExternalStorageRemovable() {
        return Environment.isExternalStorageRemovable();
    }

    static File getExternalCacheDir(Context context) {
        return context.getExternalCacheDir();
    }
}
