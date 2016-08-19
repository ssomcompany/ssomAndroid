package com.ssomcompany.ssomclient.push;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ssomcompany.ssomclient.BaseApplication;

import java.io.IOException;

public class PushManageService extends Service {
    private static final String TAG = PushManageService.class.getSimpleName();
    private GoogleCloudMessaging gcm;

    public PushManageService() {
    }

    @Override
    public void onCreate() {
        Log.i(TAG,"pushManageService on create");
        super.onCreate();
        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
        register();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void register() {
        Log.i(TAG, "gcm register");
        new AsyncTask(){
            protected Object doInBackground(final Object... params) {
                String token;
                try {
                    token = gcm.register(BaseApplication.getInstance().getGCMSenderID());
                    Log.i(TAG, "token :: " + token);
                }
                catch (IOException e) {
                    Log.i(TAG, e.getMessage());
                }
                return true;
            }
        }.execute(null, null, null);
    }
}