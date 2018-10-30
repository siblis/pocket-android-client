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
import com.gb.pocketmessenger.ChatActivity;
import com.gb.pocketmessenger.DataBase.ChatsTable;
import com.gb.pocketmessenger.DataBase.PocketDao;
import com.gb.pocketmessenger.DataBase.UsersChatsTable;
import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.models.Dialog;
import com.gb.pocketmessenger.models.Message;
import com.gb.pocketmessenger.models.User;
import com.gb.pocketmessenger.utils.ImgLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatList extends Fragment implements DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog>, ChatActivity.OnNewChatAdded {
    private List<Dialog> dialogs;
    private DialogsList chats;
    private DialogsListAdapter chatListAdapter;
    private PocketDao mPocketDao;
    private static final String TAG = "tar";

    public static ChatList newInstance() {
        return new ChatList();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((ChatActivity) getActivity()).setOnNewChatAddedListener(this);
        super.onCreate(savedInstanceState);
        mPocketDao = ((AppDelegate) Objects.requireNonNull(getActivity()).getApplicationContext()).getPocketDatabase().getPocketDao();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        chats = view.findViewById(R.id.chatList);

        List<UsersChatsTable> mLinks = mPocketDao.getLinks();
        for (int i = 0; i < mLinks.size(); i++) {
            Log.d(TAG, "Links: " + mLinks.get(i).getId() + " user: " + mLinks.get(i).getUserId() + " chat: " + mLinks.get(i).getChatId());
        }

        getDialogList();
        setChatAdapter();
        return view;
    }

    private void setChatAdapter() {

        chatListAdapter = new DialogsListAdapter<>(new ImgLoader());
        chatListAdapter.setItems(dialogs);
        chatListAdapter.setOnDialogClickListener(dialog -> ((ChatActivity) getActivity()).setMessageScreen(dialog.getId()));
        chats.setAdapter(chatListAdapter);
    }

    private List<Dialog> getDialogList(){
        List<ChatsTable> mChatsList = mPocketDao.getChats();
        dialogs = new ArrayList<>();
        for (int i = 0; i < mChatsList.size(); i++) {
            //TODO Метод PocketDao.getUsersFromChat() работает не верно! Разобраться почему. Пока не использовать в коде. Пример использования в логе ниже. Выдает неверные данные.
            for (int k = 0; k < mPocketDao.getUsersFromChat(i).size(); k++) {
                Log.d(TAG, "Users from chat: " + mPocketDao.getUsersFromChat(i).get(k).getId() + " name: " + mPocketDao.getUsersFromChat(i).get(k).getUserName());
            }
            dialogs.add(new Dialog(String.valueOf(mChatsList.get(i).getId()), mChatsList.get(i).getChatName(), getChatUsers(mChatsList.get(i).getId())));

        }
        return dialogs;
    }

    private void onNewMessage(String dialogId, Message message) {
        boolean isUpdated = chatListAdapter.updateDialogWithMessage(dialogId, message);
    }

    private List<User> getChatUsers(int id) {
        //List<ContactsTable> contactsTables = mPocketDao.getUsersFromChat(id);
        List<UsersChatsTable> mLinksUsers = mPocketDao.getLinks();
        List<User> users = new ArrayList<>();

        for (int i = 0; i < mLinksUsers.size(); i++) {
            if (mLinksUsers.get(i).getChatId() == id) {
                Log.d(TAG, "getChatUsers: " + Integer.toString(mLinksUsers.get(i).getUserId()) + " name: " + mPocketDao.getOneContact(mLinksUsers.get(i).getUserId()).getUserName());
                //TODO Почему не добавляет User???
                //users.add(new User("test", "123", String.valueOf(40)));
                users.add(new User(mPocketDao.getOneContact(mLinksUsers.get(i).getUserId()).getUserName(), "", Integer.toString(mLinksUsers.get(i).getUserId())));
            }
        }
/*
        for (ContactsTable e : contactsTables) {
            users.add(new User(e.getUserName(), "", Integer.toString(e.getId())));
        }
*/
        return users;
    }

    private void onNewDialog(Dialog dialog) {
        chatListAdapter.addItem(dialog);
    }

    @Override
    public void onDialogClick(Dialog dialog) {
        Toast.makeText(getContext(), "NJCNC", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogLongClick(Dialog dialog) {

    }

    @Override
    public void onNewChatAdded() {
        getDialogList();
        setChatAdapter();
    }
}
