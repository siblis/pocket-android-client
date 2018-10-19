package com.gb.pocketmessenger.fragments;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.provider.Settings.Secure;

import com.gb.pocketmessenger.ChatActivity;
import com.gb.pocketmessenger.AppDelegate;
import com.gb.pocketmessenger.DataBase.PocketDao;
import com.gb.pocketmessenger.DataBase.UserTable;
import com.gb.pocketmessenger.Network.ConnectionToServer;
import com.gb.pocketmessenger.Network.WssConnector;
import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.models.User;
import com.gb.pocketmessenger.utils.JsonParser;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import se.simbio.encryption.Encryption;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.gb.pocketmessenger.Constants.CURRENT_SERVER;
import static com.gb.pocketmessenger.Constants.MESSAGE_BODY;
import static com.gb.pocketmessenger.Constants.WEBSOCKET_MESSAGE_TAG;
import static com.gb.pocketmessenger.services.PocketMessengerWssService.TOKEN_INTENT;


public class LoginFragment extends Fragment {

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    private EditText login;
    private EditText password;
    private SharedPreferences mPrefs;
    private static final String TAG = "tar";
    private String mAndroidId;
    private String mUserId;
    private String mUserPass;
    private String mUserEmail = "e-mail";
    private String token;
    private int mServerUserId;
    private String result = "";
    private String mCryptoKey = "vnfjn&^6fh4673";
    private Encryption encryption;
    private PocketDao mPocketDao;
    private WssConnector connector;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAndroidId = getDeviceId();
        Log.d(TAG, "Device ID: " + mAndroidId);
        mPrefs = getContext().getSharedPreferences("com.gb.pocketmessenger.PREFERENCE", getContext().MODE_PRIVATE);
        mPocketDao = ((AppDelegate) Objects.requireNonNull(getActivity()).getApplicationContext()).getPocketDatabase().getPocketDao();
        connector = WssConnector.getInstance();

        //TODO Раскомментируйте следующую строку для LOGOUT. После создания макета будет привязано к кнопке logout.
        deleteUser();

        if (checkSavedUser()) {
            loadUser();
            Log.d(TAG, "USER: " + mPocketDao.getUser().getUserName()
                    + " PASS: " + mPocketDao.getUser().getPassword()
                    + " EMAIL: " + mPocketDao.getUser().getEmail()
                    + " TOKEN: " + mPocketDao.getUser().getToken()
                    + " SERVER_USER_ID: " + mPocketDao.getUser().getServerUserId());
        } else Log.d(TAG, "USER: Empty");

        if (!checkSavedUser()) {

            // TODO времяночка для тестов. Потом нужно бует по этим данным доставть пользователя из базы и передавать его
            login = view.findViewById(R.id.login_textview);
            password = view.findViewById(R.id.password_textview);
            view.findViewById(R.id.button_login).setOnClickListener(v -> {
                mUserId = login.getText().toString();
                mUserPass = password.getText().toString();
                authentication(mUserId, mUserPass);
            });
            view.findViewById(R.id.button_register).setOnClickListener(v -> loadRegisterFragment());
            mUserId = login.getText().toString();
            mUserPass = password.getText().toString();

        } else {
            authentication(mUserId, mUserPass);
        }

        return view;
    }

    //TODO REFACTOR
    private void authentication(String login, String password) {
        User newUser = new User(login, password);
        ConnectionToServer connection = new ConnectionToServer("LOGIN", newUser);
        connection.execute(CURRENT_SERVER);
        try {
            result = connection.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "result: " + result);
        token = JsonParser.parseToken(result);
        Log.d(TAG, "token: " + token);


        if (token != null) {

            newUser.setToken(token);
            connector.bindWss(token);
            User inServerUser = getUserInfo(newUser);
            String nickName = inServerUser.getLogin();
            mUserEmail = inServerUser.geteMail();
            mServerUserId = Integer.parseInt(inServerUser.getId());

            if (!checkSavedUser()) saveUser();

            Toast.makeText(getContext(), "You logged successfully!", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "result: " + result);
            Log.d(TAG, "token: " + token);
            Log.d(TAG, "You logged successfully!");

//            Intent intent = new Intent(getActivity(), ChatActivity.class);
//            startActivity(intent);
            loadChatMessagesFragment();
        } else {
            Toast.makeText(getContext(), "Incorrect Login or Password!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Incorrect Login or Password!");
        }
    }

    private User getUserInfo(User newUser) {
        String userID = " ";
        ConnectionToServer connection = new ConnectionToServer("GET_ID", newUser);
        connection.execute(CURRENT_SERVER);
        try {
            userID = connection.get();
            Log.d(TAG, "getUserId: " + userID);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return JsonParser.parseUser(userID);
    }


    private String getDeviceId() {
        return Secure.getString(getContext().getContentResolver(),
                Secure.ANDROID_ID);
    }

    private Boolean checkSavedUser() {
        return mPocketDao.getUser() != null;
    }

    private void saveUser() {
        String cUser = crypt(mUserId);
        String cPass = crypt(mUserPass);
        mPocketDao.insertUser(new UserTable(0, cUser, cPass, mUserEmail, token, mServerUserId));
        Log.d(TAG, "User saved!");
    }

    private void loadUser() {
        if (checkSavedUser()) {
            mUserId = mPocketDao.getUser().getUserName();
            mUserPass = mPocketDao.getUser().getPassword();
            mUserId = decrypt(mUserId);
            mUserPass = decrypt(mUserPass);
        } else Log.d(TAG, "loadUser: ERROR, NO USER!");
    }

    // Для LOGOUT!
    private void deleteUser() {
        if (mPocketDao.getUser() != null)
            mPocketDao.deleteUser(mPocketDao.getUser());
    }

    private String crypt(String mSring) {
        encryption = Encryption.getDefault(mCryptoKey, mAndroidId, new byte[16]);
        String mEncryptedString = encryption.encryptOrNull(mSring);
        return mEncryptedString;
    }

    private String decrypt(String encryptedUser) {
        encryption = Encryption.getDefault(mCryptoKey, mAndroidId, new byte[16]);
        String decryptedUser = encryption.decryptOrNull(encryptedUser);
        return decryptedUser;
    }

    @Deprecated
    private void loadChatMessagesFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.login_container, new ChatMessages());
        transaction.commit();
    }

    private void loadRegisterFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.login_container, new RegisterFragment());
        transaction.commit();
    }

}
