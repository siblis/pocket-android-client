package com.gb.pocketmessenger.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.provider.Settings.Secure;

import com.gb.pocketmessenger.Network.ConnectionToServer;
import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.models.User;


import java.util.concurrent.ExecutionException;

import static com.gb.pocketmessenger.fragments.RegisterFragment.POCKET_MESSENGER_URL;

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
    private String result = "";

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
        connection.execute(POCKET_MESSENGER_URL);
        try {
            result = connection.get();
            //Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "authentication: " + result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (result.contains("You logged success")) {
            if (!checkSavedUser()) saveUser();
            loadChatMessagesFragment();
            Toast.makeText(getContext(), "You logged successfully!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "You logged successfully!");
        } else {
            Toast.makeText(getContext(), "Incorrect Login or Password!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Incorrect Login or Password!");
        }


    }

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

    private String getDeviceId() {
        return Secure.getString(getContext().getContentResolver(),
                Secure.ANDROID_ID);
    }

    private Boolean checkSavedUser() {
        //loadUser();
        if (mPrefs.getString("user_id", "#error!").equals("#error!") || mPrefs.getString("user_pass", "#error!").equals("#error!"))
            return false;
        else return true;
    }

    private void saveUser() {
        mPrefs.edit().putString("user_id", mUserId).apply();
        mPrefs.edit().putString("user_pass", mUserPass).apply();
        Log.d(TAG, "User saved!");
        Log.d(TAG, "Prefs User_ID: " + mUserId + " User_PASS: " + mUserPass);
    }

    private void loadUser() {
        mUserId = mPrefs.getString("user_id", "#error!");
        mUserPass = mPrefs.getString("user_pass", "#error!");
        Log.d(TAG, "User loaded!");
        Log.d(TAG, "Prefs User_ID: " + mUserId + " User_PASS: " + mUserPass);
    }

    // Для LOGOUT!
    private void deleteUser() {
        mPrefs.edit().remove("user_id").apply();
        mPrefs.edit().remove("user_pass").apply();
    }

}
