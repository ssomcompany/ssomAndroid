package com.ssomcompany.ssomclient.push;

import android.util.Log;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by AaronMac on 2016. 9. 23..
 */

public class SsomNotiOpenedHandler implements OneSignal.NotificationOpenedHandler {

    // This fires when a notification is opened by tapping on it.
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.actionType;
        JSONObject data = result.notification.payload.additionalData;
        String customKey;

        if (data != null) {
            customKey = data.optString("customkey", null);
            if (customKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + customKey);
        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken)
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

        // The following can be used to open an Activity of your choice.

        // Intent intent = new Intent(getApplication(), YourActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        // startActivity(intent);

        // Follow the instructions in the link below to prevent the launcher Activity from starting.
        // https://documentation.onesignal.com/docs/android-notification-customizations#changing-the-open-action-of-a-notification
    }
}
