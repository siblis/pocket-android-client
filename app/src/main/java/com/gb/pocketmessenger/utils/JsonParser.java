package com.gb.pocketmessenger.utils;


import com.gb.pocketmessenger.DataBase.ContactsTable;
import com.gb.pocketmessenger.models.IncomingMessage;
import com.gb.pocketmessenger.models.Message;
import com.gb.pocketmessenger.models.PocketContact;
import com.gb.pocketmessenger.models.PocketMessage;
import com.gb.pocketmessenger.models.Token;
import com.gb.pocketmessenger.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class JsonParser {

    public static String parseToken(String tokenJson) {
        Gson gson = new GsonBuilder().create();
        if (!tokenJson.equals("ОШИБКА ЛОГИНА")) {
            Token token = gson.fromJson(tokenJson, Token.class);
            return token.getToken();
        } else return null;
    }

    public static User parseUser(String userInfo) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(userInfo, User.class);
    }

    public static String getWssMessage(Message message) {
        PocketMessage simpleMessage = new PocketMessage(message.receiver, message.text);
        Gson gson = new GsonBuilder().create();
        return gson.toJson(simpleMessage, PocketMessage.class);
    }

    public static IncomingMessage getIncomingMessage(String jsonMessage) {
        Gson gson = new GsonBuilder().create();
        IncomingMessage msg = gson.fromJson(jsonMessage, IncomingMessage.class);
        return msg;
    }

    public static List<PocketContact> parseUsersMap(String usersJson) {
        List<PocketContact> contactList = new ArrayList<>();
        Type itemsMapType = new TypeToken<Map<String, MyPair>>() {
        }.getType();
        Map<String, MyPair> usersMap = new Gson().fromJson(usersJson, itemsMapType);
        for (String key : usersMap.keySet()) {
            contactList.add(new PocketContact(usersMap.get(key).id, key, usersMap.get(key).name));
        }
        if (contactList.isEmpty()) return null;
        return contactList;
    }

    public static List<ContactsTable> parseContacts(String usersJson) {
        List<ContactsTable> contactList = new ArrayList<>();
        Type itemsMapType = new TypeToken<Map<String, MyPair>>() {
        }.getType();
        Map<String, MyPair> usersMap = new Gson().fromJson(usersJson, itemsMapType);
        for (String key : usersMap.keySet()) {
            contactList.add(new ContactsTable(Integer.valueOf(usersMap.get(key).id), usersMap.get(key).name, key, false));
        }
        if (contactList.isEmpty()) return null;
        return contactList;
    }

    public class MyPair {
        public String id;
        public String name;

        public MyPair(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
