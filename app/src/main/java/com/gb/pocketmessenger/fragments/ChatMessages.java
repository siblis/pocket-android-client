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
import com.gb.pocketmessenger.DataBase.MessagesTable;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
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
    private static final String TAG = "tar";


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
        Log.d(TAG, "onCreate: " + dialogId);
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

        getMessagesFromDB();

        return view;
    }

    public void newMessage(Message message) {
        messageAdapter.addToStart(message, true);

        Log.d(TAG, "-------------------------");
        for (int i = 0; i < mPocketDao.getMessages().size(); i++) {
            Log.d(TAG, "Message " + i + ": " + mPocketDao.getMessages().get(i).getMessage() +
                    " FROM: " + mPocketDao.getMessages().get(i).getFromId() +
                    " TO:" + mPocketDao.getMessages().get(i).getToId() +
                    " DATE: " + mPocketDao.getMessages().get(i).getDate() +
                    " STATUS: " + mPocketDao.getMessages().get(i).getStatus());

        }
    }

    public void getMessagesFromDB() {
        List<MessagesTable> messagesList = mPocketDao.getMessages();
        Date thedate;


        for (int i = 0; i < mPocketDao.getMessages().size(); i++) {

            if (messagesList.get(i).getChatId() == Integer.valueOf(dialogId)) {

                if (messagesList.get(i).getFromId() != mPocketDao.getUser().getServerUserId()) {
                    message = new Message(messagesList.get(i).getMessage());
                    message.user.id = String.valueOf(messagesList.get(i).getFromId());
                    message.receiver = String.valueOf(messagesList.get(i).getToId());

                    //TODO отпарсить String messagesList.get(i).getDate() в формат Date
                    try {
                        thedate = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(messagesList.get(i).getDate());
                        Log.d(TAG, "------ Date: " + messagesList.get(i).getDate() + " / " + thedate);
                        message.setCreatedAt(thedate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    messageAdapter.addToStart(message, true);
                } else {
                    message = new Message(messagesList.get(i).getMessage());
                    message.user.id = senderId;
                    message.receiver = String.valueOf(messagesList.get(i).getToId());

                    try {
                        thedate = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(messagesList.get(i).getDate());
                        message.setCreatedAt(thedate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    messageAdapter.addToStart(message, true);
                }
            }

        }

        // В логах список сообщений из базы
        Log.d(TAG, "-------------------------");
        for (int i = 0; i < mPocketDao.getMessages().size(); i++) {
            User userTo = new User(mPocketDao.getOneContact(messagesList.get(i).getFromId()).getUserName(), "",
                    mPocketDao.getOneContact(messagesList.get(i).getFromId()).getEmail(), "",
                    String.valueOf(messagesList.get(i).getFromId()));
            Log.d(TAG, "Message " + i + ": " + messagesList.get(i).getMessage() +
                    " ID: " + String.valueOf(messagesList.get(i).getId()) +
                    " TO: " + userTo.getId() + "/" + userTo.geteMail() +
                    //" TO: " + mPocketDao.getOneContact(messagesList.get(i).getFromId()).getEmail() +
                    " DATE: " + messagesList.get(i).getDate() +
                    " STATUS: " + String.valueOf(messagesList.get(i).getStatus()));

        }
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

        Log.d(TAG, "onSubmit: FROM = " + mPocketDao.getUser().getServerUserId() + " TO = " + Integer.valueOf(receiver) + " TEXT = " + input.toString());

        mPocketDao.insertMessage(new MessagesTable(mPocketDao.getMessages().size(),
                mPocketDao.getUser().getServerUserId(),
                Integer.valueOf(receiver),
                input.toString(),
                String.valueOf(new Date()),
                Integer.valueOf(dialogId), 0));

        newMessage(message);

        return true;
    }

    @Override
    public void onIncomingMessage(String receiverId, String incomingMessage) {
        if (receiverId != null && incomingMessage != null) {
            Message newMessage = new Message(incomingMessage);
            newMessage.user.id = receiverId;

            Log.d(TAG, "onSubmit: FROM = " + Integer.valueOf(receiverId) + " TO = " + mPocketDao.getUser().getServerUserId() + " TEXT = " + incomingMessage);
            mPocketDao.insertMessage(new MessagesTable(mPocketDao.getMessages().size(),
                    Integer.valueOf(receiverId),
                    mPocketDao.getUser().getServerUserId(),
                    incomingMessage,
                    String.valueOf(new Date()),
                    Integer.valueOf(dialogId), 0));

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
