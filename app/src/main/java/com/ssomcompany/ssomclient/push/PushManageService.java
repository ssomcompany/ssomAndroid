package com.ssomcompany.ssomclient.push;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ssomcompany.ssomclient.R;

import java.io.IOException;

public class PushManageService extends Service {
    private GoogleCloudMessaging gcm;

    public PushManageService() {
    }

    @Override
    public void onCreate() {
        Log.i("kshgizmo","pushManageService on create");
        super.onCreate();
        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void register() {
        Log.i("kshgizmo", "gcm register");
        new AsyncTask(){
            protected Object doInBackground(final Object... params) {
                String token;
                try {
                    token = gcm.register(getString(R.string.project_number));
                    Log.i("registrationId", token);
                }
                catch (IOException e) {
                    Log.i("Registration Error", e.getMessage());
                }
                return true;
            }
        }.execute(null, null, null);
    }
}
