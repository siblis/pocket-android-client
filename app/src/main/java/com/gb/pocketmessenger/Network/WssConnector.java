package com.gb.pocketmessenger.Network;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.gb.pocketmessenger.models.IncomingMessage;
import com.gb.pocketmessenger.services.PocketMessengerWssService;
import com.gb.pocketmessenger.utils.JsonParser;

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

    public interface OnIncomingMessage {
        void onIncomingMessage(String receiverId, String incomingMessage);
    }

    public interface OnWssConnected {
        void onWssConnected();
    }

    private WssConnector(Context context) {
        this.context = context;
    }

    public static void initInstance(Context context) {
        if (connector == null) {
            receiverInit(context);
            connector = new WssConnector(context);
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

    private static void receiverInit(Context context) {
        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String jsonMsg = intent.getStringExtra(MESSAGE_BODY);
                if (jsonMsg != null) {
                    IncomingMessage message = JsonParser.getIncomingMessage(jsonMsg);
                    Toast.makeText(context, message.getMessage(), Toast.LENGTH_SHORT).show();
                    if (listener != null)
                        listener.onIncomingMessage(message.getSenderid(), message.getMessage());
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
