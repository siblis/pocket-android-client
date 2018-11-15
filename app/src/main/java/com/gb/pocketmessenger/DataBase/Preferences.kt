package com.gb.pocketmessenger.DataBase

import android.content.Context
import android.content.SharedPreferences

const val PREFS_FILENAME = "com.gb.pocketmessenger.prefs"
const val IS_NOTIFICATIONS = "is_notifications"

class Preferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME,
            Context.MODE_PRIVATE)

    var isNotifications: Boolean?
        get() = prefs.getBoolean(IS_NOTIFICATIONS, true)
        set(value) = prefs.edit().putBoolean(IS_NOTIFICATIONS, value!!).apply()
}