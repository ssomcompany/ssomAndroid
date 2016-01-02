package com.ssomcompany.ssomclient.common;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by kshgizmo on 2015-10-02.
 */
public class Util {

    public static RoundImage getCircleBitmap(Bitmap bitmap, int size) {
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        Log.i("kshgizmo", "before : " + width + "/ " + height);
        // Calculate image's size by maintain the image's aspect ratio

        float percente = width / 100;
        float scale = size / percente;
        width *= (scale / 100);
        height *= (scale / 100);

        Log.i("kshgizmo", "after : " + width + "/ " + height);
        // Resizing image
        Bitmap bitmapimg = Bitmap.createScaledBitmap(bitmap, (int) width, (int) width, true);

        return new RoundImage(bitmapimg);
    }

    public static String getTimeText(long timestamp) {
        long currentTimestamp = System.currentTimeMillis();
        long gap = currentTimestamp - timestamp;

        if (gap < 60 * 1000) {
            return "방금전";
        } else if (gap < 60 * 60 * 1000) {
            int min = (int) (gap / (60 * 1000));
            return min + "분전";
        } else if (gap < 24 * 60 * 60 * 1000) {
            int hour = (int) (gap / (60 * 60 * 1000));
            return hour + "시간전";
        } else {
            return "오래전";
        }
    }
}
