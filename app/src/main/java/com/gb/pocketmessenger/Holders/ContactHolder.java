package com.gb.pocketmessenger.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gb.pocketmessenger.Adapters.ContactsAdapter;
import com.gb.pocketmessenger.DataBase.ContactsTable;
import com.gb.pocketmessenger.R;

public class ContactHolder extends RecyclerView.ViewHolder {

    private TextView mName;
    private TextView mEmail;
    private int userId;

    public ContactHolder(View itemView) {
        super(itemView);
        mName = itemView.findViewById(R.id.tv_contact_name);
        mEmail = itemView.findViewById(R.id.tv_contact_email);
    }

    public void bind(ContactsTable contactsTable) {
        mName.setText(contactsTable.getUserName());
        mEmail.setText(contactsTable.getEmail());
        userId = contactsTable.getId();
    }

    public void setListener(ContactsAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(v -> listener.onContactClick(userId));
    }
}
