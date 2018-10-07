package com.gb.pocketmessenger.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        // TODO времяночка для тестов. Потом нужно бует по этим данным доставть пользователя из базы и передавать его
        login = view.findViewById(R.id.login_textview);
        password = view.findViewById(R.id.password_textview);
        view.findViewById(R.id.button_login).setOnClickListener(v ->
                loadChatMessagesFragment(login.getText().toString(), password.getText().toString()));
        view.findViewById(R.id.button_register).setOnClickListener(v -> loadRegisterFragment());
        return view;
    }

    //TODO REFACTOR
    private void loadChatMessagesFragment( String login, String password) {
                User newUser = new User(login, password);
        ConnectionToServer connection = new ConnectionToServer("LOGIN", newUser);
        connection.execute(POCKET_MESSENGER_URL);
        try {
            Toast.makeText(getContext(), connection.get(), Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
