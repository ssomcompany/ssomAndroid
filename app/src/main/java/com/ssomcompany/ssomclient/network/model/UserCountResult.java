package com.ssomcompany.ssomclient.network.model;

import com.google.gson.annotations.SerializedName;

public class UserCountResult {
    @SerializedName("userCount")
    private int userCount;

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }
}
