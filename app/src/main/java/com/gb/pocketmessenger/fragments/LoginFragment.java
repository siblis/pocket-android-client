package com.gb.pocketmessenger.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gb.pocketmessenger.R;

public class LoginFragment extends Fragment implements Button.OnClickListener {

    private Button newUser;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        newUser = view.findViewById(R.id.button_register);
        newUser.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_register:
                FragmentManager fr = getFragmentManager();
                FragmentTransaction transaction = fr.beginTransaction();
                transaction.replace(R.id.login_container, RegisterFragment.newInstance());
                transaction.commit();
                break;
        }
    }
}
