package com.gb.pocketmessenger.utils;

import com.gb.pocketmessenger.models.Token;
import com.gb.pocketmessenger.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonParser {

    public static String parseToken(String tokenJson) {
        Gson gson = new GsonBuilder().create();
        Token token = gson.fromJson(tokenJson, Token.class);
        return token.getToken();
    }

    public static User parseUser(String userInfo){
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(userInfo, User.class);
    }
}
