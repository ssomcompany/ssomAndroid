package com.ssomcompany.ssomclient.network.model;

import com.google.gson.annotations.SerializedName;

public class ChatRoomCreateResult {
    @SerializedName("chatroomId")
    private String chatroomId;
    @SerializedName("createdTimestamp")
    private long createdTimestamp;

    public String getChatroomId() {
        return chatroomId;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }
}
