package com.ssomcompany.ssomclient.network.api.model;

public class ChattingItem extends SsomItem {
    enum MessageType {
        initial,  // 최초 표기 메시지
        send,   // 보낸 메시지
        receive   // 받은 메시지
    }

    private String message;
    private long messageTime;
    private MessageType type;

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

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
