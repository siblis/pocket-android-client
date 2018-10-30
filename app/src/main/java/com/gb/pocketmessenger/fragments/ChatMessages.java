package com.gb.pocketmessenger.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gb.pocketmessenger.AppDelegate;
import com.gb.pocketmessenger.DataBase.ContactsTable;
import com.gb.pocketmessenger.DataBase.PocketDao;
import com.gb.pocketmessenger.DataBase.UsersChatsTable;
import com.gb.pocketmessenger.Network.WssConnector;
import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.models.Message;
import com.gb.pocketmessenger.models.User;
import com.gb.pocketmessenger.utils.JsonParser;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatMessages extends Fragment implements MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageInput.TypingListener, WssConnector.OnIncomingMessage {

    protected ImageLoader imageLoader;
    private MessagesList messages;
    private MessagesListAdapter<Message> messageAdapter;
    private Message message;
    private final String senderId = "0";    //TODO: get senderID
    private String dialogId;
    private WssConnector connector;
    private PocketDao mPocketDao;
    private String receiver;


    public static ChatMessages newInstance(String dialogId) {
        ChatMessages myFragment = new ChatMessages();

        Bundle args = new Bundle();
        args.putString("DIALOG_ID", dialogId);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connector = WssConnector.getInstance();
        connector.setOnIncomingMessageListener(this);
        dialogId = getArguments().getString("DIALOG_ID", "");
        mPocketDao = ((AppDelegate) Objects.requireNonNull(getActivity()).getApplicationContext()).getPocketDatabase().getPocketDao();
        List<User> chatUsers = getChatUsers(Integer.parseInt(dialogId));
        int myId = mPocketDao.getUser().getServerUserId();
        if (chatUsers != null) {
            for (User user : chatUsers) {
                if (!user.getId().equals(String.valueOf(myId))) {
                    receiver = user.getId();
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages_list, container, false);
        messages = view.findViewById(R.id.messagesList);

        initAdapter();
        MessageInput input = view.findViewById(R.id.input);
        input.setInputListener(this);
        input.setTypingListener(this);
        input.setAttachmentsListener(this);

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


    private void initAdapter() {
        messageAdapter = new MessagesListAdapter<>(senderId, imageLoader);
        messages.setAdapter(messageAdapter);
    }

    @Override
    public void onAddAttachments() {

    }

    @Override
    public boolean onSubmit(CharSequence input) {

        message = new Message(input.toString());
        message.user.id = senderId;
        message.receiver = receiver;
        if (connector != null)
            WssConnector.sendMessage(JsonParser.getWssMessage(message));
        else Toast.makeText(getContext(), "Ошибка отправки сообщения", Toast.LENGTH_SHORT).show();
        //send
        //save to DB
        newMessage(message);
        return true;
    }

    @Override
    public void onIncomingMessage(String receiverId, String incomingMessage) {
        if (receiverId != null && incomingMessage != null) {
            Message newMessage = new Message(incomingMessage);
            newMessage.user.id = receiverId;
            newMessage(newMessage);
        }
    }

    private List<User> getChatUsers(int id) {
        List<UsersChatsTable> mLinksUsers = mPocketDao.getLinks();
        List<User> users = new ArrayList<>();

        for (int i = 0; i < mLinksUsers.size(); i++) {
            if (mLinksUsers.get(i).getChatId() == id) {
                users.add(new User(mPocketDao.getOneContact(mLinksUsers.get(i).getUserId()).getUserName(), "", Integer.toString(mLinksUsers.get(i).getUserId())));
            }
        }
        return users;
    }
}
