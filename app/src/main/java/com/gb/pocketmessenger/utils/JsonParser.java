package com.gb.pocketmessenger.utils;


import com.gb.pocketmessenger.models.IncomingMessage;
import com.gb.pocketmessenger.models.Message;
import com.gb.pocketmessenger.models.PocketMessage;
import com.gb.pocketmessenger.models.Token;
import com.gb.pocketmessenger.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class JsonParser {

    public static String parseToken(String tokenJson) {
        Gson gson = new GsonBuilder().create();
        if (!tokenJson.equals("ОШИБКА ЛОГИНА")) {
        Token token = gson.fromJson(tokenJson, Token.class);
        return token.getToken();
        } else return null;
    }

    public static User parseUser(String userInfo){
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(userInfo, User.class);
    }

    public static String getWssMessage(Message message){
        PocketMessage simpleMessage = new PocketMessage(message.receiver, message.text);
        Gson gson = new GsonBuilder().create();
        return gson.toJson(simpleMessage, PocketMessage.class);
    }

    public static IncomingMessage getIncomingMessage(String jsonMessage){
        Gson gson = new GsonBuilder().create();
        IncomingMessage msg = gson.fromJson(jsonMessage, IncomingMessage.class);
        return msg;
    }

}
