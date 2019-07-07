package com.example.imtest.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.imtest.entity.ChatHistoryEntity;
import com.example.imtest.entity.FriendEntity;

@Database(entities = {FriendEntity.class, ChatHistoryEntity.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {


    public abstract ChatListDao chatListDao();
    public abstract ChatDetailDao chatDetailDao();

    private static AppDatabase sInstance;
    private static final String databaseName = "appDatabase";


    public static AppDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context,AppDatabase.class,databaseName).build();
                }
            }
        }

        return sInstance;
    }




}
