package com.gb.pocketmessenger.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gb.pocketmessenger.Adapters.ContactsAdapter;
import com.gb.pocketmessenger.AppDelegate;
import com.gb.pocketmessenger.ChatActivity;
import com.gb.pocketmessenger.DataBase.PocketDao;
import com.gb.pocketmessenger.R;

import java.util.Objects;

public class ContactList extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ChatActivity.OnContactAdded {

    private RecyclerView mContactsRecycler;
    private final ContactsAdapter mContactsAdapter = new ContactsAdapter();
    private PocketDao mPocketDao;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ContactsAdapter.OnItemClickListener mListener;

    public static ContactList newInstance() {
        return new ContactList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ContactsAdapter.OnItemClickListener) {
            mListener = (ContactsAdapter.OnItemClickListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mPocketDao = ((AppDelegate) Objects.requireNonNull(getActivity()).getApplicationContext()).getPocketDatabase().getPocketDao();
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        ((ChatActivity) getActivity()).setOnContactAddListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mContactsRecycler = view.findViewById(R.id.contacts_recycler);
        mSwipeRefreshLayout = view.findViewById(R.id.contacts_refresher);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContactsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mContactsAdapter.addData(mPocketDao.getContacts());
        mContactsRecycler.setAdapter(mContactsAdapter);
        mContactsAdapter.setListener(mListener);
    }

    public void adapterReload() {

        mContactsAdapter.addData(mPocketDao.getContacts());
    }

    @Override
    public void onRefresh() {
        mContactsAdapter.addData(mPocketDao.getContacts());
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onContactAdded() {
        mContactsAdapter.addData(mPocketDao.getContacts());
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }
}
