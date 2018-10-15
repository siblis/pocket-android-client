package com.gb.pocketmessenger.DataBase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

public interface PocketDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(List<UserTable> users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChats(List<ChatsTable> chats);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertContacts(List<ContactsTable> contacts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessages(List<MessagesTable> messages);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setLinksUsersChats(List<UsersChatsTable> linksUsersChats);

    @Query("SELECT * FROM chatstable")
    List<ChatsTable> getChats();

    @Query("SELECT * FROM chatstable")
    Cursor getChatsCursor();

    @Query("SELECT * FROM chatstable WHERE id = :chatId")
    Cursor getChatWithIdCursor(int chatId);

    @Delete
    void deleteChat(ChatsTable chatsTable);

    @Query("SELECT * FROM contactstable INNER JOIN userschatstable ON contactstable.id WHERE chat_id = :chatId")
    List<ContactsTable> getUsersFromChat(int chatId);

    @Query("SELECT * FROM messagestable WHERE to_id = :chatOrUserId")
    List<ContactsTable> getMessagesToId(int chatOrUserId);

    @Delete
    void deleteContact(ContactsTable contactsTable);

    @Delete
    void deleteMessage(MessagesTable messagesTable);

    @Query("SELECT * FROM contactstable")
    List<ContactsTable> getContacts();

    


}
