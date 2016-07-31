
package com.ssomcompany.ssomclient.network;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ssomcompany.ssomclient.network.model.BaseResponse;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class GsonRequest<T extends BaseResponse> extends Request<T> {
    private final Gson gson;
    private final Type type;
    private final Map<String, String> headers;
    private final Listener<T> listener;
    private final String mRequestBody;

    {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(String.class, new EscapeJsonStringSerializer());
        gson = builder.create();
    }

    /**
     * Default charset for JSON request.
     */
    protected static final String PROTOCOL_CHARSET = "utf-8";

    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param resType Relevant class object type, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(String url, Type resType, Map<String, String> headers,
                       Listener<T> listener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.type = resType;
        this.headers = headers;
        this.listener = listener;
        this.mRequestBody = null;
    }

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param method the HTTP method to use
     * @param url URL of the request to make
     * @param payload request payload
     * @param resType Relevant response class object type, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(int method, String url, Type resType, Map<String, String> headers, String payload,
                       Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);

        this.type = resType;
        this.headers = headers;
        this.listener = listener;
        this.mRequestBody = payload;
    }

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param method the HTTP method to use
     * @param url URL of the request to make
     * @param payload request payload
     * @param resType Relevant response class object type, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(int method, String url, Type resType, Map<String, String> headers, String payload,
                       Listener<T> listener, ErrorListener errorListener, RetryPolicy retryPolicy) {
        super(method, url, errorListener);

        this.type = resType;
        this.headers = headers;
        this.listener = listener;
        this.mRequestBody = payload;

        if (null != retryPolicy) {
            setRetryPolicy(retryPolicy);
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    public String getBodyContentType() {
        if (null != headers && headers.containsKey(HTTP.CONTENT_TYPE)) {
            return null;
        } else {
            return PROTOCOL_CONTENT_TYPE;
        }
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }

    private static class EscapeJsonStringSerializer implements JsonSerializer<String> {

        public static String escapeJsonString(String string) {
            String escapes[][] = new String[][] {
                    {
                            "\n", "\\n"
                    },
                    {
                            "\\", "\\\\"
                    },
                    {
                            "\"", "\\\""
                    },
                    {
                            "\r", "\\r"
                    },
                    {
                            "\b", "\\b"
                    },
                    {
                            "\f", "\\f"
                    },
                    {
                            "\t", "\\t"
                    }
            };
            for (String[] esc : escapes) {
                string = string.replace(esc[0], esc[1]);
            }
            return string;
        }

        @Override
        public JsonElement serialize(String paramT, Type paramType, JsonSerializationContext paramJsonSerializationContext) {
            return new JsonPrimitive(escapeJsonString(paramT));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String json = null;
        T obj = null;
        try {
            if (null == response.data || 0 == response.data.length) {
                Log.w("GsonRequest", "response data size is 0!! check the API");
                TypeToken<T> typeToken = (TypeToken<T>) TypeToken.get(type);
                try {
                    obj = (T) typeToken.getRawType().newInstance();
                } catch (Exception e) {
                    Log.e("GsonRequest", "couldn't instantiation response");
                }
            } else {
                json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                Log.i("GsonRequest", "raw header : " + response.headers);
                Log.i("GsonRequest", "raw data : " + json);

                if(json.startsWith("[")) json = "{\"data\":{\"list\":" + json + "}}";
                else if(json.startsWith("{")) json = "{\"data\":" + json + "}";

                Log.i("GsonRequest", "raw data : " + json);
                obj = gson.fromJson(json, type);
            }
            if (null != obj) {
                obj.setStatusCode(response.statusCode);
            }
            return Response.success(obj, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            Log.e("GsonRequest", "origin message : " + json);
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            Log.e("GsonRequest", "origin message : " + json);
            return Response.error(new ParseError(e));
        }
    }
}
