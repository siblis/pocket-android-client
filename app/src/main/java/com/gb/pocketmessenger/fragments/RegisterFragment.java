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
import com.gb.pocketmessenger.models.User;

import java.util.concurrent.ExecutionException;

import static com.gb.pocketmessenger.Constants.CURRENT_SERVER;

public class RegisterFragment extends Fragment {

    private EditText loginEditText;
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
        loginEditText = view.findViewById(R.id.login_edittext);
        passwordEditText = view.findViewById(R.id.password_edittext);
        emailEditText = view.findViewById(R.id.email_edittext);
        registerButton = view.findViewById(R.id.register_ok_button);
        cancelButton = view.findViewById(R.id.register_cancel_button);
        registerButton.setOnClickListener(v ->
                sendRegisterData(loginEditText.getText().toString(), emailEditText.getText().toString(),
                        passwordEditText.getText().toString()));
        return view;
    }

    private void sendRegisterData(String login, String email, String password) {
        User newUser = new User(login, password);
        newUser.seteMail(email);
        ConnectionToServer connection = new ConnectionToServer("REGISTER", newUser);
        connection.execute(CURRENT_SERVER);
        try {
            serverResponse.setText(connection.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}
