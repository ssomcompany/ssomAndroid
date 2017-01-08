package com.ssomcompany.ssomclient.push;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;
import com.onesignal.shortcutbadger.ShortcutBadger;
import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.common.SsomPreferences;

import org.json.JSONObject;

/**
 * Created by AaronMac on 2016. 9. 22..
 */

public class SsomNotiReceiveHandler implements OneSignal.NotificationReceivedHandler {

    @Override
    public void notificationReceived(OSNotification notification) {
        JSONObject data = notification.payload.additionalData;
        String message = notification.payload.body;
        SsomPreferences pref = new SsomPreferences(BaseApplication.getInstance(), SsomPreferences.LOGIN_PREF);

        Log.d("receive", "data : " + data.toString());
        Log.d("receive", "received : " + message);

        int unreadCount = pref.getInt(SsomPreferences.PREF_SESSION_UNREAD_COUNT, 0);
        pref.put(SsomPreferences.PREF_SESSION_UNREAD_COUNT, ++unreadCount);
        ShortcutBadger.applyCount(BaseApplication.getInstance(), unreadCount);

        if(BaseApplication.getInstance().getCurrentActivityCount().get() != 0) {
            Log.d("receive", "local broad cast sent");

            Intent localIntent = new Intent(MessageManager.BROADCAST_MESSAGE_RECEIVED_PUSH);
            localIntent.putExtra(CommonConst.Intent.FROM_USER_ID, data.optString(CommonConst.Intent.FROM_USER_ID, null) == null ?
                    data.optString("userId", null) == null ? data.optString("ownerId", null) : data.optString("userId", null) : data.optString(CommonConst.Intent.FROM_USER_ID, null));
            localIntent.putExtra(CommonConst.Intent.TO_USER_ID, data.optString(CommonConst.Intent.TO_USER_ID, null) == null ?
                    data.optString("participantId", null) : data.optString(CommonConst.Intent.TO_USER_ID, null));
            localIntent.putExtra(CommonConst.Intent.TIMESTAMP, data.optLong(CommonConst.Intent.TIMESTAMP, 0));
            localIntent.putExtra(CommonConst.Intent.CHAT_ROOM_ID, data.optString(CommonConst.Intent.CHAT_ROOM_ID, null) == null ?
                    data.optString("id", null) : data.optString(CommonConst.Intent.CHAT_ROOM_ID, null));
            localIntent.putExtra(CommonConst.Intent.STATUS, data.optString(CommonConst.Intent.STATUS, null) == null ?
                    "SYSTEM".equalsIgnoreCase(data.optString("msgType", null)) && "out".equalsIgnoreCase(message) ?
                            CommonConst.Chatting.MEETING_OUT : null : data.optString(CommonConst.Intent.STATUS, null));
            localIntent.putExtra(CommonConst.Intent.MESSAGE, message);

            LocalBroadcastManager.getInstance(BaseApplication.getInstance()).sendBroadcast(localIntent);
        } else {
            Log.d("receive", "app is not running");
            // TODO noti builder 생성
        }
    }
}
