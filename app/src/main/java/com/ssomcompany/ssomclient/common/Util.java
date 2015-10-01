package com.ssomcompany.ssomclient.common;

import android.graphics.Bitmap;
import android.util.Log;

import com.ssomcompany.ssomclient.post.RoundImage;

/**
 * Created by kshgizmo on 2015-10-02.
 */
public class Util {

    public static RoundImage getCircleBitmap(Bitmap bitmap,int size){
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        Log.i("kshgizmo", "before : " + width + "/ " + height);
        // Calculate image's size by maintain the image's aspect ratio

        float percente = width / 100;
        float scale = size / percente;
        width *= (scale / 100);
        height *= (scale / 100);

        Log.i("kshgizmo","after : "+width + "/ "+height);
        // Resizing image
        Bitmap bitmapimg = Bitmap.createScaledBitmap(bitmap, (int) width, (int) width, true);

        return new RoundImage(bitmapimg);
    }
}
