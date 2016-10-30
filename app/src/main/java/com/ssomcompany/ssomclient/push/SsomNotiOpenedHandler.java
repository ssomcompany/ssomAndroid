package com.ssomcompany.ssomclient.push;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.activity.IntroActivity;
import com.ssomcompany.ssomclient.activity.SsomChattingActivity;
import com.ssomcompany.ssomclient.common.CommonConst;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;

import org.json.JSONObject;

public class SsomNotiOpenedHandler implements OneSignal.NotificationOpenedHandler {

    // This fires when a notification is opened by tapping on it.
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String message = result.notification.payload.body;

        Log.d("receive", "received : " + message);
//        SsomItem ssomItem;
//
//        if (data != null) {
//            ssomItem = new SsomItem();
//            ssomItem.setUserId(data.optString(CommonConst.Intent.USER_ID, null));
//            ssomItem.setCreatedTimestamp();, data.optLong(CommonConst.Intent.TIMESTAMP, 0));
//            localIntent.putExtra(CommonConst.Intent.CHAT_ROOM_ID, data.optString(CommonConst.Intent.CHAT_ROOM_ID, null));
//            localIntent.putExtra(CommonConst.Intent.STATUS, data.optString(CommonConst.Intent.STATUS, null));
//            localIntent.putExtra(CommonConst.Intent.MESSAGE, message);
//        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken)
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

        // The following can be used to open an Activity of your choice.

        if(BaseApplication.getInstance().getCurrentActivityCount().get() == 0) {
            Log.i("OneSignalTabListener", "go to chatting activity !");
            Intent intent = new Intent(BaseApplication.getInstance(), IntroActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(CommonConst.Intent.IS_FROM_NOTI, true);
            BaseApplication.getInstance().startActivity(intent);
        } else {
            Log.d("receive", "local broad cast sent");

            Intent localIntent = new Intent(MessageManager.BROADCAST_MESSAGE_RECEIVED_PUSH);
            localIntent.putExtra(CommonConst.Intent.FROM_USER_ID, data.optString(CommonConst.Intent.FROM_USER_ID, null));
            localIntent.putExtra(CommonConst.Intent.TO_USER_ID, data.optString(CommonConst.Intent.TO_USER_ID, null));
            localIntent.putExtra(CommonConst.Intent.TIMESTAMP, data.optLong(CommonConst.Intent.TIMESTAMP, 0));
            localIntent.putExtra(CommonConst.Intent.CHAT_ROOM_ID, data.optString(CommonConst.Intent.CHAT_ROOM_ID, null) == null ?
                    data.optString("id", null) : data.optString(CommonConst.Intent.CHAT_ROOM_ID, null));
            localIntent.putExtra(CommonConst.Intent.STATUS, data.optString(CommonConst.Intent.STATUS, null));
            localIntent.putExtra(CommonConst.Intent.MESSAGE, message);

            LocalBroadcastManager.getInstance(BaseApplication.getInstance()).sendBroadcast(localIntent);
        }

        // Follow the instructions in the link below to prevent the launcher Activity from starting.
        // https://documentation.onesignal.com/docs/android-notification-customizations#changing-the-open-action-of-a-notification
    }
}
