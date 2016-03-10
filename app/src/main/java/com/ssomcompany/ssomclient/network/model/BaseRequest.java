
package com.ssomcompany.ssomclient.network.model;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic interface definition for handling requests. Implemented the function related to request header.
 *
 * @author minsung.ku
 */
public abstract class BaseRequest {

    /** The default socket timeout in milliseconds */
    transient public static final int DEFAULT_TIMEOUT_MS = 15000;

    /** The default number of retries */
    transient public static final int DEFAULT_MAX_RETRIES = 1;

    /** The default backoff multiplier */
    transient public static final float DEFAULT_BACKOFF_MULT = 1f;

    transient protected Map<String, String> header = null;
    transient protected RetryPolicy retryPolicy = new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT);

    /**
     * @return HTTP method
     *
     *         <pre>
     * NetworkConstant.Method.GET
     * NetworkConstant.Method.POST
     * NetworkConstant.Method.PUT
     * NetworkConstant.Method.DELETE
     *         </pre>
     */
    public abstract int getMethod();

    public abstract String getURL();

    public abstract String getPayload();

    public Map<String, String> getHeaders() {
        return header;
    }

    /**
     * Maps the specified key to the specified value.
     *
     * @param key the key
     * @param value the value
     * @return the value of any previous mapping with the specified key or null if there was no mapping.
     */
    public String putHeader(String key, String value) {
        if (null == header) {
            header = new HashMap<String, String>();
        }
        return header.put(key, value);
    }

    /**
     * Gets the retry policy for this request.
     */
    public RetryPolicy getRetryPolicy() {
        return this.retryPolicy;
    }

    /**
     * Sets the retry policy for this request.
     */
    public void setRetryPolicy(RetryPolicy retryPolicy) {
        if (null != retryPolicy) {
            this.retryPolicy = retryPolicy;
        }
    }

    /**
     * Sets The default socket timeout in milliseconds
     *
     * @param timout The default socket timeout in milliseconds
     */
    public void setTimeoutMillis(int timout) {
        setRetryPolicy(new DefaultRetryPolicy(timout, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
    }
}
