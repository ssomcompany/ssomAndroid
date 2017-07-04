package com.ssomcompany.ssomclient.network.model;

import com.google.gson.annotations.SerializedName;

public class VersionResult {
    @SerializedName("version")
    private String version;

    public String getVersion() {
        return version;
    }
}
