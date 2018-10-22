package com.gb.pocketmessenger.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gb.pocketmessenger.AppDelegate;
import com.gb.pocketmessenger.DataBase.ContactsTable;
import com.gb.pocketmessenger.DataBase.PocketDao;
import com.gb.pocketmessenger.Holders.ContactHolder;
import com.gb.pocketmessenger.R;

import java.util.List;
import java.util.Objects;

public class ContactsAdapter extends RecyclerView.Adapter<ContactHolder> {

    private List<ContactsTable> mContactsList;

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.holder_contact, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, int position) {
        holder.bind(mContactsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mContactsList.size();
    }

    public void addData(List<ContactsTable> contactsTable) {
        mContactsList = contactsTable;
        notifyDataSetChanged();
    }

    public void reload() {
        notifyDataSetChanged();
    }
}
