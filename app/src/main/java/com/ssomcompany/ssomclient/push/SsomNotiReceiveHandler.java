package com.ssomcompany.ssomclient.push;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;
import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.activity.MainActivity;
import com.ssomcompany.ssomclient.activity.SsomChattingActivity;
import com.ssomcompany.ssomclient.activity.SsomChattingGuideActivity;
import com.ssomcompany.ssomclient.common.CommonConst;

import org.json.JSONObject;

/**
 * Created by AaronMac on 2016. 9. 22..
 */

public class SsomNotiReceiveHandler implements OneSignal.NotificationReceivedHandler {

    @Override
    public void notificationReceived(OSNotification notification) {
        JSONObject data = notification.payload.additionalData;
        String message = notification.payload.body;

        Log.d("receive", "received : " + message);

        if (data != null) {
            if(BaseApplication.getInstance().getCurrentActivityCount().get() != 0) {
                Log.d("receive", "local broad cast sent");

                Intent localIntent = new Intent(MessageManager.BROADCAST_MESSAGE_RECEIVED_PUSH);
                localIntent.putExtra(CommonConst.Intent.FROM_USER_ID, data.optString(CommonConst.Intent.FROM_USER_ID, null));
                localIntent.putExtra(CommonConst.Intent.TO_USER_ID, data.optString(CommonConst.Intent.TO_USER_ID, null));
                localIntent.putExtra(CommonConst.Intent.TIMESTAMP, data.optInt(CommonConst.Intent.TIMESTAMP, 0));
                localIntent.putExtra(CommonConst.Intent.CHAT_ROOM_ID, data.optString(CommonConst.Intent.CHAT_ROOM_ID, null));
                localIntent.putExtra(CommonConst.Intent.MESSAGE, message);

                LocalBroadcastManager.getInstance(BaseApplication.getInstance()).sendBroadcast(localIntent);
            } else {
                Log.d("receive", "app is not running");
                // TODO noti builder 생성
            }
        }
    }
}
