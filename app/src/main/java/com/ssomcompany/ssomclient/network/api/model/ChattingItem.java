package com.ssomcompany.ssomclient.network.api.model;

import java.io.Serializable;

public class ChattingItem extends SsomItem implements Serializable {
    private String message;
    private long messageTime;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
