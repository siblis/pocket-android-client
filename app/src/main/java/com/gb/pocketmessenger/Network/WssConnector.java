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
import com.gb.pocketmessenger.DataBase.Dao;
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

                        Log.d(TAG, "Incomming message: FROM = " + message.getSenderName() + " ID=" + Integer.valueOf(message.getSenderid()) + " TO = " + mPocketDao.getUser().getServerUserId() + " TEXT = " +
                                message.getMessage());

                        String chatName = Dao.incommingMessage(mPocketDao, message);

                        if (chatName == null) {
                            if (newContactListener != null)
                                newContactListener.onUnknownContact();
                        }

                        // ChatMessages, при загрузке диалога - устанавливать все как прочитанное
                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(WEBSOCKET_MESSAGE_TAG);
        context.registerReceiver(messageReceiver, intentFilter);
    }

    public void startService(String token){
        intent = new Intent(context, PocketMessengerWssService.class);
        intent.putExtra(TOKEN_INTENT, token);
        context.startService(intent);
    }

    public void bindWss() {
        intent = new Intent(context, PocketMessengerWssService.class);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                wssService = ((PocketMessengerWssService.MyBinder) service).getService();
                if (wssListener != null)
                    wssListener.onWssConnected();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Toast.makeText(context, "Wss Service Stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBindingDied(ComponentName name) {
                Toast.makeText(context, "Wss Service Broken", Toast.LENGTH_SHORT).show();
            }
        };

        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public static void sendMessage(String message) {
        wssService.sendMessage(message);
    }

}
