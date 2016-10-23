package com.ssomcompany.ssomclient.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ssomcompany.ssomclient.common.BitmapWorkerTask;
import com.ssomcompany.ssomclient.network.NetworkManager;

public class SsomNetworkImageView extends NetworkImageView {
    private Bitmap mLocalBitmap;

    private boolean mShowLocal;

    public void setLocalImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            mShowLocal = true;
        }
        this.mLocalBitmap = bitmap;
        requestLayout();
    }

    @Override
    public void setImageUrl(final String url, ImageLoader imageLoader) {
        if(NetworkManager.getInstance().hasBitmapFromMemoryCache(url)) {
//        if(NetworkManager.getInstance().hasBitmapInCache(url)) {
            Log.d("NetworkImage", "Image loading from cache...");
//            if(NetworkManager.getInstance().hasBitmapFromMemoryCache(url)) {
                setLocalImageBitmap(NetworkManager.getInstance().getBitmapFromMemoryCache(url));
//            } else {
//                BitmapWorkerTask uploadTask = new BitmapWorkerTask() {
//                    @Override
//                    protected void onPostExecute(Bitmap result) {
//                        super.onPostExecute(result);
//                        if (result != null) {
//                            // Add final bitmap to caches
//                            NetworkManager.getInstance().addBitmapToCache(url, result);
//                            setLocalImageBitmap(result);
//                        }
//                    }
//                };
//
//                uploadTask.execute(url);
//            }
        } else {
            mShowLocal = false;
            super.setImageUrl(url, imageLoader);
        }
    }

    public SsomNetworkImageView(Context context) {
        this(context, null);
    }

    public SsomNetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SsomNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed, left, top, right, bottom);
        if (mShowLocal) {
            setImageBitmap(mLocalBitmap);
        }
    }
}
