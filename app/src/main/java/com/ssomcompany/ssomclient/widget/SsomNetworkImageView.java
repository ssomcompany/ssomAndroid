package com.ssomcompany.ssomclient.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;

import com.android.volley.toolbox.*;
import com.ssomcompany.ssomclient.network.NetworkManager;

public class SsomNetworkImageView extends com.android.volley.toolbox.NetworkImageView {
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
            Log.d("NetworkImage", "Image loading from cache...");
            setLocalImageBitmap(NetworkManager.getInstance().getBitmapFromMemoryCache(url));
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
