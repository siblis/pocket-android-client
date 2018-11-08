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

import com.gb.pocketmessenger.Adapters.ChatsAdapter;
import com.gb.pocketmessenger.AppDelegate;
import com.gb.pocketmessenger.ChatActivity;
import com.gb.pocketmessenger.DataBase.PocketDao;
import com.gb.pocketmessenger.R;

import java.util.Objects;

public class ChatList extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ChatActivity.OnNewChatAdded {

    private RecyclerView mChatsRecycler;
    private final ChatsAdapter mChatsAdapter = new ChatsAdapter();
    private PocketDao mPocketDao;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ChatsAdapter.OnChatClickListener mListener;

    public static ChatList newInstance() {
        return new ChatList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ChatsAdapter.OnChatClickListener) {
            mListener = (ChatsAdapter.OnChatClickListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mPocketDao = ((AppDelegate) Objects.requireNonNull(getActivity()).getApplicationContext()).getPocketDatabase().getPocketDao();
        View view = inflater.inflate(R.layout.fragment_chat_list_v2, container, false);
        ((ChatActivity) getActivity()).setOnNewChatAddedListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mChatsRecycler = view.findViewById(R.id.chats_recycler);
        mSwipeRefreshLayout = view.findViewById(R.id.chats_refresher);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mChatsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChatsAdapter.addData(mPocketDao.getChats());
        mChatsRecycler.setAdapter(mChatsAdapter);
        mChatsAdapter.setListener(mListener);
    }



    @Override
    public void onRefresh() {
        mChatsAdapter.addData(mPocketDao.getChats());
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDetach() {
        //mListener = null;
        super.onDetach();
    }

    @Override
    public void onNewChatAdded() {
        mChatsAdapter.addData(mPocketDao.getChats());
    }
}
