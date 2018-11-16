package com.gb.pocketmessenger

import android.app.Application
import android.arch.persistence.room.Room
import com.gb.pocketmessenger.DataBase.PocketDataBase
import com.gb.pocketmessenger.DataBase.Preferences
import com.gb.pocketmessenger.Network.WssConnector

class AppDelegate : Application() {

    companion object {
        var prefs: Preferences? = null
    }

    var pocketDatabase: PocketDataBase? = null
        private set

    override fun onCreate() {
        super.onCreate()
        pocketDatabase = Room
                .databaseBuilder<PocketDataBase>(applicationContext,
                        PocketDataBase::class.java,
                        "pocket_database")
                .allowMainThreadQueries()
                .build()
        WssConnector.initInstance(applicationContext, pocketDatabase!!.pocketDao)
        prefs = Preferences(applicationContext)
    }
}