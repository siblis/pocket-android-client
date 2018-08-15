package com.gb.pocketmessenger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkingToken()) {
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
            finish();
        } else {
            //TODO:  Authorization and Registration fragments
            setContentView(R.layout.activity_main);
        }
    }

    //TODO:  check token
    private boolean checkingToken() {
        return true;
    }

}
