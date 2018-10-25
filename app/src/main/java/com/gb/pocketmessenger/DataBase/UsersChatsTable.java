package com.gb.pocketmessenger.DataBase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = ContactsTable.class, parentColumns = "id",childColumns = "user_id"),
        @ForeignKey(entity = ChatsTable.class, parentColumns = "id",childColumns = "chat_id")})
public class UsersChatsTable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "user_id")
    private int mUserId;

    @ColumnInfo(name = "chat_id")
    private int ChatId;

    @ColumnInfo(name = "join_date")
    private String mJoinDate;

    public UsersChatsTable() {
    }

    public UsersChatsTable(int id, int userId, int chatId, String joinDate) {
        mId = id;
        mUserId = userId;
        ChatId = chatId;
        mJoinDate = joinDate;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public int getChatId() {
        return ChatId;
    }

    public void setChatId(int chatId) {
        ChatId = chatId;
    }

    public String getJoinDate() {
        return mJoinDate;
    }

    public void setJoinDate(String joinDate) {
        mJoinDate = joinDate;
    }
}
