package com.example.imtest.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.imtest.BaseApplication;

import java.util.Date;

@Entity(primaryKeys = {"host","theOther","time"})
public class ChatHistoryEntity {



    @NonNull
    private String host;
    @NonNull
    private String theOther;
    private String text;
    private boolean sendOrReceive;
    private long time;
    private int state;

    @Ignore
    public ChatHistoryEntity(@NonNull String host, @NonNull String theOther, long time) {
        this.host = host;
        this.theOther = theOther;
        this.time = time;
    }

    public ChatHistoryEntity(String text, boolean sendOrReceive, long time, @NonNull String theOther) {
        this.text = text;
        this.sendOrReceive = sendOrReceive;
        this.time = time;
        this.theOther = theOther;
        this.host = BaseApplication.getLoginUser();
        this.state = Type.unConfirmed;
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @NonNull
    public String getTheOther() {
        return theOther;
    }

    public void setTheOther(@NonNull String theOther) {
        this.theOther = theOther;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSendOrReceive() {
        return sendOrReceive;
    }

    public void setSendOrReceive(boolean sendOrReceive) {
        this.sendOrReceive = sendOrReceive;
    }

    public String getDate(){
        Date date = new Date(time);
        return date.getHours()+":"+date.getMinutes();
    }

    @NonNull
    public String getHost() {
        return host;
    }

    public long getTime() {
        return time;
    }

    public void setHost(@NonNull String host) {
        this.host = host;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ChatHistoryEntity){
            ChatHistoryEntity entity = (ChatHistoryEntity) obj;
            return entity.host.equals(host)&&
                    entity.time == time&&
                    entity.theOther.equals(theOther);
        }

        return false;

    }

    public interface Type{
        int unConfirmed = 0;
        int confirmed = 1;
        int failed = 2;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
