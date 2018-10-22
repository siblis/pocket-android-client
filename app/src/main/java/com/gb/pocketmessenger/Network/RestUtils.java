package com.gb.pocketmessenger.Network;

import android.util.Log;

import com.gb.pocketmessenger.DataBase.PocketDao;
import com.gb.pocketmessenger.models.User;
import com.gb.pocketmessenger.utils.JsonParser;

import java.util.concurrent.ExecutionException;

import static com.gb.pocketmessenger.Constants.CURRENT_SERVER;

public class RestUtils {

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
}
