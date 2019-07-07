package com.example.imtest.entity;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.gson.Gson;

import java.net.InetAddress;

public class TransportPayload {

    public interface Type{
        int HEART_PACKET = 0;
        int IM_TO_SERVER = 1;
        int IM_SERVER_FORWARD = 2;
        int IM_SERVER_CONFIRM = 3;
    }
    /**
     * command for
     * 服务端：
     * 0:客户端向服务端发送的心跳包，服务器收到后更新路由表
     * 1:客户端企图向另一个客户端发送即时消息，服务端收到后查询路由表并转发，同时在聊天历史记录表中增加一条记录。
     *
     *
     * 客户端：
     * 2:客户端收到服务器转发另一台客户端发来的即时消息，该客户端在本地聊天记录数据库中增加一条内容。
     */


    private int command;
    private String from;
    private String to;
    private String content;
    private int port;
    private String fromAddress;
    private long time;




    public static TransportPayload getInstance(byte[] bytes,int length){
        return new Gson().fromJson(new String(bytes,0,length),TransportPayload.class);
    }


    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public TransportPayload(int command, String from, String to, String content) {
        this.command = command;
        this.from = from;
        this.to = to;
        this.content = content;
        this.port=0;
        this.fromAddress = null;
        time = System.currentTimeMillis();

    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public byte[] getBytes(){
        return new Gson().toJson(this).getBytes();
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TransportPayload){
            return time == ((TransportPayload) obj).time&&
                    this.from.equals(((TransportPayload) obj).from)&&
                    this.to.equals(((TransportPayload) obj).to);
        }

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int hashCode() {

        int result = 17;

        result = 31*result+(from==null?0:from.hashCode());
        result = 31*result+(to==null?0:to.hashCode());
        result = 31*result+Long.hashCode(time);

        return result;
    }
}
