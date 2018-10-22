package com.gb.pocketmessenger.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gb.pocketmessenger.Adapters.ContactsAdapter;
import com.gb.pocketmessenger.AppDelegate;
import com.gb.pocketmessenger.DataBase.PocketDao;
import com.gb.pocketmessenger.R;

import java.util.Objects;

public class ContactList extends Fragment {

    //private RecyclerView mContactsRecycler;
    //private final ContactsAdapter mContactsAdapter = new ContactsAdapter();
    //private PocketDao mPocketDao;

    public static ContactList newInstance() {
        return new ContactList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //mPocketDao = ((AppDelegate) Objects.requireNonNull(getActivity()).getApplicationContext()).getPocketDatabase().getPocketDao();
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //mContactsRecycler = view.findViewById(R.id.contacts_recycler);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mContactsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        //mContactsRecycler.setAdapter(mContactsAdapter);
        //mContactsAdapter.addData(mPocketDao.getContacts());
    }
}
