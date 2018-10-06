package com.gb.pocketmessenger.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gb.pocketmessenger.R;

public class LoginFragment extends Fragment {

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        view.findViewById(R.id.button_login).setOnClickListener(v -> loadChatMessagesFragment());
        view.findViewById(R.id.button_register).setOnClickListener(v -> loadRegisterFragment());
        return view;
    }

    //TODO REFACTOR
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
