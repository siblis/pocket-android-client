package com.gb.pocketmessenger.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gb.pocketmessenger.AppDelegate;
import com.gb.pocketmessenger.ChatActivity;
import com.gb.pocketmessenger.DataBase.ChatsTable;
import com.gb.pocketmessenger.DataBase.ContactsTable;
import com.gb.pocketmessenger.DataBase.PocketDao;
import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.models.Dialog;
import com.gb.pocketmessenger.models.Message;
import com.gb.pocketmessenger.models.User;
import com.gb.pocketmessenger.utils.ImgLoader;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatList extends Fragment implements DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog> {
    private List<Dialog> dialogs = new ArrayList<>();
    private DialogsList chats;
    DialogsListAdapter chatListAdapter;
    private PocketDao mPocketDao;

    public static ChatList newInstance () {
        return new ChatList();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPocketDao = ((AppDelegate) Objects.requireNonNull(getActivity()).getApplicationContext()).getPocketDatabase().getPocketDao();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ChatActivity.setMessageScreen("0");

        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        chats = view.findViewById(R.id.chatList);

        Cursor chatTable = mPocketDao.getChatsCursor();

        while (chatTable.moveToNext()) {
            dialogs.add(new Dialog(chatTable.getString(chatTable.getColumnIndex("id")),
                    chatTable.getString(chatTable.getColumnIndex("chat_name")),
                    getChatUsers(chatTable.getInt(chatTable.getColumnIndex("id"))
                    )));

        }
        chatTable.close();

        chatListAdapter = new DialogsListAdapter<>(new ImgLoader());

        chatListAdapter.setItems(dialogs);

        chatListAdapter.setOnDialogClickListener(new DialogsListAdapter.OnDialogClickListener() {
            @Override
            public void onDialogClick(IDialog dialog) {
                ChatActivity.setMessageScreen(dialog.getId());
            }
        });

        chats.setAdapter(chatListAdapter);

        return view;
    }

    private void onNewMessage(String dialogId, Message message) {
        boolean isUpdated = chatListAdapter.updateDialogWithMessage(dialogId, message);

    }

    private List<User> getChatUsers (int id) {
        List<ContactsTable> contactsTables = mPocketDao.getUsersFromChat(id);
        List<User> users = null;

        for(ContactsTable e: contactsTables) {
            users.add(new User(e.getUserName(), "", Integer.toString(e.getId())));
        }

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
}
