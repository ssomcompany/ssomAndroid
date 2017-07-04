package com.ssomcompany.ssomclient.network.model;

import com.google.gson.annotations.SerializedName;

public class ProfileResult {
    @SerializedName("profileImgUrl")
    private String profileImgUrl;
    @SerializedName("hearts")
    private int hearts;

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public int getHearts() {
        return hearts;
    }
}
