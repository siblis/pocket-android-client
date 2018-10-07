package com.gb.pocketmessenger.Network;

import android.os.AsyncTask;

import com.gb.pocketmessenger.models.User;
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
    private User user;

    public ConnectionToServer(String action, User user) {
        this.action = action;
        this.user = user;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            return connectToServer(urls[0], action, user);
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    private String connectToServer(String myUrl, String action, User user) throws IOException {
        String data = "";
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("account_name", user.getLogin());
            if (user.geteMail() != null) userJson.put("email", user.geteMail());
            userJson.put("password", user.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (action) {
            case "REGISTER":
                InputStream inputstream = null;
                try {
                    URL url = new URL(myUrl + "/v1/users/");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);

                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write(userJson.toString());
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
// TODO получаем токен и логинимся с ним
                    } else if (responseCode == 409) {
                        data = "Такая учетная запись существует!";
                    } else if (responseCode == 400) {
                        data = "Неверные учетные данные";
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
            case "LOGIN":
                InputStream loginInputStream = null;
                try {
                    URL url = new URL(myUrl + "/v1/auth/");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    userJson.remove("email");
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write(userJson.toString());
                    wr.flush();
                    connection.connect();

                    loginInputStream = connection.getInputStream();
                    int responseCode = connection.getResponseCode();

                    if (responseCode == 200) {
                        inputstream = connection.getInputStream();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();

                        int read;
                        while ((read = inputstream.read()) != -1) {
                            bos.write(read);
                        }
                        byte[] result = bos.toByteArray();
                        bos.close();

                        data = new String(result);

                    } else  {
                        data = "ОШИБКА ЛОГИНА";
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (loginInputStream != null)
                        loginInputStream.close();

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

    public void setParams(String register, User newUser) {
    }
}