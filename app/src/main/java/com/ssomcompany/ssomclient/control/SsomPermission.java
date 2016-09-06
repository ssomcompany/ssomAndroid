package com.ssomcompany.ssomclient.control;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.ssomcompany.ssomclient.BaseApplication;

import java.util.ArrayList;

/**
 * Created by AaronMac on 2016. 9. 6..
 */
public class SsomPermission {

    private static final String TAG = SsomPermission.class.getSimpleName();

    private static SsomPermission instance;
    private ViewListener.OnPermissionListener listener;

    private String[] permissions;

    public static SsomPermission getInstance() {
        if(instance == null) {
            instance = new SsomPermission();
        }

        return instance;
    }

    public SsomPermission setOnPermissionListener(ViewListener.OnPermissionListener listener) {
        this.listener = listener;

        return this;
    }

    public SsomPermission setPermissions(String... permissions) {
        this.permissions = permissions;

        return this;
    }

    public void checkPermission() {
        if (instance.listener == null) {
            throw new NullPointerException("You must setPermissionListener() on TedPermission");
        } else if (instance.permissions == null || instance.permissions.length == 0) {
            throw new NullPointerException("You must setPermissions() on TedPermission");
        }


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.d(TAG, "preMarshmallow");
            instance.listener.onPermissionGranted();
        } else {
            Log.d(TAG, "Marshmallow");
            ArrayList<String> deniedPermission = new ArrayList<>();
            for(String permission : instance.permissions) {
                if(ContextCompat.checkSelfPermission(BaseApplication.getInstance(),
                        permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedPermission.add(permission);
                }
            }

            if(deniedPermission.size() == instance.permissions.length) {
                instance.listener.onPermissionDenied(deniedPermission);
            } else {
                instance.listener.onPermissionGranted();
            }
        }
    }

}
