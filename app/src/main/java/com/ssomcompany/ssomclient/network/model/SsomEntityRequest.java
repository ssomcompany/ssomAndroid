package com.ssomcompany.ssomclient.network.model;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ssomcompany.ssomclient.BaseApplication;
import com.ssomcompany.ssomclient.network.NetworkConstant;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SsomEntityRequest extends SsomRequest {

    final transient protected String url;

    @SuppressWarnings("unused")
    private SsomEntityRequest() {
        this.url = null;
    }

    public SsomEntityRequest(String url) {
        super();
        this.url = url;
    }

    @Override
    public String getURL() {
        return getHost() + this.url;
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> header = super.getHeaders();
        if (isFormParam()) {
            header.put(NetworkConstant.HeaderParam.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
        }
        return header;
    }

    @Override
    public String getPayload() {
        if (false == isFormParam()) {
            String jsonPayload = null;
            try {
                Gson gson = new Gson();
                jsonPayload = gson.toJson(this);
                if (null != jsonPayload && 0 == new JSONObject(jsonPayload).length()) {
                    jsonPayload = null;
                }
            } catch (Exception e) {
                Log.w(BaseApplication.getInstance().TAG, "parsing error", e);
                jsonPayload = null;
            }

            return jsonPayload;
        } else {
            String queryString = null;
            try {
                Gson gson = new Gson();
                String json = gson.toJson(this);
                Map<String, Object> retMap = gson.fromJson(json, new TypeToken<HashMap<String, Object>>() {
                }.getType());

                Uri.Builder builder = new Uri.Builder();
                for (String key : retMap.keySet()) {
                    Object obj = retMap.get(key);

                    if (obj != null) {
                        obj = handleGSonLongTypeConversionBug(obj);

                        StringBuffer value = new StringBuffer();
                        if (obj instanceof List<?>) {
                            List<?> list = ((List<?>) obj);
                            for (int i = 0; i < list.size(); i++) {
                                value.append(i != 0 ? "," : "");
                                value.append(handleGSonLongTypeConversionBug(list.get(i)));
                            }
                        } else {
                            value.append(obj.toString());
                        }
                        if (!TextUtils.isEmpty(value.toString())) {
                            builder.appendQueryParameter(key, value.toString());
                        }
                    }
                }
                queryString = builder.build().toString();
                // remove ? character!!
                if (!TextUtils.isEmpty(queryString)) {
                    queryString = queryString.substring(1);
                }

            } catch (Exception e) {
                Log.w(BaseApplication.getInstance().TAG, "parsing error", e);
            }

            return queryString;
        }
    }

    /**
     * GSon has problems when deserializing Long type as Object. it always deserialized as Double type. this is the logic of correcting it
     */
    private Object handleGSonLongTypeConversionBug(Object value) {
        if (null != value) {
            if (value instanceof Double) {
                Double dbl = (Double) value;
                if (dbl == dbl.longValue()) {
                    return dbl.longValue();
                }
            }
        }

        return value;
    }

    /**
     * If the parameters are passed to the body in the type of "application/x-www-form-urlencoded", then override this function and return "true".
     * otherwise the parameters will be passed to the body in the type of "application/json"
     */
    protected boolean isFormParam() {
        return false;
    }
}
