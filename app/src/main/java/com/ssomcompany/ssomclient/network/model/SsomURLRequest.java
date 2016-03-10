package com.ssomcompany.ssomclient.network.model;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ssomcompany.ssomclient.BaseApplication;

import java.util.HashMap;
import java.util.Map;

public abstract class SsomURLRequest extends SsomRequest {

    transient protected String url;

    @SuppressWarnings("unused")
    private SsomURLRequest() {
        this.url = null;
    }

    public SsomURLRequest(String url) {
        super();
        this.url = url;
    }

    @Override
    public String getURL() {

        String queryString = "";
        try {
            Gson gson = new Gson();
            String json = gson.toJson(this);
            Map<String, String> retMap = gson.fromJson(json, new TypeToken<HashMap<String, String>>() {
            }.getType());

            Uri.Builder builder = new Uri.Builder();
            for (String key : retMap.keySet()) {
                builder.appendQueryParameter(key, retMap.get(key));
            }
            queryString = builder.build().toString();
        } catch (Exception e) {
            Log.w(BaseApplication.getInstance().TAG, "parsing error", e);
        }

        return getHost() + this.url + queryString;
    }

    @Override
    public String getPayload() {
        return null;
    }
}
