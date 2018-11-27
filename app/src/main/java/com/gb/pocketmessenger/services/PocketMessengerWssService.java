package com.gb.pocketmessenger.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

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
import static com.gb.pocketmessenger.Constants.MESSAGE_BODY;
import static com.gb.pocketmessenger.Constants.STATUS_CONNECTED;
import static com.gb.pocketmessenger.Constants.STATUS_DISCONNECTED;
import static com.gb.pocketmessenger.Constants.STATUS_INCOMING_TEXT_MESSAGE;
import static com.gb.pocketmessenger.Constants.STATUS_MESSAGE;
import static com.gb.pocketmessenger.Constants.WEBSOCKET_MESSAGE_TAG;

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
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        token = intent.getStringExtra(TOKEN_INTENT);
        startWssConnection(token);
        return super.onStartCommand(intent, flags, startId);
    }

    private void startWssConnection(String token) {
        wssThread = Executors.newSingleThreadExecutor();
        Future<?> socketResult = wssThread.submit(() -> {
            if (chatWebSocket != null) {
                reconnect();
            } else {
                try {

                    chatWebSocket = new WebSocketFactory().createSocket(CURRENT_SERVER + "/v1/ws/");
                    chatWebSocket.addHeader("token", token);
                    chatWebSocket.addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);
                    chatWebSocket.addListener(new WebSocketAdapter() {
                        @Override
                        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                            super.onConnected(websocket, headers);
                            Intent connected = new Intent(WEBSOCKET_MESSAGE_TAG);
                            connected.putExtra(STATUS_MESSAGE, STATUS_CONNECTED);
                            sendBroadcast(connected);
                        }

                        @Override
                        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                            Intent disconnected = new Intent(WEBSOCKET_MESSAGE_TAG);
                            disconnected.putExtra(STATUS_MESSAGE, STATUS_DISCONNECTED);
                            sendBroadcast(disconnected);
                            if (closedByServer) {
                                reconnect();
                            }
                        }

                        @Override
                        public void onTextMessage(WebSocket websocket, String text) {
                            Intent textMessage = new Intent(WEBSOCKET_MESSAGE_TAG);
                            textMessage.putExtra(STATUS_MESSAGE, STATUS_INCOMING_TEXT_MESSAGE);
                            textMessage.putExtra(MESSAGE_BODY, text);
                            sendBroadcast(textMessage);
                        }

                        @Override
                        public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                            super.onPongFrame(websocket, frame);
                            chatWebSocket.sendPing("Hello_pong!");
                        }

                        @Override
                        public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                            super.onPingFrame(websocket, frame);
                            chatWebSocket.sendPong("Hello_ping");
                        }

                        @Override
                        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                            reconnect();
                        }

                        @Override
                        public void onUnexpectedError(WebSocket websocket, WebSocketException cause) {
                            reconnect();
                        }

                    });
                    chatWebSocket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WebSocketException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void reconnect() {
        try {
            chatWebSocket = chatWebSocket.recreate().connect();
        } catch (WebSocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setUpAlarm(final Context context, final Intent intent, final int timeInterval)
    {
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final PendingIntent pi = PendingIntent.getBroadcast(context, timeInterval, intent, 0);
        am.cancel(pi);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            final AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(System.currentTimeMillis() + timeInterval, pi);
            am.setAlarmClock(alarmClockInfo, pi);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeInterval, pi);
        else
            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeInterval, pi);
    }

    public void sendMessage(String message) {
        if (chatWebSocket != null)
            chatWebSocket.sendText(message);
    }
}