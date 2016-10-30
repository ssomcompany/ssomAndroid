package com.ssomcompany.ssomclient.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.network.api.model.SsomItem;
import com.ssomcompany.ssomclient.push.MessageCountCheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    private static final String TAG = Util.class.getSimpleName();

    // 이메일정규식
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    //비밀번호정규식
    public static final Pattern VALID_PASSWOLD_REGEX_ALPHA_NUM = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{6,16}$"); // 6자리 ~ 16자리까지 가능

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static boolean validatePassword(String pwStr) {
        Matcher matcher = VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(pwStr);
        return matcher.matches();
    }

//    public static RoundImage getCircleBitmap(Bitmap bitmap) {
//        float width = bitmap.getWidth();
//        float height = bitmap.getHeight();
//        // Calculate image's size by maintain the image's aspect ratio
//
//        float percent = width / 100;
//        float scale = Math.min(width, height) / percent;
//        width *= (scale / 100);
//        height *= (scale / 100);
//
//        // Resizing image
//        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
//        return new RoundImage(cropCenter(resizedBitmap));
//    }

    // network imageView crop 을 위한 함수
    public static Bitmap cropCenter(Bitmap bmp) {
        int dimension = Math.min(bmp.getWidth(), bmp.getHeight());
        return ThumbnailUtils.extractThumbnail(bmp, dimension, dimension);
    }

    // main 화면의 marker crop 을 위한 함수
    public static Bitmap cropCenterBitmap(Bitmap bitmap) {
        final int IMAGE_SIZE = convertDpToPixel(47);
        boolean landscape = bitmap.getWidth() > bitmap.getHeight();

        float scale_factor;
        if (landscape) scale_factor = (float)IMAGE_SIZE / bitmap.getHeight();
        else scale_factor = (float)IMAGE_SIZE / bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scale_factor, scale_factor);

        Bitmap croppedBitmap;
        if (landscape){
            int start = (bitmap.getWidth() - bitmap.getHeight()) / 2;
            croppedBitmap = Bitmap.createBitmap(bitmap, start, 0, bitmap.getHeight(), bitmap.getHeight(), matrix, true);
        } else {
            int start = (bitmap.getHeight() - bitmap.getWidth()) / 2;
            croppedBitmap = Bitmap.createBitmap(bitmap, 0, start, bitmap.getWidth(), bitmap.getWidth(), matrix, true);
        }

        return croppedBitmap;
    }

    /**
     * 파일 회전 임시
     */
    public static String rotatePhoto(Context context, String path) {
        if (!TextUtils.isEmpty(path)) {
            int exifDegree = getOrientationFromUri(path);
            if (exifDegree != 0) {
//                Bitmap bitmap = getBitmap(path);
//                Bitmap rotatePhoto = rotate(BitmapFactory.decodeFile(path), exifDegree);
                return saveBitmapToJpeg(context, rotate(getBitmap(path), exifDegree));
            }
        }
        return path;
    }

    public static Bitmap rotate(Bitmap image, int degrees) {
        if (degrees != 0 && image != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, image.getWidth(), image.getHeight());

            try {
                Bitmap b = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), m, true);

                if (!image.equals(b)) {
                    image.recycle();
                    image = b;
                }

                image = b;
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
            }
        }
        return image;
    }

    private static Bitmap getBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inInputShareable = true;
        options.inDither = false;
        options.inTempStorage = new byte[32 * 1024];
        options.inPurgeable = true;
        options.inJustDecodeBounds = false;
        options.inSampleSize = 8;

        File f = new File(path);

        FileInputStream fs = null;
        try {
            fs = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            // TODO do something intelligent
            e.printStackTrace();
        }

        Bitmap bm = null;

        try {
            if (fs != null) {
                bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
            }
        } catch (IOException e) {
            // TODO do something intelligent
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }

    public static int getOrientationFromUri(String path) {
        int orientation = 0;
        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = 270;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    orientation = 0;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "orientation : " + orientation);
        return orientation;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap input, float radius, boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
        int w = input.getWidth(), h = input.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, radius, radius, paint);


        //draw rectangles over the corners we want to be square
        if (topLeft){
            canvas.drawRect(0, 0, w/2, h/2, paint);
        }
        if (topRight){
            canvas.drawRect(w/2, 0, w, h/2, paint);
        }
        if (bottomLeft){
            canvas.drawRect(0, h/2, w/2, h, paint);
        }
        if (bottomRight){
            canvas.drawRect(w/2, h/2, w, h, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(input, 0,0, paint);

        return output;
    }

    // refill 기준시간이 없으면 4시간으로 셋팅함
    // 4시간 보다 전에 요청되었던 것이면 하트를 1개 채우기 위해 1초를 반환, 아니면 gap을 반환하여 타이머 시작
    public static long getRefillTime(long timestamp) {
        long currentTimestamp = System.currentTimeMillis();
        return timestamp == 0 ? 4 * 60 * 60 * 1000 :
                currentTimestamp - timestamp > 4 * 60 * 60 * 1000 ? 1000 : 4 * 60 * 60 * 1000 - (currentTimestamp - timestamp);
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
     * 채팅 메시지를 동일시간에 보여주지 않기 위해 비교하는 함수
     * @param beforeTime 비교대상 1
     * @param afterTime 비교대상 2
     * @return 1분 이내이고 같은 시간인 경우 true, otherwise false
     */
    public static boolean isSameTimeBetweenTwoTimes(long beforeTime, long afterTime) {
        Calendar beforeCalendar = Calendar.getInstance();
        beforeCalendar.setTimeInMillis(beforeTime);
        Calendar afterCalendar = Calendar.getInstance();
        afterCalendar.setTimeInMillis(afterTime);
        long gap = Math.abs(afterTime - beforeTime);
        return gap < 60 * 1000 && beforeCalendar.get(Calendar.MINUTE) == afterCalendar.get(Calendar.MINUTE);
    }

    public static String getTimeTextForMessage(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.KOREA);
            Date currentTimeZone = calendar.getTime();
            Log.d(TAG, "message time : " + sdf.format(currentTimeZone));
            return sdf.format(currentTimeZone);
        }catch (Exception e) {
            Log.e(TAG, "Failed to get time.");
        }
        return "12:00";
    }

    public static String getTimeTextForChatRoom(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("a h:mm", Locale.KOREA);
            Date currentTimeZone = calendar.getTime();
            return sdf.format(currentTimeZone);
        }catch (Exception e) {
            Log.e(TAG, "Failed to get time.");
        }
        return "오후 12:00";
    }

    ////////////////////////////////////////////////////// image file for camera ///////////////////////////////////////////////////

    // file directory 가져오기
    public static String getFileRootPath(Context context) {
        File rootFile = context.getExternalFilesDir(null);
        if (rootFile != null) {
            return rootFile.getPath() + "/";
        }

        return null;
    }

    public static String saveBitmapToJpeg(Context context, Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(new Date());

        Environment.getExternalStorageDirectory().getAbsolutePath();

        String folderName = "upload/";
        String fileName = timeStamp + ".jpg";
        String stringPath = getFileRootPath(context) + folderName;

        File filePath;
        try {
            filePath = new File(stringPath);
            if (!filePath.isDirectory()) {
                filePath.mkdir();
            }

            FileOutputStream out = new FileOutputStream(stringPath + fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            return stringPath + fileName;

        } catch (FileNotFoundException e) {
            Log.v(TAG, "FileNotFoundException : " + e.toString());
        } catch (IOException e) {
            Log.v(TAG, "IOException : " + e.toString());
        }
        return null;
    }

    ////////////////////////////////////////////////// convert pixel /////////////////////////////////////////////////////////

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertDpToPixel(float dp){
        Resources resources = BaseApplication.getInstance().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px){
        Resources resources = BaseApplication.getInstance().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return  px / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /////////////////////////////////////////////////////// convert variable /////////////////////////////////////////////////

    /**
     * This method coverts Map to ArrayList.
     *
     * @param items A map
     */
    public static ArrayList<SsomItem> convertMapToArrayList(Map<String, SsomItem> items) {
        ArrayList<SsomItem> arrayList = new ArrayList<>();
        for(Map.Entry<String, SsomItem> item : items.entrySet()) {
            arrayList.add(item.getValue());
        }
        return arrayList;
    }

    /**
     * This method converts all list to ssom list
     *
     * @param allList all list arrayList
     */
    public static ArrayList<SsomItem> convertAllListToSsomList(ArrayList<SsomItem> allList) {
        ArrayList<SsomItem> toList = new ArrayList<>();
        for(SsomItem item : allList) {
            if(CommonConst.SSOM.equals(item.getSsomType())) toList.add(item);
        }

        return toList;
    }

    /**
     * This method converts all list to ssoa list
     *
     * @param allList all list arrayList
     */
    public static ArrayList<SsomItem> convertAllListToSsoaList(ArrayList<SsomItem> allList) {
        ArrayList<SsomItem> toList = new ArrayList<>();
        for(SsomItem item : allList) {
            if(!CommonConst.SSOM.equals(item.getSsomType())) toList.add(item);
        }

        return toList;
    }

    /**
     * This method converts all map to ssom map
     *
     * @param allMap all map HashMap
     */
    public static Map<String, SsomItem> convertAllMapToSsomMap(Map<String, SsomItem> allMap) {
        Map<String, SsomItem> toMap = new HashMap<>();
        for(Map.Entry<String, SsomItem> item : allMap.entrySet()) {
            if(CommonConst.SSOM.equals(item.getValue().getSsomType())) toMap.put(item.getKey(), item.getValue());
        }

        return toMap;
    }

    /**
     * This method converts all map to ssoa map
     *
     * @param allMap all map HashMap
     */
    public static Map<String, SsomItem> convertAllMapToSsoaMap(Map<String, SsomItem> allMap) {
        Map<String, SsomItem> toMap = new HashMap<>();
        for(Map.Entry<String, SsomItem> item : allMap.entrySet()) {
            if(!CommonConst.SSOM.equals(item.getValue().getSsomType())) toMap.put(item.getKey(), item.getValue());
        }

        return toMap;
    }

    /**
     *  This method converts age to age range
     *
     *  @param age int
     */
    public static String convertAgeRange(int age) {
        String ageRange = "";
        switch (age) {
            case 20:
                ageRange = "20대 초반";
                break;
            case 25:
                ageRange = "20대 중반";
                break;
            case 29:
                ageRange = "20대 후반";
                break;
            case 30:
                ageRange = "30대";
                break;
        }
        return ageRange;
    }

    /**
     *  This method converts age to age range of one character at the end
     *
     *  @param age int
     */
    public static String convertAgeRangeAtBackOneChar(int age) {
        String ageRange = "";
        switch (age) {
            case 20:
                ageRange = "20대 초반";
                break;
            case 25:
                ageRange = "20대 중반";
                break;
            case 29:
                ageRange = "20대 후반";
                break;
            case 30:
                ageRange = "30대";
                break;
        }
        return ageRange;
    }

    /**
     *  This method converts count to people range
     *
     *  @param count int
     */
    public static String convertPeopleRange(int count) {
        String peopleRange = "";
        switch (count) {
            case 1:
                peopleRange = "1명";
                break;
            case 2:
                peopleRange = "2명";
                break;
            case 3:
                peopleRange = "3명";
                break;
            case 4:
                peopleRange = "4명 이상";
                break;
        }
        return peopleRange;
    }

    public static String getDecodedString(String content) {
        try {
            return URLDecoder.decode(content, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return content;
        }
    }

    public static String getEncodedString(String content) {
        try {
            return URLEncoder.encode(content, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return content;
        }
    }

    /**
     *
     * @param activity target activity
     * @return boolean
     */
    public static boolean isMessageCountCheckingExcludeActivity(Activity activity) {
        Class<?>[] validList = new Class<?>[] {
                /* BaseLoginActivity.class, BaseIntroActivity.class, PassCodeActivity.class, OtpVerificationActivity.class, */
                MessageCountCheck.class
        };

        if (null == activity) {
            return false;
        }

        for (Class<?> exclude : validList) {
            if (exclude.isInstance(activity)) {
                Log.v(TAG, activity.getClass().getSimpleName() + " is instance of " + exclude.getSimpleName());
                return true;
            }
        }

        return false;
    }

    /////////////////////////////////////////////// convert String spannable ////////////////////////////////////////////////////

    /**
     *
     * @param context context for the view
     * @param sysStrRes string resources being converted to
     * @param colorRes string color
     * @return spannableStringBuilder for spannable TextView
     */
    public static SpannableStringBuilder getSystemMsg(Context context, int sysStrRes, int colorRes) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString redSpannable= new SpannableString(context.getString(sysStrRes));
        redSpannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(colorRes)), 0, context.getString(sysStrRes).length(), 0);
        builder.append(redSpannable);
        return builder;
    }
}
