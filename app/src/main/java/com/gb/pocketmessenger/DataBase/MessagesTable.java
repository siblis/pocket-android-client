package com.gb.pocketmessenger.DataBase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = ContactsTable.class, parentColumns = "id", childColumns = "from_id")})
public class MessagesTable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "from_id")
    private int mFromId;

    @ColumnInfo(name = "to_id")
    private int mToId;

    @ColumnInfo(name = "message")
    private String mMessage;

    @ColumnInfo(name = "date")
    private String mDate;

    @ColumnInfo(name = "status")
    private int mStatus;

    public MessagesTable() {
    }

    public MessagesTable(int id, int fromId, int toId, String message, String date, int status) {
        mId = id;
        mFromId = fromId;
        mToId = toId;
        mMessage = message;
        mDate = date;
        mStatus = status;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getFromId() {
        return mFromId;
    }

    public void setFromId(int fromId) {
        mFromId = fromId;
    }

    public int getToId() {
        return mToId;
    }

    public void setToId(int toId) {
        mToId = toId;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

}