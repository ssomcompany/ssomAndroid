package com.ssomcompany.ssomclient.network.model;

import com.google.gson.annotations.SerializedName;

public class ChatUnreadCountResult {
    @SerializedName("unreadCount")
    private int unreadCount;

    public int getUnreadCount() {
        return unreadCount;
    }
}
