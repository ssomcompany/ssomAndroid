package com.ssomcompany.ssomclient.network.api.model;

public class ChattingItem {
    public enum MessageType {
        message,   // 일반 메시지
        initial,  // 최초 표기 메시지
        request,  // 만남 요청 한 상태
        approve,  // 만남 수락 한 상태
        cancel,   // 만남 요청이 취소된 상태
        finish   // 최종 표기 메시지
    }

    private String msgId;
    private String msg;
    private String toUserId;
    private String fromUserId;
    private long timestamp;
    private String msgType;
    private MessageType status;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MessageType getStatus() {
        return status;
    }

    public ChattingItem setStatus(MessageType status) {
        this.status = status;
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

    public ChattingItem setMsgType(String msgType) {
        this.msgType = msgType;
        return this;
    }
}
