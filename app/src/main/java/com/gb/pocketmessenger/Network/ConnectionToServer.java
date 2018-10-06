package com.gb.pocketmessenger.Network;

import android.os.AsyncTask;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static com.gb.pocketmessenger.fragments.ChatMessages.WSS_POCKETMSG;


public class ConnectionToServer extends AsyncTask<String, Void, String> {
    private WebSocket chatWebSocket;
    private String action;
    private String login;
    private String password;
    private String email;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            return connectToServer(urls[0], action, login, email, password);
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    private String connectToServer(String myurl, String action, String... params) throws IOException {
        String data = "";
        switch (action) {
            case "REGISTER":
                InputStream inputstream = null;
                try {
                    URL url = new URL(myurl + "/v1/users/");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    JSONObject auth = new JSONObject();
                    try {
                        auth.put("account_name", params[0]);
                        auth.put("email", params[1]);
                        auth.put("password", params[2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write(auth.toString());
                    wr.flush();
                    connection.connect();
                    int responseCode = connection.getResponseCode();

                    if (responseCode == 201) {
                        inputstream = connection.getInputStream();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();

                        int read;
                        while ((read = inputstream.read()) != -1) {
                            bos.write(read);
                        }
                        byte[] result = bos.toByteArray();
                        bos.close();

                        data = new String(result);

                    } else {
                        data = connection.getResponseMessage() + " . Error Code : " + responseCode;
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputstream != null) {
                        inputstream.close();
                    }
                }
                return data;
        }
        return data;
    }

    private void getWebSocketConnection() {
        // TODO в параметр метода передаем TOKEN
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    chatWebSocket = new WebSocketFactory().createSocket(WSS_POCKETMSG);
                    chatWebSocket.addHeader("Token", "36a6908c783ba6e5");
                    chatWebSocket.addListener(new WebSocketAdapter() {
                        @Override
                        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                            super.onConnected(websocket, headers);
                        }

                        @Override
                        public void onTextMessage(WebSocket websocket, String text) throws Exception {
                        }
                    });
                    chatWebSocket.connect();
                } catch (WebSocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setParams(String action, String login, String email, String password) {
        this.action = action;
        this.login = login;
        this.email = email;
        this.password = password;
    }
}