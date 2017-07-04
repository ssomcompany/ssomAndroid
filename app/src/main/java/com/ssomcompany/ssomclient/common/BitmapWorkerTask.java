package com.ssomcompany.ssomclient.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.concurrent.ExecutionException;

/**
 * this task makes get from url with glide library asynchronous
 */
public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private Context mContext;

    public BitmapWorkerTask(Context context) {
        mContext = context;
    }

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

        try {
            return Glide.with(mContext)
                    .load(imageKey)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .fitCenter()
                    .into(Util.convertDpToPixel(49), Util.convertDpToPixel(57))
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
