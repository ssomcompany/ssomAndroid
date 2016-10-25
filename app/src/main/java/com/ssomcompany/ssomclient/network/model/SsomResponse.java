
package com.ssomcompany.ssomclient.network.model;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Locale;

public class SsomResponse<T> extends BaseResponse {
    private String message;
    private int resultCode;
    private T data;

    public String getMessage() {
        if (null != getError()) {
            return "[" + getError().getClass().getSimpleName() + "] " + getError().getMessage();
        } else if (null != message) {
            return message;
        }

        return null;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public boolean isSuccess() {
        return null == getError() && isSuccessResultCode(getStatusCode());
    }

    private boolean isSuccessResultCode(int statusCode) {
        if (0 <= statusCode && 1000 >= statusCode) {
            resultCode = statusCode * 100;

            if (statusCode == 200 /* HttpStatus.SC_OK */) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {

        String result = null;

        result = String.format(Locale.getDefault(), "[statusCode : %d, resultCode : %d, message : %s, data : %s]",
                getStatusCode(), getResultCode(), getMessage(), (null != getData() ? getData().toString() : "none"));

        return result;
    }

    public static <T> Type getType(T t) {
        return new TypeToken<SsomResponse<T>>() {
        }.getType();
    }
}
