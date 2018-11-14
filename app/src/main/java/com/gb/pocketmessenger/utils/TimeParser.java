package com.gb.pocketmessenger.utils;

import java.util.Calendar;
import java.util.Date;

public class TimeParser {

    public static String getTime() {
        Date currentTime = Calendar.getInstance().getTime();
        String time = (currentTime.getHours() + 1) + ":"
                + (currentTime.getMinutes() + 1) + ":"
                + (currentTime.getSeconds() + 1) + " "
                + currentTime.getDate() + "."
                + (currentTime.getMonth() + 1) + "."
                + (currentTime.getYear() + 1900);
        return time;
    }
}
