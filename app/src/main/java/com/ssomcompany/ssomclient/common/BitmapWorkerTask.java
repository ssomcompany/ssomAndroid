package com.ssomcompany.ssomclient.common;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.ssomcompany.ssomclient.network.NetworkManager;

import java.lang.ref.WeakReference;

/**
 * Created by AaronMac on 2016. 10. 23..
 */

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... params) {
        final String imageKey = params[0];

        if(this.isCancelled()) {
            return null;
        }

        // Check disk cache in background thread
        return NetworkManager.getInstance().getBitmapFromDiskCache(imageKey);
    }
}
