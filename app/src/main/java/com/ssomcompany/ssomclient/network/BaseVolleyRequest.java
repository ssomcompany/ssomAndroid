package com.ssomcompany.ssomclient.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.HashMap;
import java.util.Map;


public class BaseVolleyRequest extends Request<NetworkResponse> {

    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;

    private String token;

    public BaseVolleyRequest(String url, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        super(0, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    public BaseVolleyRequest(String token, int method, String url, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        super(method, NetworkUtil.getSsomHostUrl() + url, errorListener);
        this.token = token;
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    public String getToken() {
        return token;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(
                    response,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return super.parseNetworkError(volleyError);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }
}