
package com.ssomcompany.ssomclient.network.model;

import android.util.Log;

import java.lang.reflect.Type;

import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.network.NetworkManager;
import com.ssomcompany.ssomclient.network.NetworkManager.NetworkListener;

/**
 * Parsed data from response of server.
 *
 * @author minsung.ku
 */
public abstract class BaseResponse {

    /**
     * Exception object
     */
    transient private Exception mError;

    /**
     * The HTTP status code
     */
    transient private int mStatusCode;

    transient private BaseRequest request;
    transient private Type resType;
    transient private NetworkListener<? extends BaseResponse> listener;

    /**
     * @return the HTTP status code
     */
    public int getStatusCode() {
        return mStatusCode;
    }

    /**
     * Sets the HTTP status code.
     */
    public void setStatusCode(int statusCode) {
        this.mStatusCode = statusCode;
    }

    /**
     * @return The exception object if exception occurred during communication with server.
     */
    public Exception getError() {
        return mError;
    }

    /**
     * @return returns true if error occurred due to offline network. otherwise returns false
     */
    public boolean isOfflineError() {
        return null != getError() && getError() instanceof NetworkOffLineException;
    }

    public boolean retry() {
        if (isOfflineError() && null != request && null != resType) {
            Log.i(BaseApplication.getInstance().TAG, "retry last request : " + this.request);
            NetworkManager.request(this.request, this.resType, this.listener);
            return true;
        } else {
            Log.i(BaseApplication.getInstance().TAG, "cannot retry. ignore action!!" + this.request);
        }
        return false;
    }

    /**
     * save last request for retrying
     */
    public void saveLastRequest(BaseRequest request, final Type resType, final NetworkListener<? extends BaseResponse> listener) {
        this.request = request;
        this.resType = resType;
        this.listener = listener;
    }

    /**
     * Sets the exception object if exception occurred during communication with server.
     */
    public void setError(Exception error) {
        this.mError = error;
    }

    /**
     * @return whether this response is considered successful.
     */
    public boolean isSuccess() {
        return null == getError();
    }

    @Override
    public String toString() {

        String result;

        if (isSuccess()) {
            result = String.format("[statusCode : %d]", getStatusCode());
        } else {
            result = String.format("[statusCode : %d, message : %s]", getStatusCode(),
                    "[" + getError().getClass().getSimpleName() + "] " + getError().getMessage());
        }

        return result;
    }

    @SuppressWarnings("serial")
    public static class NetworkOffLineException extends Exception {

        public NetworkOffLineException() {
            super("Network is offline");
        }
    }
}
