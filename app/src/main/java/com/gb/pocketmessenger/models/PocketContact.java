package com.gb.pocketmessenger.models;

import android.support.annotation.NonNull;

public class PocketContact implements Comparable {
    private String id;
    private String eMail;
    private String name;

    public PocketContact(String id, String eMail, String name) {
        this.id = id;
        this.eMail = eMail;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return 0;
    }
}
