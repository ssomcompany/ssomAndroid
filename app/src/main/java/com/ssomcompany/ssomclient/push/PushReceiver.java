package com.ssomcompany.ssomclient.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PushReceiver extends BroadcastReceiver {
    public PushReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent!=null) {
            Bundle extras = intent.getExtras();
            for (String key: extras.keySet()) {
                Log.i("kshgizmo", key + " : " + extras.get(key));
            }
        }
    }
}
