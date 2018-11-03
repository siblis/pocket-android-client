package com.gb.pocketmessenger.models;

import com.google.gson.annotations.SerializedName;

public class IncomingMessage {

    @SerializedName("message")
    private String message;
    @SerializedName("receiver")
    private String receiver;
    @SerializedName("sender_name")
    private String senderName;
    @SerializedName("senderid")
    private String senderid;
    @SerializedName("timestamp")
    private String timeStamp;
    @SerializedName("response")
    private String serverResponse;

    public IncomingMessage(String message, String receiver, String sender, String senderid, String timeStamp, String serverResponse) {
        this.message = message;
        this.receiver = receiver;
        this.senderName = sender;
        this.senderid = senderid;
        this.timeStamp = timeStamp;
        this.serverResponse = serverResponse;
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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getServerResponse() {
        return serverResponse;
    }

    public void setServerResponse(String serverResponse) {
        this.serverResponse = serverResponse;
    }
}
