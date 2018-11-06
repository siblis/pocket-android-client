package com.gb.pocketmessenger.Network;

import android.util.Log;

import com.gb.pocketmessenger.DataBase.PocketDao;
import com.gb.pocketmessenger.models.User;
import com.gb.pocketmessenger.utils.JsonParser;

import java.util.concurrent.ExecutionException;

import static com.gb.pocketmessenger.Constants.CURRENT_SERVER;

public class RestUtils {

    private static final String TAG = "tar";

    public static String login(User user, PocketDao pocketDao) {
        String result = "";
        ConnectionToServer connection = new ConnectionToServer("LOGIN", user, pocketDao);
        connection.execute(CURRENT_SERVER);
        try {
            result = connection.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return JsonParser.parseToken(result);
    }

    public static User getUserInfo(User newUser, PocketDao pocketDao) {
        String userID = " ";
        ConnectionToServer connection = new ConnectionToServer("GET_ID", newUser, pocketDao);
        connection.execute(CURRENT_SERVER);
        try {
            userID = connection.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return JsonParser.parseUser(userID);
    }

    public static String sendRegisterData(String login, String email, String password, PocketDao pocketDao) {
        User newUser = new User(login, password);
        String result = "";
        newUser.seteMail(email);
        ConnectionToServer connection = new ConnectionToServer("REGISTER", newUser, pocketDao);
        connection.execute(CURRENT_SERVER);
        try {
            result = connection.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String addContact(String email, PocketDao pocketDao) {
        User newUser = new User(email);
        String result = "";
        ConnectionToServer connection = new ConnectionToServer("ADD_CONTACT", newUser, pocketDao);
        connection.execute(CURRENT_SERVER);
        try {
            result = connection.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "addContact to Server result: " + result);
        return result;
    }

    public static String getContactList(PocketDao pocketDao) {
        User newUser = new User();
        String result = "";
        ConnectionToServer connection = new ConnectionToServer("GET_CONTACTS", newUser, pocketDao);
        connection.execute(CURRENT_SERVER);
        try {
            result = connection.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getUserById(String id, PocketDao pocketDao){
        String result = "";
        User user = new User();
        user.id = id;
        ConnectionToServer connection = new ConnectionToServer("GET_USER_BY_ID", user, pocketDao);
        connection.execute(CURRENT_SERVER);
        try {
            result = connection.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }
}
