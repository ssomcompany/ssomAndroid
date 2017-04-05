package com.ssomcompany.ssomclient.network;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.network.model.BaseRequest;
import com.ssomcompany.ssomclient.network.model.BaseResponse;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class NetworkManager {

    public static final String TAG = "NetworkManager";

    private static NetworkManager mInstance;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private BitmapLruCache bitmapCache;
    private DiskImageLruCache diskCache;


    private NetworkManager() {
        mRequestQueue = getRequestQueue();
        bitmapCache = new BitmapLruCache();
        diskCache = new DiskImageLruCache(BaseApplication.getInstance());
        mImageLoader = new ImageLoader(mRequestQueue, bitmapCache);
    }

    public static NetworkManager getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkManager();
        }

        return mInstance;
    }

    public Bitmap getBitmapFromCache(String key) {
        if(hasBitmapFromMemoryCache(key)) {
            return getBitmapFromMemoryCache(key);
        } else {
            return getBitmapFromDiskCache(key);
        }
    }

    public Bitmap getBitmapFromMemoryCache(String key) {
        if(key == null) return null;
        Log.d(TAG, "get bitmap from memory cache");
        return bitmapCache.getBitmap(key);
    }

    public Bitmap getBitmapFromDiskCache(String key) {
        Log.d(TAG, "get bitmap from disk cache");
        return diskCache.getBitmap(key);
    }

    public void addBitmapToCache(String key, Bitmap cacheBitmap) {
        Log.d(TAG, "addBitmapToCache called");

        if(!hasBitmapFromMemoryCache(key)) {
            Log.d(TAG, "stored in memory cache");
            this.bitmapCache.putBitmap(key, cacheBitmap);
        }

        // Also add to disk cache
        if(!hasBitmapFromDiskCache(key)) {
            Log.d(TAG, "stored in disk cache");
            diskCache.put(key, cacheBitmap);
        }
    }

    public void removeBitmapFromMemoryCache(String key) {
        if(key == null) return;
        this.bitmapCache.remove(key);
    }

    public boolean hasBitmapInCache(String key) {
        return hasBitmapFromMemoryCache(key) || hasBitmapFromDiskCache(key);
    }

    public boolean hasBitmapFromDiskCache(String key) {
        return diskCache.containsKey(key);
    }

    public boolean hasBitmapFromMemoryCache(String key) {
        return !TextUtils.isEmpty(key) && bitmapCache.getBitmap(key) != null;
    }

    @SuppressWarnings("deprecation")
    public RequestQueue getRequestQueue() {
        if(mRequestQueue == null) {
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.networkInterceptors().add(new StethoInterceptor());
            mRequestQueue = Volley.newRequestQueue(BaseApplication.getInstance(), new OkHttpStack(okHttpClient));
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    private <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    private <T> void addToRequestQueue(Request<T> req) {
        addToRequestQueue(req, null);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    private static <T> void doRequest(Request<T> request) {
        getInstance().addToRequestQueue(request);
    }

    public static Map<String, String> makeCommonHederParam(String contentType) {
        Map<String, String> headerParam = new HashMap<String, String>();

        headerParam.put(NetworkConstant.HeaderParam.CACHE_CONTROL, "no-cache");
        headerParam.put(NetworkConstant.HeaderParam.CONTENT_TYPE, contentType + "; charset=UTF-8");
        headerParam.put(NetworkConstant.HeaderParam.ACCEPT, contentType);

        return headerParam;
    }

    /**
     * A request for retrieving a Model object type of T from response body at a given URL.
     *
     * @param request driven class of BaseRequest to get request information
     * @param resType type of response model. driven class of BaseResponse
     * @param listener Callback interface for delivering parsed responses. if callback is useless, pass by null.
     * @param errorListener Callback interface for delivering error responses. if callback is useless, pass by null.
     */
    public static <T extends BaseResponse> void request(BaseRequest request, Type resType, Listener<T> listener, ErrorListener errorListener) {
        String url = request.getURL();

        printReqestData(request.getMethod(), url, request.getHeaders(), request.getPayload());

        doRequest(new GsonRequest<T>(request.getMethod(), url, resType, request.getHeaders(), request.getPayload(),
                addResponseLoggingInterceptor(url, listener), addErrorResponseLoggingInterceptor(url, errorListener), request.getRetryPolicy()));
    }

    /**
     * @param request driven class of BaseRequest to get request information
     * @param resType type of response model. driven class of BaseResponse
     * @param listener callback interface for delivering parsed responses. if callback is useless, pass by null. all of error and success response
     *            will be sent to this callback.
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseResponse> void request(BaseRequest request, final Type resType, final NetworkListener<T> listener) {

        if (null == request || null == resType) {
            Log.w(TAG, "invalid parameter!!. ignore action!!");
            return;
        }

        if (!NetworkUtil.isOnline(BaseApplication.getInstance())) {
            Log.w(TAG, "Network is offline. ignore action!!");
            T response = null;
            try {
                TypeToken<T> typeToken = (TypeToken<T>) TypeToken.get(resType);
                response = (T) typeToken.getRawType().newInstance();
                response.setError(new BaseResponse.NetworkOffLineException());
                response.saveLastRequest(request, resType, listener);
                response.setStatusCode(-1);

                if (null != listener) {
                    listener.onResponse(response);
                }

            } catch (Exception e) {
                Log.e(TAG, "exception occurred while creating response instance!!", e);
            }

            return;
        }

        NetworkManager.request(request, resType, new Listener<T>() {
            @Override
            @SuppressWarnings("unchecked")
            public void onResponse(T response) {
                if (null == listener) {
                    return;
                }

                if (null == response) {
                    try {
                        TypeToken<T> typeToken = (TypeToken<T>) TypeToken.get(resType);
                        response = (T) typeToken.getRawType().newInstance();
                        response.setError(new Exception("Unknown Exception Occurred. it seems that hand-over occurred."));
                        response.setStatusCode(-1);
                    } catch (Exception e) {
                        Log.e(TAG, "exception occurred while creating response instance!!", e);
                    }
                } else {
                    response.setStatusCode(200 /* HttpStatus.SC_OK */);
                }

//                if (response instanceof SsomResponse<?>) {
//                    Log.w(TAG, "Session Expired!!");
//                }

                listener.onResponse(response);
            }
        }, new ErrorListener() {

            @Override
            @SuppressWarnings("unchecked")
            public void onErrorResponse(VolleyError error) {
                if (null == listener) {
                    return;
                }

                try {
                    TypeToken<T> typeToken = (TypeToken<T>) TypeToken.get(resType);
                    T response = (T) typeToken.getRawType().newInstance();
                    response.setError(error);
                    if (null != error.networkResponse) {
                        response.setStatusCode(error.networkResponse.statusCode);
                    }
                    listener.onResponse(response);
                } catch (Exception e) {
                    Log.e(TAG, "exception occurred while creating response instance!!", e);
                    listener.onResponse(null);
                }
            }
        });
    }

    public interface NetworkListener<T extends BaseResponse> {
        void onResponse(T response);
    }

    /********************************************************************************************************
     * Logging utility
     ********************************************************************************************************/

    private static void printReqestData(int method, String url, Map<String, String> headerParam, Object payload) {
        Log.v(TAG, "******************** Request Data ********************");
        Log.d(TAG, "Method Type = " + method + ", URL = " + url);
        Log.d(TAG, "HeaderParam = " + (headerParam != null ? headerParam.toString() : "none"));
        Log.d(TAG, "payload = " + (payload != null ? payload.toString() : "none"));
        Log.v(TAG, "******************************************************");
    }

    private static <T> Listener<T> addResponseLoggingInterceptor(final String url, final Listener<T> listener) {
        return new Listener<T>() {

            @Override
            public void onResponse(T response) {
                Log.i(TAG, "******************** Response Data ********************");
                Log.d(TAG, "Url: " + url);
                // SSOM_PRODUCT_REMOVED_CODES_START
                Log.d(TAG, "Response: " + (null != response ? response.toString() : "none"));
                // SSOM_PRODUCT_REMOVED_CODES_END
                if (null != listener) {
                    listener.onResponse(response);
                } else {
                    Log.w(TAG, "there is no onResponse listener.");
                }
                Log.i(TAG, "*******************************************************");
            }
        };
    }

    private static ErrorListener addErrorResponseLoggingInterceptor(final String url, final ErrorListener listener) {
        return new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Error Type : TimeoutError, NoConnectionError,
                // AuthFailureError, ServerError, NetworkError, ParseError
                Log.w(TAG, "******************** Response Data ********************");
                Log.e(TAG, "Url: " + url);
                Log.e(TAG, (null == error ? "Error" : error.getClass().getSimpleName()) + ": " + error);
                if (null != error) {
                    Log.e(TAG, "status code : " + (null == error.networkResponse ? "N/A" : "" + error.networkResponse.statusCode));
                    Log.e(TAG, "error message : " + error.getLocalizedMessage());
                    error.printStackTrace();
                }
                if (null != listener) {
                    listener.onErrorResponse(error);
                } else {
                    Log.w(TAG, "there is no onErrorResponse listener.");
                }
                Log.w(TAG, "*******************************************************");
            }
        };
    }
}