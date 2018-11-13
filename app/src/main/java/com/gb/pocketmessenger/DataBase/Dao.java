package com.gb.pocketmessenger.DataBase;

import android.util.Log;

import com.gb.pocketmessenger.Network.RestUtils;
import com.gb.pocketmessenger.models.IncomingMessage;
import com.gb.pocketmessenger.models.Message;
import com.gb.pocketmessenger.models.User;
import com.gb.pocketmessenger.utils.JsonParser;

import java.util.Date;

public class Dao {
    private static PocketDao mPocketDao;
    private static final String TAG = "tar";
    private static IncomingMessage mMessage;

    public Dao() {
    }

    public Dao(PocketDao pocketDao) {
        mPocketDao = pocketDao;
    }

    public static String incommingMessage(PocketDao pocketDao, IncomingMessage message) {
        mPocketDao = pocketDao;
        mMessage = message;
        String chatName = null;
        for (int k = 0; k < mPocketDao.getChats().size(); k++) {
            if (mPocketDao.getChats().get(k).getChatName().equals(mMessage.getSenderName())) {
                chatName = mPocketDao.getChats().get(k).getChatName();
                Log.d(TAG, "Found Chat with name: " + mPocketDao.getChats().get(k).getChatName());
                mPocketDao.insertMessage(new MessagesTable(mPocketDao.getMessages().size(),
                        Integer.valueOf(mMessage.getSenderid()),
                        mPocketDao.getUser().getServerUserId(),
                        mMessage.getMessage(),
                        String.valueOf(new Date()),
                        mPocketDao.getChats().get(k).getId(), 0));
            }
        }

        if (chatName == null) {

            Log.d(TAG, "New User!");
            mPocketDao.insertChat(new ChatsTable(mPocketDao.getChats().size(), mMessage.getSenderName(), String.valueOf(new Date())));
            mPocketDao.setOneLinkUserToChat(new UsersChatsTable(mPocketDao.getLinks().size(), mPocketDao.getUser().getServerUserId(), (mPocketDao.getChats().size() - 1), String.valueOf(new Date())));

            User newUser = JsonParser.parseUser(RestUtils.getUserById(mMessage.getSenderid(), mPocketDao));
            String email = newUser.geteMail();

            mPocketDao.insertContact(new ContactsTable(Integer.valueOf(mMessage.getSenderid()), mMessage.getSenderName(), email, false));

            mPocketDao.setOneLinkUserToChat(new UsersChatsTable(mPocketDao.getLinks().size(), Integer.valueOf(mMessage.getSenderid()), (mPocketDao.getChats().size() - 1), String.valueOf(new Date())));
            mPocketDao.insertMessage(new MessagesTable(mPocketDao.getMessages().size(),
                    Integer.valueOf(mMessage.getSenderid()),
                    mPocketDao.getUser().getServerUserId(),
                    mMessage.getMessage(),
                    String.valueOf(new Date()),
                    (mPocketDao.getChats().size() - 1), 0));

        }

        return chatName;
    }
}
