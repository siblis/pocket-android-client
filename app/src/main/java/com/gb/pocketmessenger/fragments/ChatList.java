package com.gb.pocketmessenger.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gb.pocketmessenger.ChatActivity;
import com.gb.pocketmessenger.R;
import com.gb.pocketmessenger.models.Dialog;
import com.gb.pocketmessenger.models.Message;
import com.gb.pocketmessenger.utils.ImgLoader;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import java.util.ArrayList;
import java.util.List;

public class ChatList extends Fragment implements DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog> {
    private List<Dialog> dialogs = new ArrayList<>();
    private DialogsList chats;
    DialogsListAdapter chatListAdapter;

    public static ChatList newInstance () {
        return new ChatList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        chats = view.findViewById(R.id.chatList);

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
