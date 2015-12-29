package com.ssomcompany.ssomclient.network;

/**
 * Created by AaronMac on 2015. 12. 20..
 */
public class NetworkManager {
    private static NetworkManager mInstance;
    private static String host = "";

    public enum TYPE{
        POST,
        IMAGE;
    };

    private NetworkManager() {
        host = new StringBuffer(NetworkConstant.isHttps?
                NetworkConstant.HTTPS.concat(NetworkConstant.HOST):NetworkConstant.HTTP.concat(NetworkConstant.HOST)).toString();
    }

    public static synchronized NetworkManager getInstance() {
        if(mInstance == null) {
            mInstance = new NetworkManager();
        }

        return mInstance;
    }

    /**
     * Author : Aaron Choi
     * Desc : when you need to call network url, use this method with enum TYPE
     */
    public String getNetworkUrl(TYPE type) {
        String url = "";

        switch (type) {
            case POST:
                url = host.concat(NetworkConstant.API_POST);
                break;
            case IMAGE:
                url = host.concat(NetworkConstant.IMAGE_PATH);
                break;
            default:
                url = host;
        }

        return url;
    }
}
