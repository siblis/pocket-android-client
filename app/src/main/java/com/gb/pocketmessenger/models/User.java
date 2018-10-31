package com.gb.pocketmessenger.models;


import com.google.gson.annotations.SerializedName;
import com.stfalcon.chatkit.commons.models.IUser;

public class User implements IUser {

    @SerializedName("account_name")
    private String login;

    private String password;

    @SerializedName("email")
    private String eMail;
    private String token;

    @SerializedName("uid")
    public String id;

    public User() {
    }

    public User(String login, String password, String id) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public User(String email) {
        this.eMail = email;
    }

    public User(String login, String password, String eMail, String token, String id) {
        this.login = login;
        this.password = password;
        this.eMail = eMail;
        this.token = token;
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return login;
    }

    @Override
    public String getAvatar() {
        return null;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
