package com.gb.pocketmessenger.Network;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.gb.pocketmessenger.DataBase.MessagesTable;
import com.gb.pocketmessenger.DataBase.PocketDao;
import com.gb.pocketmessenger.DataBase.PocketDataBase;
import com.gb.pocketmessenger.models.IncomingMessage;
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
    private static PocketDao mPocketDao;

    public interface OnIncomingMessage {
        void onIncomingMessage(String receiverId, String incomingMessage);
    }

    public interface OnWssConnected {
        void onWssConnected();
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
                        Toast.makeText(context, "Сообщение отправлено", Toast.LENGTH_SHORT).show();
                    } else {
                        if (listener != null)
                            listener.onIncomingMessage(message.getSenderid(), message.getMessage());

                        Toast.makeText(context, "Входящее сообщение от " + message.getSenderName(), Toast.LENGTH_SHORT).show();

// Здесь нужно добавить есть ли у нас чат с этим человеком, если есть - получаем dialogID, если нет - создаем новый чат-рум


//                        mPocketDao.insertMessage(new MessagesTable(mPocketDao.getMessages().size(),
//                                Integer.valueOf(message.getReceiver()),
//                                mPocketDao.getUser().getServerUserId(),
//                                message.getMessage(),
//                                String.valueOf(new Date()),
//                                Integer.valueOf(dialogId), 0)); // здесь мы будем статус всегда устанавливать как непрочитанное, а в
                    }                                                  // ChatMessages, при загрузке диалога - устанавливать все как прочитанное
                }
            }
        };
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
