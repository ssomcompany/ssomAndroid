package com.ssomcompany.ssomclient.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.post.PostContent;

import java.util.ArrayList;
import java.util.Map;

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

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return  px / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method coverts Map to ArrayList.
     *
     * @param items A map
     */
    public static ArrayList<PostContent.PostItem> convertMapToArrayList(Map<String, PostContent.PostItem> items) {
        ArrayList<PostContent.PostItem> arrayList = new ArrayList<>();
        for(Map.Entry<String, PostContent.PostItem> item : items.entrySet()) {
            arrayList.add(item.getValue());
        }
        return arrayList;
    }
}
