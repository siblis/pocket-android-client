package com.gb.pocketmessenger;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.gb.pocketmessenger.DataBase.PocketDataBase;

public class AppDelegate extends Application {

    private PocketDataBase mPocketDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mPocketDatabase = Room.databaseBuilder(getApplicationContext(), PocketDataBase.class,"pocket_database")
                .allowMainThreadQueries()
                .build();
    }

    public PocketDataBase getPocketDatabase() {
        return mPocketDatabase;
    }
}