package com.gb.pocketmessenger.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gb.pocketmessenger.Network.ConnectionToServer;
import com.gb.pocketmessenger.R;

import java.util.concurrent.ExecutionException;

public class RegisterFragment extends Fragment {
    public static final String POCKET_MESSENGER_URL = "https://pocketmsg.ru:8888";
    private EditText loginEditext;
    private TextView serverResponse;
    private EditText passwordEditText;
    private EditText emailEditText;
    private Button registerButton;
    private Button cancelButton;


    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        serverResponse = view.findViewById(R.id.server_response);
        loginEditext = view.findViewById(R.id.login_edittext);
        passwordEditText = view.findViewById(R.id.password_edittext);
        emailEditText = view.findViewById(R.id.email_edittext);
        registerButton = view.findViewById(R.id.register_ok_button);
        cancelButton = view.findViewById(R.id.register_cancel_button);
        registerButton.setOnClickListener(v ->
                sendRegisterData(loginEditext.getText().toString(), emailEditText.getText().toString(),
                        passwordEditText.getText().toString()));
        return view;
    }

    private void sendRegisterData(String login, String email, String password) {
        ConnectionToServer connection = new ConnectionToServer();
        connection.setParams("REGISTER", login, email, password);
        connection.execute(POCKET_MESSENGER_URL);
        try {
            serverResponse.setText(connection.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}
