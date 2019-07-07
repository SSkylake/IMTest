package com.example.imtest.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.imtest.BaseApplication;

import java.io.Serializable;
import java.util.Date;


@Entity(primaryKeys = {"theOther","currentName"})
public class FriendEntity   {


    @NonNull
    private String theOther;
    @NonNull
    private String currentName;

    private String recentChat;
    private long recentTime;
    private int nonReadingCount;

    @NonNull
    public String getTheOther() {
        return theOther;
    }

    public void setTheOther(@NonNull String theOther) {
        this.theOther = theOther;
    }

    public String getRecentChat() {
        return recentChat;
    }

    public void setRecentChat(String recentChat) {
        this.recentChat = recentChat;
    }

    public long getRecentTime() {
        return recentTime;
    }

    public void setRecentTime(long recentTime) {
        this.recentTime = recentTime;
    }

    public int getNonReadingCount() {
        return nonReadingCount;
    }

    public void setNonReadingCount(int nonReadingCount) {
        this.nonReadingCount = nonReadingCount;
    }


    @NonNull
    public String getCurrentName() {
        return currentName;
    }

    public FriendEntity(@NonNull String theOther, String recentChat, long recentTime, int nonReadingCount) {
        this.theOther = theOther;
        this.recentChat = recentChat;
        this.recentTime = recentTime;
        this.nonReadingCount = nonReadingCount;
        this.currentName = BaseApplication.getLoginUser();
    }

    public String getDate(){
        Date date = new Date(recentTime);
        return date.getHours()+":"+date.getMinutes();
    }

    public void setCurrentName(@NonNull String currentName) {
        this.currentName = currentName;
    }
}
