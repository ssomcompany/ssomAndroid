
package com.ssomcompany.ssomclient.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.R;
import com.ssomcompany.ssomclient.activity.MainActivity;
import com.ssomcompany.ssomclient.common.UiUtils;
import com.ssomcompany.ssomclient.network.APICaller;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.api.GetHeartCount;
import com.ssomcompany.ssomclient.network.api.SsomChatUnreadCount;
import com.ssomcompany.ssomclient.network.model.SsomResponse;

public class MessageManager {
    public static final String TAG = MessageManager.class.getSimpleName();

    private final Context ctx;

    private static MessageManager mInstance;

    private static final int NOTIFICATION_ID = 6;
    public static final String BROADCAST_HEART_COUNT_CHANGE = "com.ssomcompany.ssomclient.BROADCAST_HEART_COUNT_CHANGE";
    public static final String BROADCAST_MESSAGE_COUNT_CHANGE = "com.ssomcompany.ssomclient.BROADCAST_MESSAGE_COUNT_CHANGE";
    public static final String BROADCAST_MESSAGE_RECEIVED_PUSH = "com.ssomcompany.ssomclient.BROADCAST_MESSAGE_RECEIVED_PUSH";
    public static final String EXTRA_KEY_MESSAGE_COUNT = "messageCount";
    public static final String EXTRA_KEY_HEART_COUNT = "heartCount";

    private Integer alarmCount = null;
    private Integer heartCount = null;
    private int pushCount = 0;

    private final LocalBroadcastManager localBroadcastManager;

    private MessageManager() {
        ctx = BaseApplication.getInstance().getApplicationContext();
        localBroadcastManager = LocalBroadcastManager.getInstance(ctx);
    }

    public static synchronized MessageManager getInstance() {
        if (mInstance == null) {
            mInstance = new MessageManager();
        }
        return mInstance;
    }

    public void getMessageCount(String token) {
        Log.d(TAG, "getMessageCount call");
        getUnreadCount(token);
    }

    public void getHeartCount(String token) {
        Log.d(TAG, "getHeartCount call");
        getTotalHeartCount(token);
    }

    // TODO push message setting 화면
//    public Intent movePushMessage() {
//        return new Intent(ctx, PushMessageActivity.class);
//    }

    public Intent moveHome() {
        Intent launchActivity = new Intent();
        launchActivity.setClassName(ctx, MainActivity.class.getName());
        launchActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        return launchActivity;
    }

    public void createPushMessage(Bundle bundle) {
        if (bundle == null) {
            return;
        }

//        String notiTypeCd = bundle.getString(CommonConst.PUSH_NOTI_TYPE_CD);
//        String notiId = bundle.getString(CommonConst.PUSH_NOTI_ID);
//        String notiDmsgUserId = bundle.getString(CommonConst.PUSH_NOTI_DMSG_USER_ID);
//        String krNotiMsg = bundle.getString(CommonConst.PUSH_KR_NOTI_MSG);
//        String enNotiMsg = bundle.getString(CommonConst.PUSH_EN_NOTI_MSG);

//        Log.d(TAG, "notiTypeCd : " + notiTypeCd);
//        Log.d(TAG, "notiId : " + notiId);
//        Log.d(TAG, "notiDmsgUserId : " + notiDmsgUserId);
//        Log.d(TAG, "notiTgtUrl : " + notiTgtUrl);
//        Log.d(TAG, "krNotiMsg : " + krNotiMsg);
//        Log.d(TAG, "enNotiMsg : " + enNotiMsg);

        Intent actionIntent = new Intent();

//        if (notiId != null) {
//            actionIntent.putExtra(CommonConst.PUSH_NOTI_ID, notiId);
//        }
//
        String pushMessage = null;
//
//        if (Locale.getDefault().equals(Locale.KOREA)) {
//            pushMessage = krNotiMsg;
//        } else {
//            pushMessage = enNotiMsg;
//        }
//
//        Log.i(TAG, "pushMessage : " + pushMessage);

        if (TextUtils.isEmpty(pushMessage)) {
            Log.i(TAG, "pushMessage is empty");
            return;
        }

        increasePushCount();

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, actionIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        dismissPushMessage();
        showPushMessage(ctx.getResources().getString(R.string.app_name), pushMessage, pendingIntent);
    }

