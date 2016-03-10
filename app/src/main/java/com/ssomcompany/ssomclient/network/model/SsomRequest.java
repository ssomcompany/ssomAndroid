package com.ssomcompany.ssomclient.network.model;

import com.ssomcompany.ssomclient.network.NetworkConstant;
import com.ssomcompany.ssomclient.network.NetworkUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class SsomRequest extends BaseRequest {

    private static Pattern queryArrayPramPattern = Pattern.compile("^[\t ]*\\[(.*)\\][\t ]*$");

    transient protected String host;

    public String getHost() {
        return NetworkUtil.getSsomHostUrl();
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> header = super.getHeaders();

        if (null == header) {
            header = new HashMap<String, String>();
        }
        header.put(NetworkConstant.HeaderParam.ACCEPT, "application/json");
        header.put(NetworkConstant.HeaderParam.CONTENT_TYPE, "application/json; charset=UTF-8");

        return header;
    }
}
