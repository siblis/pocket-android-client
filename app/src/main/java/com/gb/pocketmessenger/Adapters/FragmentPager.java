package com.gb.pocketmessenger.Adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.gb.pocketmessenger.fragments.ChatList;
import com.gb.pocketmessenger.fragments.ContactList;

public class FragmentPager extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 2;
    //TODO: string res
    private final String[] tabTitles = {"CHATS", "CONTACTS"};
    private Context context;

    public FragmentPager(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ChatList.newInstance();
            case 1:
                return ContactList.newInstance();
            default:
                return ChatList.newInstance();
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }


}
