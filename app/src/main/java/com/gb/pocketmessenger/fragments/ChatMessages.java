package com.gb.pocketmessenger.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gb.pocketmessenger.ChatActivity;
import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.models.Message;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

public class ChatMessages extends Fragment implements MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener,
        MessageInput.TypingListener {

    private static final int TOTAL_MESSAGES_COUNT = 50;
    private MessagesList messages;
    private MessagesListAdapter<Message> messageAdapter;
    private final String senderId = "0";    //TODO: get senderID


    public static ChatMessages newInstance(String dialogId) {
        //TODO: get messages
        return new ChatMessages();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_messages_list, container, false);

        messages = view.findViewById(R.id.messagesList);

        messageAdapter = new MessagesListAdapter<>(senderId, ChatActivity.imageLoader);
        messages.setAdapter(messageAdapter);

        return view;
    }

    public void newMessage(Message message) {
        messageAdapter.addToStart(message, true);
    }

    @Override
    public void onStartTyping() {

    }

    @Override
    public void onStopTyping() {

    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            //TODO: loadMessages();
        }
    }

    @Override
    public void onSelectionChanged(int count) {

    }
}
