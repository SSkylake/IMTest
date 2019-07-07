package com.example.imtest.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.imtest.entity.ChatHistoryEntity;

import java.util.List;

@Dao
public interface ChatDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ChatHistoryEntity entity);

    @Query("select * from ChatHistoryEntity where host=:host and theOther=:friend order by time")
    LiveData<List<ChatHistoryEntity>> getAll(String host,String friend);

//    @Query("select * from ChatHistoryEntity where host=:host and theOther=:friend order by time desc")
//    List<ChatHistoryEntity> getNewestChat(String host, String friend);

    @Query("update ChatHistoryEntity set state = :state where host = :host and theOther = :theOther and time=:time")
    void updateChatState(int state,String host,String theOther,long time);


}
