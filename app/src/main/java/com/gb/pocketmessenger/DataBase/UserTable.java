package com.gb.pocketmessenger.DataBase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class UserTable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "name")
    private String mUserName;

    @ColumnInfo(name = "pass")
    private String mPassword;

    @ColumnInfo(name = "email")
    private String mEmail;

    @ColumnInfo(name = "token")
    private String mToken;

    public UserTable() {
    }

    public UserTable(int id, String userName, String password, String email, String token) {
        mId = id;
        mUserName = userName;
        mPassword = password;
        mEmail = email;
        mToken = token;
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

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }
}
