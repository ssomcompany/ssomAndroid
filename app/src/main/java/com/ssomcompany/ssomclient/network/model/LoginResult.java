package com.ssomcompany.ssomclient.network.model;

import com.google.gson.annotations.SerializedName;

public class LoginResult {
    @SerializedName("token")
    private String token;
    @SerializedName("userId")
    private String userId;
    @SerializedName("profileImgUrl")
    private String profileImgUrl;
    @SerializedName("hearts")
    private int hearts;

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public int getHearts() {
        return hearts;
    }
}
