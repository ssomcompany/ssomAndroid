package com.ssomcompany.ssomclient.network.model;

import com.google.gson.annotations.SerializedName;

public class HeartResult {
    @SerializedName("heartsCount")
    private int heartsCount;

    public int getHeartsCount() {
        return heartsCount;
    }

    public void setHeartsCount(int heartsCount) {
        this.heartsCount = heartsCount;
    }
}
