package com.gb.pocketmessenger.DataBase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class SettingsTable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int mId;

    public SettingsTable() {
    }

    public SettingsTable(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
}
