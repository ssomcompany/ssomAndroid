package com.ssomcompany.ssomclient.network.api.model;

public class ChattingItem {
    public enum MessageType {
        initial,  // 최초 표기 메시지
        finish,   // 최종 표기 메시지
        send,   // 보낸 메시지
        receive   // 받은 메시지
    }

    private String postId;
    private String message;
    private long messageTime;
    private MessageType type;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

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

    public ChattingItem setType(MessageType type) {
        this.type = type;
        return this;
    }
}
