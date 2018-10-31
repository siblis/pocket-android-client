package com.gb.pocketmessenger.DataBase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

@Dao
public interface PocketDao {

    //-----------UserTable----------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserTable user);

    @Delete
    void deleteUser(UserTable userTable);

    @Query("SELECT * FROM usertable WHERE id = 0")
    UserTable getUser();

    //-----------ChatsTable----------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChats(List<ChatsTable> chats);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChat(ChatsTable chat);

    @Query("SELECT * FROM chatstable")
    List<ChatsTable> getChats();

    @Query("SELECT * FROM chatstable")
    Cursor getChatsCursor();

    @Query("SELECT * FROM chatstable WHERE id = :chatId")
    Cursor getChatWithIdCursor(int chatId);

    @Query("SELECT * FROM chatstable WHERE chat_name = :chatName")
    ChatsTable getChatWithName(String chatName);

    //-----------UsersChatsTable-------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setLinksUsersChats(List<UsersChatsTable> linksUsersChats);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setOneLinkUserToChat(UsersChatsTable linkUsersChats);

    @Query("SELECT * FROM userschatstable")
    List<UsersChatsTable> getLinks();

    @Query("SELECT * FROM contactstable INNER JOIN userschatstable ON contactstable.id WHERE chat_id = :chatId")
    List<ContactsTable> getUsersFromChat(int chatId);

    @Delete
    void deleteChat(ChatsTable chatsTable);

    //-----------ContactsTable----------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertContacts(List<ContactsTable> contacts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertContact(ContactsTable contact);

    @Query("SELECT * FROM contactstable WHERE id = :UserId")
    ContactsTable getOneContact(int UserId);

    @Query("SELECT * FROM contactstable")
    List<ContactsTable> getContacts();

    @Delete
    void deleteContact(ContactsTable contact);

    //-----------MessagesTable---------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessages(List<MessagesTable> messages);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessage(MessagesTable message);

    @Query("SELECT * FROM messagestable WHERE to_id = :chatOrUserId")
    List<MessagesTable> getMessagesToId(int chatOrUserId);

    @Query("SELECT * FROM messagestable WHERE to_id = :chatOrUserId AND status = :status")
    List<MessagesTable> getMessagesToIdWithStatus(int chatOrUserId, String status);

    @Query("SELECT * FROM messagestable")
    List<MessagesTable> getMessages();

    @Delete
    void deleteMessage(MessagesTable message);

}
