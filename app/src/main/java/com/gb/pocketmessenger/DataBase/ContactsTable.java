package com.gb.pocketmessenger.DataBase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ContactsTable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "name")
    private String mUserName;

    @ColumnInfo(name = "email")
    private String mEmail;

    @ColumnInfo(name = "type")
    private boolean mType;

    public ContactsTable() {
    }

    public ContactsTable(int id, String userName, String email, boolean type) {
        mId = id;
        mUserName = userName;
        mEmail = email;
        mType = type;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public boolean isType() {
        return mType;
    }

    public void setType(boolean type) {
        mType = type;
    }
}
