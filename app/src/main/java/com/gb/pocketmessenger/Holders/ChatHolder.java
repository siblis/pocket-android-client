package com.gb.pocketmessenger.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gb.pocketmessenger.Adapters.ChatsAdapter;
import com.gb.pocketmessenger.DataBase.ChatsTable;
import com.gb.pocketmessenger.DataBase.ContactsTable;
import com.gb.pocketmessenger.R;

public class ChatHolder extends RecyclerView.ViewHolder {

    private TextView mName;
    private TextView mFrom;
    private TextView mUnread;
    private int chatId;

    public ChatHolder(View itemView) {
        super(itemView);
        mName = itemView.findViewById(R.id.tv_chat_name);
        mFrom = itemView.findViewById(R.id.tv_last_message);
        mFrom = itemView.findViewById(R.id.unread);
    }

    public void bind(ChatsTable chatsTable) {
        mName.setText(chatsTable.getChatName());
        //mFrom.setText();
        chatId = chatsTable.getId();
    }

    public void setListener(ChatsAdapter.OnChatClickListener listener) {
        itemView.setOnClickListener(v -> listener.onChatClick(chatId));
    }
}