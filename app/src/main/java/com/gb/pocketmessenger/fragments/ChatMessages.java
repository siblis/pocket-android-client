package com.gb.pocketmessenger.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.models.Message;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

public class ChatMessages extends Fragment implements MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageInput.TypingListener {

    protected ImageLoader imageLoader;
    private MessagesList messages;
    private MessagesListAdapter<Message> messageAdapter;
    private Message message;
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

        //TODO : тут отправляем сообщение на сервер и сохраняем в БД
        message = new Message(input.toString());
        message.user.id = senderId;
        //send
        //save to DB
        newMessage(message);
        return true;
    }

//    private String getMessage(CharSequence input, String receiver) {
//        PocketMessage message = new PocketMessage(receiver, input.toString());
//        GsonBuilder builder = new GsonBuilder();
//        Gson gson = builder.create();
//        return gson.toJson(message);
//    }
}