    private void showPushMessage(String title, String contents, PendingIntent intent) {
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        StringBuilder content = new StringBuilder();
        content.append(contents);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
        builder.setSmallIcon(R.mipmap.ssom_icon)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content.toString()))
                .setAutoCancel(true)
                .setContentText(content.toString())
                .setNumber(pushCount)
                .setContentIntent(intent);

        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_ALL;

        notificationManager.notify(NOTIFICATION_ID, notification);

        receivedPush();
    }

    /**
     * Remove the message
     */
    private void dismissPushMessage() {
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void getTotalHeartCount(String token) {
        Log.i(TAG, "::getHeartCount:");

        APICaller.getHeartCount(token, new NetworkManager.NetworkListener<SsomResponse<GetHeartCount.Response>>() {
            @Override
            public void onResponse(SsomResponse<GetHeartCount.Response> response) {
                if(response.isSuccess() && response.getData() != null) {
                    Integer data = response.getData().getHeartsCount();

                    changeHeartCount(data);

                    Log.d(TAG, "heartCount : " + data);
                } else {
                    Log.i(TAG,
                            "getHeartCount error with code " + response.getResultCode() + ", message : " + response.getMessage(),
                            response.getError());
                    UiUtils.makeToastMessage(BaseApplication.getInstance(), BaseApplication.getInstance().getResources().getString(R.string.error_occurred));
                }
            }
        });
    }

    private void getUnreadCount(String token) {
        Log.i(TAG, "::getUnreadCount:");

        APICaller.totalChatUnreadCount(token, new NetworkManager.NetworkListener<SsomResponse<SsomChatUnreadCount.Response>>() {
            @Override
            public void onResponse(SsomResponse<SsomChatUnreadCount.Response> response) {
                if(response.isSuccess() && response.getData() != null) {
                    Integer data = response.getData().getUnreadCount();

                    changeCount(data);

                    Log.d(TAG, "alarmCount : " + data);
                } else {
                    Log.i(TAG,
                            "getUnreadCount error with code " + response.getResultCode() + ", message : " + response.getMessage(),
                            response.getError());
                    UiUtils.makeToastMessage(BaseApplication.getInstance(), BaseApplication.getInstance().getResources().getString(R.string.error_occurred));
                }
            }
        });
    }

    private void changeHeartCount(Integer count) {
        if (count == null) {
            heartCount = 0;
        } else {
            heartCount = count;
        }

        Log.d(TAG, "sendBroadcast: action=BROADCAST_HEART_COUNT_CHANGE");

        Intent countChangeIntent = new Intent(BROADCAST_HEART_COUNT_CHANGE);
        countChangeIntent.putExtra(EXTRA_KEY_HEART_COUNT, heartCount);

        localBroadcastManager.sendBroadcast(countChangeIntent);
    }

    private void changeCount(Integer count) {
        if (count == null) {
            alarmCount = 0;
        } else {
            alarmCount = count;
        }

        Log.d(TAG, "sendBroadcast: action=BROADCAST_MESSAGE_COUNT_CHANGE");

        Intent countChangeIntent = new Intent(BROADCAST_MESSAGE_COUNT_CHANGE);
        countChangeIntent.putExtra(EXTRA_KEY_MESSAGE_COUNT, alarmCount);

        localBroadcastManager.sendBroadcast(countChangeIntent);
    }

    private void receivedPush() {
        Log.d(TAG, "sendBroadcast: action=BROADCAST_MESSAGE_RECEIVED_PUSH");

        Intent pushIntent = new Intent(BROADCAST_MESSAGE_RECEIVED_PUSH);

        localBroadcastManager.sendBroadcast(pushIntent);
    }

    private void increasePushCount() {
        pushCount++;

        Log.v(TAG, "::increasePushCount: count=" + pushCount);
    }

    public void resetPushCount() {
        pushCount = 0;

        Log.v(TAG, "::increasePushCount: count=" + pushCount);
    }
}
