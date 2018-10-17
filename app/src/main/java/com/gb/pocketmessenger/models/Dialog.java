package com.gb.pocketmessenger.models;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.List;

public class Dialog implements IDialog {

    public String Id;
    public String DialogName;
    public List<User> users;

    public Dialog(String id, String dialogName, List<User> users) {
        Id = id;
        DialogName = dialogName;
        this.users = users;
    }


    @Override
    public String getId() {
        return Id;
    }

    @Override
    public String getDialogPhoto() {
        return null;
    }

    @Override
    public String getDialogName() {
        return DialogName;
    }

    @Override
    public List<? extends IUser> getUsers() {
        return users;
    }

    @Override
    public IMessage getLastMessage() {
        return null;
    }

    @Override
    public void setLastMessage(IMessage message) {

    }

    @Override
    public int getUnreadCount() {
        return 0;
    }
}

