package com.gb.pocketmessenger.DataBase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {ChatsTable.class, ContactsTable.class, MessagesTable.class, SettingsTable.class, UsersChatsTable.class, UserTable.class}, version = 3)
public abstract class PocketDataBase extends RoomDatabase {
    public abstract PocketDao getPocketDao();
}
