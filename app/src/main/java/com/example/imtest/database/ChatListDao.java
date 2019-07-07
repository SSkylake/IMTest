package com.example.imtest.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.imtest.entity.FriendEntity;

import java.util.List;

@Dao
public interface ChatListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FriendEntity entity);

    @Query("select * from FriendEntity where currentName = :host")
    LiveData<List<FriendEntity>> getAllFriends(String host);



    @Query("select nonReadingCount from FriendEntity where currentName = :host and theOther=:theOther")
    int getNonReadingCount(String host,String theOther);

    @Update
    void updateRecentChat(FriendEntity entity);

    @Query("update FriendEntity set nonReadingCount = 0 where currentName = :host and theOther = :theOther")
    void setNonReadingNumZero(String host,String theOther);


}
