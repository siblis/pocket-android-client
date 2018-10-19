package com.gb.pocketmessenger.models;

import com.google.gson.annotations.SerializedName;

public class IncomingMessage {

    @SerializedName("message")
    private String message;
    @SerializedName("receiver")
    private String receiver;
    @SerializedName("sender")
    private String sender;
    @SerializedName("senderid")
    private String senderid;

    public IncomingMessage(String message, String receiver, String sender, String senderid) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.senderid = senderid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }
}
