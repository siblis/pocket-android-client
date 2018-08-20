package com.gb.pocketmessenger.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gb.pocketmessenger.Adapters.FragmentPager;
import com.gb.pocketmessenger.ChatActivity;
import com.gb.pocketmessenger.R;

public class TabsFragment extends Fragment{



    private static final String ARG_IND = "Screen_Index";

    public static TabsFragment newInstance(ChatActivity.Tabs tab) {
        TabsFragment fragment = new TabsFragment();
        Bundle bundle = new Bundle();
        switch (tab) {
            case Chat:
                bundle.putInt(ARG_IND, 0);
                break;
            case Contacts:
                bundle.putInt(ARG_IND, 1);
                break;
        }
//        bundle.putInt(ARG_IND, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        int index = getArguments().getInt(ARG_IND, 0);

        View view = inflater.inflate(R.layout.fragment_tabs, container, false);

        ViewPager viewPager = view.findViewById(R.id.viewpager);

        FragmentPagerAdapter fragmentPager = new FragmentPager(getChildFragmentManager(), getContext());

        viewPager.setAdapter(fragmentPager);
        viewPager.setCurrentItem(index);

        TabLayout tabLayout = view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}
