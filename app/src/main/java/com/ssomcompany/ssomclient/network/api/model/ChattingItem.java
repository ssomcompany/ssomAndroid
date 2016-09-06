package com.ssomcompany.ssomclient.network.api.model;

public class ChattingItem {
    public enum MessageType {
        initial,  // 최초 표기 메시지
        finish   // 최종 표기 메시지
    }

    private String msgId;
    private String msg;
    private String toUserId;
    private String fromUserId;
    private long timestamp;
    private String msgType;
    private MessageType type;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MessageType getType() {
        return type;
    }

    public ChattingItem setType(MessageType type) {
        this.type = type;
        return this;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}
