package com.gb.pocketmessenger.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gb.pocketmessenger.AppDelegate;
import com.gb.pocketmessenger.DataBase.ChatsTable;
import com.gb.pocketmessenger.DataBase.ContactsTable;
import com.gb.pocketmessenger.DataBase.PocketDao;
import com.gb.pocketmessenger.Holders.ChatHolder;
import com.gb.pocketmessenger.Holders.ContactHolder;
import com.gb.pocketmessenger.R;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatHolder> {

    private List<ChatsTable> mChatsList;
    private OnChatClickListener mListener;

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.holder_chat, parent, false);
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        holder.bind(mChatsList.get(position));
        holder.setListener(mListener);
    }

    @Override
    public int getItemCount() {
        return mChatsList.size();
    }

    public void addData(List<ChatsTable> chatsTables) {
        mChatsList = chatsTables;
        notifyDataSetChanged();
    }

    public void reload() {
        notifyDataSetChanged();
    }

    public void setListener(OnChatClickListener listener) {
        mListener = listener;
    }

    public interface OnChatClickListener {
        void onChatClick(Integer chatId);
    }
}
