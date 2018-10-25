package com.gb.pocketmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gb.pocketmessenger.DataBase.ChatsTable;
import com.gb.pocketmessenger.DataBase.ContactsTable;
import com.gb.pocketmessenger.DataBase.PocketDao;
import com.gb.pocketmessenger.DataBase.UsersChatsTable;
import com.gb.pocketmessenger.Network.RestUtils;
import com.gb.pocketmessenger.fragments.AboutFragment;
import com.gb.pocketmessenger.fragments.ChatMessages;
import com.gb.pocketmessenger.fragments.MyProfileFragment;
import com.gb.pocketmessenger.fragments.SupportFragment;
import com.gb.pocketmessenger.fragments.TabsFragment;
import com.gb.pocketmessenger.models.PocketContact;
import com.gb.pocketmessenger.models.User;
import com.gb.pocketmessenger.utils.Correct;
import com.gb.pocketmessenger.utils.JsonParser;

import java.util.Date;
import java.util.Calendar;
import java.util.List;


public class ChatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public enum Tabs {
        Chat,
        Contacts
    }

    public interface OnContactAdded {
        void onContactAdded();
    }
    public interface OnNewChatAdded {
        void onNewChatAdded();
    }

    public static final String BACKSTACK_TAG = "BackStack_tag";
    private PocketDao mPocketDao;
    private static final String TAG = "tar";
    private OnContactAdded contactAddListener;
    private OnNewChatAdded chatAddListener;
    private FragmentManager fragmentManager;
    private FloatingActionButton fab;

    public void setOnContactAddListener(OnContactAdded listener) {
        this.contactAddListener = listener;
    }
    public void setOnNewChatAddedListener(OnNewChatAdded listener) {
        this.chatAddListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mPocketDao = ((AppDelegate) getApplicationContext()).getPocketDatabase().getPocketDao();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            String usersJson = RestUtils.getContactList(mPocketDao);
            List<PocketContact> allContacts = JsonParser.parseUsersMap(usersJson);
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //TODO откладываем до лучших времен
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, TabsFragment.newInstance(Tabs.Chat));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.action_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_add_contact:
                addContact();
                //ContactList cl = new ContactList();
                //cl.onRefresh();
                return true;

            case R.id.action_add_chat:
                addChatRoom();
                return true;
        }

        return false;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_my_profile:
                fragmentClass = MyProfileFragment.class;
                break;
            case R.id.nav_contacts:
                fragmentClass = TabsFragment.class;
                break;
            case R.id.nav_support:
                fragmentClass = SupportFragment.class;
                break;
            case R.id.nav_about:
                fragmentClass = AboutFragment.class;
                break;
            case R.id.nav_logout:
                logout();
                return true;
        }

        try {
            if (fragmentClass == TabsFragment.class) {
                fragment = TabsFragment.newInstance(Tabs.Contacts);
            } else {
                fragment = (Fragment) (fragmentClass != null ? fragmentClass.newInstance() : null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(BACKSTACK_TAG).commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setMessageScreen(String dialogId) {
        fab.setVisibility(View.GONE);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ChatMessages fragment = ChatMessages.newInstance(dialogId);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void logout() {
        if (mPocketDao.getUser() != null)
            mPocketDao.deleteUser(mPocketDao.getUser());
        super.onBackPressed();

    }


    private void addContact() {
        AlertDialog.Builder mContactBuilder = new AlertDialog.Builder(ChatActivity.this);
        View mContactAddView = getLayoutInflater().inflate(R.layout.dialog_add_contact, null);
        EditText mEmail = mContactAddView.findViewById(R.id.et_email);
        Button mAddContactBtn = mContactAddView.findViewById(R.id.btn_add_contact);
        mContactBuilder.setView(mContactAddView);
        AlertDialog addContactDialog = mContactBuilder.create();

        mAddContactBtn.setOnClickListener(v -> {
            String email = mEmail.getText().toString();
            if (!email.isEmpty() && Correct.isValidEmail(email)) {
                //TODO Сделать поиск контакта на сервере! (ID, Name, Email)
                String newUserJSON = RestUtils.addContact(email, mPocketDao);

                if (!TextUtils.isEmpty(newUserJSON) && !newUserJSON.equals("User does not exists") && !newUserJSON.equals("Contact already in list")) {
                    Log.d(TAG, "addContact: " + newUserJSON);
                    User newContact = JsonParser.parseUser(newUserJSON);
                    mPocketDao.insertContact(new ContactsTable(Integer.parseInt(newContact.getId()), newContact.getName(), mEmail.getText().toString(), false));
                    Toast.makeText(ChatActivity.this, R.string.contact_added + " : " + newUserJSON, Toast.LENGTH_SHORT).show();

                    if (contactAddListener != null) contactAddListener.onContactAdded();

                    addContactDialog.dismiss();
                } else
                    Toast.makeText(ChatActivity.this, newUserJSON, Toast.LENGTH_SHORT).show();

            } else {
                Log.d(TAG, "Email is Empty!");

                Toast.makeText(ChatActivity.this, R.string.empty_email, Toast.LENGTH_SHORT).show();
            }
        });

        addContactDialog.show();
    }

    private void addChatRoom() {
        AlertDialog.Builder mChatRoomBuilder = new AlertDialog.Builder(ChatActivity.this);
        View mChatRoomAddView = getLayoutInflater().inflate(R.layout.dialog_add_chatroom, null);
        EditText mChatRoomName = mChatRoomAddView.findViewById(R.id.et_chatroom);
        Button mAddChatRoomBtn = mChatRoomAddView.findViewById(R.id.btn_add_chatroom);
        mChatRoomBuilder.setView(mChatRoomAddView);
        AlertDialog addChatRoomDialog = mChatRoomBuilder.create();

        mAddChatRoomBtn.setOnClickListener(v -> {
            if (!mChatRoomName.getText().toString().isEmpty()) {
                Date currentTime = Calendar.getInstance().getTime();
                String time = (currentTime.getHours() + 1) + ":"
                        + (currentTime.getMinutes() + 1) + ":"
                        + (currentTime.getSeconds() + 1) + " "
                        + currentTime.getDate() + "."
                        + (currentTime.getMonth() + 1) + "."
                        + (currentTime.getYear() + 1900);
                Log.d(TAG, "Time: " + time);
                mPocketDao.insertChat(new ChatsTable(mPocketDao.getChats().size(), mChatRoomName.getText().toString(), time));
                for (int i = 0; i < mPocketDao.getChats().size(); i++) {
                    Log.d(TAG, "addChatRoom: " + mPocketDao.getChats().get(i).getId() + " name: " + mPocketDao.getChats().get(i).getChatName());
                }
                mPocketDao.setOneLinkUserToChat(new UsersChatsTable(mPocketDao.getLinks().size(), mPocketDao.getUser().getServerUserId(), (mPocketDao.getChats().size() - 1), time));

                Log.d(TAG, "addLink: " + mPocketDao.getLinks().size() + " | " + mPocketDao.getUser().getId() + " | chats.size: " + (mPocketDao.getChats().size() - 1) + " | " + time
                );

                for (int i = 0; i < mPocketDao.getLinks().size(); i++) {
                    Log.d(TAG, "Links: " + mPocketDao.getLinks().get(i).getId() + " user: " + mPocketDao.getLinks().get(i).getUserId() + " chat id:" + mPocketDao.getLinks().get(i).getChatId());
                }

                Log.d(TAG, mChatRoomName.getText().toString());

                Toast.makeText(ChatActivity.this, "ChatRoom successfully created at: " + time, Toast.LENGTH_SHORT).show();
                if (contactAddListener != null) chatAddListener.onNewChatAdded();
                addChatRoomDialog.dismiss();
            } else {
                Log.d(TAG, "ChatRoom name is Empty!");
                Toast.makeText(ChatActivity.this, R.string.chatroom_name_empty, Toast.LENGTH_SHORT).show();
            }
        });

        addChatRoomDialog.show();
    }

}
