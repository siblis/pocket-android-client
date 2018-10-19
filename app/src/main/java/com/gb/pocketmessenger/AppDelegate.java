package com.gb.pocketmessenger;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.gb.pocketmessenger.DataBase.PocketDataBase;
import com.gb.pocketmessenger.Network.WssConnector;
import com.gb.pocketmessenger.services.PocketMessengerWssService;

import static com.gb.pocketmessenger.Constants.MESSAGE_BODY;
import static com.gb.pocketmessenger.Constants.WEBSOCKET_MESSAGE_TAG;
import static com.gb.pocketmessenger.services.PocketMessengerWssService.TOKEN_INTENT;

public class AppDelegate extends Application {

    private PocketDataBase mPocketDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mPocketDatabase = Room.databaseBuilder(getApplicationContext(), PocketDataBase.class,"pocket_database")
                .allowMainThreadQueries()
                .build();
        WssConnector.initInstance(getApplicationContext());
    }

    public PocketDataBase getPocketDatabase() {
        return mPocketDatabase;
    }
}