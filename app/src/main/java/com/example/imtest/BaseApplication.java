package com.example.imtest;

import android.app.Application;

import com.example.imtest.database.AppDatabase;

import java.net.DatagramSocket;
import java.net.SocketException;

public class BaseApplication extends Application {


    private static AppDatabase database;
    private static DatagramSocket socket;
    private static String loginUser;
    public static final String HOST = "148.70.26.120";
    private static int PORT = -1;

    public static void setLoginUser(String user) {
        loginUser = user;
    }


    public static void setPort(int PORT) {
        BaseApplication.PORT = PORT;
    }

    public static int getPort() throws IllegalStateException{
        if(PORT == -1) throw new IllegalStateException("Remote port undefined.");
        return PORT;
    }

    public static String getLoginUser() throws IllegalStateException{
        if(loginUser == null) throw new IllegalStateException("No User logged.");
        return loginUser;
    }

    public static AppDatabase getDatabase(){
        return database;
    }

    public static DatagramSocket getSocket(){
        if(socket == null){
            try {
                socket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        return socket;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getInstance(getApplicationContext());
    }
}
