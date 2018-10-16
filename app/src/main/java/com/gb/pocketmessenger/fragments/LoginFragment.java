package com.gb.pocketmessenger.fragments;


import android.content.ComponentName;
import android.content.Intent;
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
import com.gb.pocketmessenger.Network.ConnectionToServer;
import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.models.User;
import com.gb.pocketmessenger.services.PocketMessengerWssService;

import java.util.concurrent.ExecutionException;

import se.simbio.encryption.Encryption;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.gb.pocketmessenger.Constants.CURRENT_SERVER;
import static com.gb.pocketmessenger.services.PocketMessengerWssService.TOKEN_INTENT;

public class LoginFragment extends Fragment {

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    private Intent intent;
    private ServiceConnection serviceConnection;
    private PocketMessengerWssService wssService;
    private Boolean isServiceConnected;

    private EditText login;
    private EditText password;
    private SharedPreferences mPrefs;
    private static final String TAG = "tar";
    private String mAndroidId;
    private String mUserId;
    private String mUserPass;
    private String result = "";
    private String mCryptoKey = "vnfjn&^6fh4673";
    private Encryption encryption;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAndroidId = getDeviceId();
        Log.d(TAG, "Device ID: " + mAndroidId);
        mPrefs = getContext().getSharedPreferences("com.gb.pocketmessenger.PREFERENCE", getContext().MODE_PRIVATE);

        //TODO Раскомментируйте следующую строку для LOGOUT. После создания макета будет привязано к кнопке logout.
        //deleteUser();

        loadUser();

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
            Log.d(TAG, "authentication: " + result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String token = parseToken(result);
        newUser.setToken(token);
        if (result.contains("You logged success")) {
            if (!checkSavedUser()) saveUser();
            if (!token.isEmpty()) bindWss(token);
//            Deprecated
//            loadChatMessagesFragment();

            Intent intent = new Intent(getActivity(), ChatActivity.class);
            startActivity(intent);

            Toast.makeText(getContext(), "You logged successfully!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "You logged successfully!");
        } else {
            Toast.makeText(getContext(), "Incorrect Login or Password!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Incorrect Login or Password!");
        }

        String userId = getUserId(newUser);
    }

    private String getUserId(User newUser) {
        String userID = " ";
        ConnectionToServer connection = new ConnectionToServer("GET_ID", newUser);
        connection.execute(CURRENT_SERVER);
        try {
            userID   = connection.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return userID;
    }

    private String parseToken(String data) {
        String[] resultArr = data.split(" ");
        return resultArr[3].substring(1, 17);
    }

    private void bindWss(String token) {
        intent = new Intent(getContext(), PocketMessengerWssService.class);
        intent.putExtra(TOKEN_INTENT, token);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                isServiceConnected = true;
                Toast.makeText(getContext(), "Wss Service Started", Toast.LENGTH_SHORT).show();
                wssService = ((PocketMessengerWssService.MyBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Toast.makeText(getContext(), "Wss Service Stopped", Toast.LENGTH_SHORT).show();

                isServiceConnected = false;
            }

            @Override
            public void onBindingDied(ComponentName name) {
                Toast.makeText(getContext(), "Wss Service Broken", Toast.LENGTH_SHORT).show();

                isServiceConnected = false;
            }
        };
        getActivity().bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private String getDeviceId() {
        return Secure.getString(getContext().getContentResolver(),
                Secure.ANDROID_ID);
    }

    private Boolean checkSavedUser() {

        return !mPrefs.getString("user_id", "#error!").equals("#error!")
                && !mPrefs.getString("user_pass", "#error!").equals("#error!");
    }

    private void saveUser() {
        String cUser = crypt(mUserId);
        String cPass = crypt(mUserPass);
        mPrefs.edit().putString("user_id", cUser).apply();
        mPrefs.edit().putString("user_pass", cPass).apply();
        Log.d(TAG, "User saved!");
    }

    private void loadUser() {
        mUserId = mPrefs.getString("user_id", "#error!");
        mUserPass = mPrefs.getString("user_pass", "#error!");
        if (!mUserId.equals("#error!")) mUserId = decrypt(mUserId);
        if (!mUserPass.equals("#error!")) mUserPass = decrypt(mUserPass);
    }

    // Для LOGOUT!

    private void deleteUser() {
        mPrefs.edit().remove("user_id").apply();
        mPrefs.edit().remove("user_pass").apply();
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
