package com.gb.pocketmessenger;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.gb.pocketmessenger.DataBase.PocketDao;
import com.gb.pocketmessenger.fragments.AboutFragment;
import com.gb.pocketmessenger.fragments.ChatMessages;
import com.gb.pocketmessenger.fragments.MyProfileFragment;
import com.gb.pocketmessenger.fragments.SupportFragment;
import com.gb.pocketmessenger.fragments.TabsFragment;
import com.gb.pocketmessenger.services.PocketMessengerWssService;
import com.stfalcon.chatkit.commons.ImageLoader;

import java.util.Objects;

import static com.gb.pocketmessenger.services.PocketMessengerWssService.TOKEN_INTENT;

public class ChatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public enum Tabs {
        Chat,
        Contacts
    }

    public static final String BACKSTACK_TAG = "BackStack_tag";
    private PocketDao mPocketDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mPocketDao = ((AppDelegate) getApplicationContext()).getPocketDatabase().getPocketDao();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, TabsFragment.newInstance(Tabs.Contacts)).commit();
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //TODO откладываем до лучших времен
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, TabsFragment.newInstance(Tabs.Chat));
        transaction.commit();

        // и сразу вызаваем фрагмент для написания сообщений
//        fab.setVisibility(View.INVISIBLE);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.container, ChatMessages.newInstance("0")).addToBackStack(BACKSTACK_TAG).commit();
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
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
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

    public static void setMessageScreen(String dialogId) {
        ChatMessages.newInstance(dialogId);
    }

    private void logout() {
        if (mPocketDao.getUser() != null)
            mPocketDao.deleteUser(mPocketDao.getUser());
        super.onBackPressed();

    }
}
