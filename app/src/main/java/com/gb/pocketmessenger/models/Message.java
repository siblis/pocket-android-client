package com.gb.pocketmessenger.models;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

public class Message implements IMessage {
    public String text;
    public String Id;
    public Date CreatedAt;
    public User user;
    public String receiver;

    public Message() {
    }

    public Message(String text) {
        this.text = text;
        this.Id = Integer.toString((int)Math.random()*1000000000);  //TODO: сделать генерацию ID нормально
        this.CreatedAt = new Date();
        this.user = new User();
    }

    public Message(String text, String id, Date createdAt, User user, String receiver) {
        this.text = text;
        this.Id = id;
        this.CreatedAt = createdAt;
        this.user = user;
        this.receiver = receiver;
    }

    @Override
    public String getId() {
        return Id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Date getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.CreatedAt = createdAt;
    }
}
