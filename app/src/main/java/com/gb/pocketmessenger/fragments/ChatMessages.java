package com.gb.pocketmessenger.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gb.pocketmessenger.Network.ConnectionToServer;
import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.models.Message;
import com.gb.pocketmessenger.models.PocketMessage;
import com.gb.pocketmessenger.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.concurrent.ExecutionException;

import static com.gb.pocketmessenger.fragments.RegisterFragment.POCKET_MESSENGER_URL;

public class ChatMessages extends Fragment implements MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageInput.TypingListener {

    public static final String WSS_POCKETMSG = "wss://pocketmsg.ru:8888/v1/ws/";
    protected ImageLoader imageLoader;
    private MessagesList messages;
    private MessagesListAdapter<Message> messageAdapter;
    private final String senderId = "0";    //TODO: get senderID
    private String login;
    private String password;


    public static ChatMessages newInstance(String dialogId) {
        ChatMessages myFragment = new ChatMessages();

//        Bundle args = new Bundle();
//        args.putString("login", login);
//        args.putString("password", password);
//        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        login = getArguments().getString("login", "");
//        password = getArguments().getString("password", "");
//        User newUser = new User(login, password);
//        ConnectionToServer connection = new ConnectionToServer("LOGIN", newUser);
//        connection.execute(POCKET_MESSENGER_URL);
//        try {
//            Toast.makeText(getContext(), connection.get(), Toast.LENGTH_SHORT).show();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        imageLoader = (imageView, url) -> Picasso.get().load(url).into(imageView);
        View view = inflater.inflate(R.layout.fragment_messages_list, container, false);
        messages = view.findViewById(R.id.messagesList);

        initAdapter();
        MessageInput input = view.findViewById(R.id.input);
        input.setInputListener((MessageInput.InputListener) this);
        input.setTypingListener(this);
        input.setAttachmentsListener((MessageInput.AttachmentsListener) this);

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

        return true;
    }

    private String getMessage(CharSequence input, String receiver) {
        PocketMessage message = new PocketMessage(receiver, input.toString());
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(message);
    }
}
