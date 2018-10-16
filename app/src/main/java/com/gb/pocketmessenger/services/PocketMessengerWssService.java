package com.gb.pocketmessenger.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.gb.pocketmessenger.Constants.CURRENT_SERVER;

public class PocketMessengerWssService extends Service {

    public static final String TOKEN_INTENT = "token_intent";
    private WebSocket chatWebSocket;
    private String token;
    private ExecutorService wssThread;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public class MyBinder extends Binder {

        public PocketMessengerWssService getService() {
            return PocketMessengerWssService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        token = intent.getStringExtra(TOKEN_INTENT);
        startWssConnection(token);
        return null;
    }

    private void startWssConnection(String token) {
        wssThread = Executors.newSingleThreadExecutor();
        Future<?> socketResult = wssThread.submit(() -> {
            try {
                chatWebSocket = new WebSocketFactory().createSocket(CURRENT_SERVER + "/v1/ws_echo/");
                chatWebSocket.addHeader("Token", token);
                chatWebSocket.addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);
                chatWebSocket.addListener(new WebSocketAdapter() {
                    @Override
                    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                        String result = headers.toString();
                        super.onConnected(websocket, headers);
                    }

                    @Override
                    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);

                    }

                    @Override
                    public void onTextMessage(WebSocket websocket, String text) {
                        Log.d("1", "2");
                    }

                });
                chatWebSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WebSocketException e) {
                e.printStackTrace();
            }
        });
    }
}