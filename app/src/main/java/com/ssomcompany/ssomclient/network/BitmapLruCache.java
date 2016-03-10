package com.ssomcompany.ssomclient.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by AaronMac on 2015. 12. 21..
 */
public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

    // Look over this, if needs to change cache size
    public static int getDefaultLruCacheSize() {
        int maxMemory = (int)(Runtime.getRuntime().maxMemory()/1024);
        int cacheSize = maxMemory / 8;
        return cacheSize;
    }

    // Constructor
    public BitmapLruCache() {
        super(getDefaultLruCacheSize());
    }

    public BitmapLruCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}
