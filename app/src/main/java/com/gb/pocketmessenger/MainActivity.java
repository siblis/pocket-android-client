package com.gb.pocketmessenger;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gb.pocketmessenger.fragments.LoginFragment;
import com.gb.pocketmessenger.fragments.TabsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkingToken()) {
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_main);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.login_container, LoginFragment.newInstance());
            transaction.commit();
        }
    }

    //TODO:  check token
    private boolean checkingToken() {
        return false;
    }

}
