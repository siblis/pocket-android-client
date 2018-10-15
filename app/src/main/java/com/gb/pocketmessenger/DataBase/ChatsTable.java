package com.gb.pocketmessenger.DataBase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ChatsTable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "chat_name")
    private String mChatName;

    @ColumnInfo(name = "creation_date")
    private String mCreationDate;

    public ChatsTable() {
    }

    public ChatsTable(int id, String chatName, String creationDate) {
        mId = id;
        mChatName = chatName;
        mCreationDate = creationDate;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getChatName() {
        return mChatName;
    }

    public void setChatName(String chatName) {
        mChatName = chatName;
    }

    public String getCreationDate() {
        return mCreationDate;
    }

    public void setCreationDate(String creationDate) {
        mCreationDate = creationDate;
    }
}
