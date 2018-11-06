package com.gb.pocketmessenger.Network;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.gb.pocketmessenger.DataBase.ChatsTable;
import com.gb.pocketmessenger.DataBase.ContactsTable;
import com.gb.pocketmessenger.DataBase.MessagesTable;
import com.gb.pocketmessenger.DataBase.PocketDao;
import com.gb.pocketmessenger.DataBase.UsersChatsTable;
import com.gb.pocketmessenger.models.IncomingMessage;
import com.gb.pocketmessenger.models.User;
import com.gb.pocketmessenger.services.PocketMessengerWssService;
import com.gb.pocketmessenger.utils.JsonParser;

import java.util.Date;

import static com.gb.pocketmessenger.Constants.MESSAGE_BODY;
import static com.gb.pocketmessenger.Constants.WEBSOCKET_MESSAGE_TAG;
import static com.gb.pocketmessenger.services.PocketMessengerWssService.TOKEN_INTENT;

public class WssConnector {
    private static WssConnector connector;
    private static Intent intent;
    private static BroadcastReceiver messageReceiver;
    private boolean isServiceConnected;
    private static PocketMessengerWssService wssService;
    private static ServiceConnection serviceConnection;
    private Context context;
    private static OnIncomingMessage listener;
    private static OnWssConnected wssListener;
    private static OnUnknownContact newContactListener;
    private static PocketDao mPocketDao;
    private static final String TAG = "tar";

    public interface OnIncomingMessage {
        void onIncomingMessage(String receiverId, String incomingMessage);
    }

    public interface OnWssConnected {
        void onWssConnected();
    }

    public interface OnUnknownContact {
        void onUnknownContact();
    }

    private WssConnector(Context context, PocketDao mPocketDao) {
        this.context = context;
        WssConnector.mPocketDao = mPocketDao;
    }

    public static void initInstance(Context context, PocketDao mPocketDao) {
        if (connector == null) {
            receiverInit(context, mPocketDao);
            connector = new WssConnector(context, mPocketDao);
        }
    }

    public void setOnIncomingMessageListener(OnIncomingMessage listener) {
        WssConnector.listener = listener;
    }

    public void setOnUnknownContactListener(OnUnknownContact newContactListener) {
        WssConnector.newContactListener = newContactListener;
    }

    public void setOnWssConnectedListener(OnWssConnected wssListener) {
        WssConnector.wssListener = wssListener;
    }

    public static WssConnector getInstance() {
        return connector;
    }

    private static void receiverInit(Context context, PocketDao mPocketDao) {
        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String jsonMsg = intent.getStringExtra(MESSAGE_BODY);
                if (jsonMsg != null) {
                    IncomingMessage message = JsonParser.getIncomingMessage(jsonMsg);
                    if (message.getServerResponse() != null) {
                        if (message.getServerResponse().equals("200"))
                            Toast.makeText(context, "Сообщение отправлено", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "Ошибка отправки", Toast.LENGTH_SHORT).show();
                    } else {
                        if (listener != null)
                            listener.onIncomingMessage(message.getSenderid(), message.getMessage());

                        Toast.makeText(context, "Входящее сообщение от " + message.getSenderName(), Toast.LENGTH_SHORT).show();
                        String chatName = null;
                        Log.d(TAG, "Incomming message: FROM = " + message.getSenderName() + " ID=" + Integer.valueOf(message.getSenderid()) + " TO = " + mPocketDao.getUser().getServerUserId() + " TEXT = " +
                                message.getMessage());

                        //TODO Я бы всю эту работу с БД перенес в отдельный класс и коллбэком в этот класс передавал бы данные, которые нужны
                        //TODO так мы отделим базу данных от вебсокет-соединения

                        for (int k = 0; k < mPocketDao.getChats().size(); k++) {
                            if (mPocketDao.getChats().get(k).getChatName().equals(message.getSenderName())) {
                                chatName = mPocketDao.getChats().get(k).getChatName();
                                Log.d(TAG, "Found Chat with name: " + mPocketDao.getChats().get(k).getChatName());
                                mPocketDao.insertMessage(new MessagesTable(mPocketDao.getMessages().size(),
                                        Integer.valueOf(message.getSenderid()),
                                        mPocketDao.getUser().getServerUserId(),
                                        message.getMessage(),
                                        String.valueOf(new Date()),
                                        mPocketDao.getChats().get(k).getId(), 0));
                            }
                        }

                        if (chatName == null) {

                            Log.d(TAG, "New User!");
                            mPocketDao.insertChat(new ChatsTable(mPocketDao.getChats().size(), message.getSenderName(), String.valueOf(new Date())));
                            mPocketDao.setOneLinkUserToChat(new UsersChatsTable(mPocketDao.getLinks().size(), mPocketDao.getUser().getServerUserId(), (mPocketDao.getChats().size() - 1), String.valueOf(new Date())));

                            User newUser = JsonParser.parseUser(RestUtils.getUserById(message.getSenderid(), mPocketDao));
                            String email = newUser.geteMail();

                            mPocketDao.insertContact(new ContactsTable(Integer.valueOf(message.getSenderid()), message.getSenderName(), email, false));

                            mPocketDao.setOneLinkUserToChat(new UsersChatsTable(mPocketDao.getLinks().size(), Integer.valueOf(message.getSenderid()), (mPocketDao.getChats().size() - 1), String.valueOf(new Date())));
                            mPocketDao.insertMessage(new MessagesTable(mPocketDao.getMessages().size(),
                                    Integer.valueOf(message.getSenderid()),
                                    mPocketDao.getUser().getServerUserId(),
                                    message.getMessage(),
                                    String.valueOf(new Date()),
                                    (mPocketDao.getChats().size() - 1), 0));

                            if (newContactListener != null)
                                newContactListener.onUnknownContact();
                        }

                        // ChatMessages, при загрузке диалога - устанавливать все как прочитанное
                    }
                }
            }
        }

        ;
        IntentFilter intentFilter = new IntentFilter(WEBSOCKET_MESSAGE_TAG);
        context.registerReceiver(messageReceiver, intentFilter);
    }

    public void bindWss(String token) {
        intent = new Intent(context, PocketMessengerWssService.class);
        intent.putExtra(TOKEN_INTENT, token);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                isServiceConnected = true;
                wssService = ((PocketMessengerWssService.MyBinder) service).getService();
                if (wssListener != null)
                    wssListener.onWssConnected();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Toast.makeText(context, "Wss Service Stopped", Toast.LENGTH_SHORT).show();
                isServiceConnected = false;
            }

            @Override
            public void onBindingDied(ComponentName name) {
                Toast.makeText(context, "Wss Service Broken", Toast.LENGTH_SHORT).show();
                isServiceConnected = false;
            }
        };
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public static void sendMessage(String message) {
        wssService.sendMessage(message);
    }

}
