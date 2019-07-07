package com.example.imtest.net;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.imtest.BaseApplication;
import com.example.imtest.entity.ChatHistoryEntity;
import com.example.imtest.entity.FriendEntity;
import com.example.imtest.database.ChatDetailDao;
import com.example.imtest.database.ChatListDao;
import com.example.imtest.entity.TransportPayload;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NetService extends Service {
    private static final String TAG = "NetService";

    private static ChatListDao chatListDao;
    private static ChatDetailDao chatDetailDao;
    private DatagramSocket socket;
    private static BlockingQueue<DatagramPacket> sendQueue;
    private static BlockingQueue<DatagramPacket> receiveQueue;
    private static Set<TransportPayload> packetExpectToConfirm;

    private static final int LENGTH = 1024;
    private static final int MAX_SEND_NUM = 128;
    private static final int MAX_RECEIVE_NUM = 128;
    private static final int RETRY_TIME = 3;
    private static final int RETRY_INTERVAL = 3 * 1000;


    private void sendHeartPacket() {
        Timer timer = new Timer();
        try {
            final TransportPayload payload =
                    new TransportPayload(TransportPayload.Type.HEART_PACKET, BaseApplication.getLoginUser(), null, null);
            byte[] bytes = payload.getBytes();
            final DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length, InetAddress.getByName(BaseApplication.HOST), BaseApplication.getPort());
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        payload.setTime(System.currentTimeMillis());
                        packet.setData(payload.getBytes());
                        sendQueue.put(packet);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }, 0, 30 * 1000);
        } catch (UnknownHostException e) {
            Log.e(TAG, "sendHeartPacket: ", e);
        } catch (IllegalStateException e) {
            Log.e(TAG, "sendHeartPacket: ", e);
        }

    }


    @Override
    public void onCreate() {
        super.onCreate();

        chatListDao = BaseApplication.getDatabase().chatListDao();
        chatDetailDao = BaseApplication.getDatabase().chatDetailDao();
        socket = BaseApplication.getSocket();
        sendQueue = new LinkedBlockingQueue<>(MAX_SEND_NUM);
        receiveQueue = new LinkedBlockingQueue<>(MAX_RECEIVE_NUM);
        packetExpectToConfirm = new HashSet<>();


        startReceiveThread();
        startHandlerThread();
        startSendThread();
        sendHeartPacket();


    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new IllegalStateException("Bind not supported!");
    }

    private void startSendThread() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        DatagramPacket packet = sendQueue.take();
                        socket.send(packet);
                        Log.d(TAG, "sender: a message is sending.");
                    } catch (IOException e) {
                        Log.e(TAG, "Send thread: ", e);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Send thread: ", e);
                    }
                }
            }
        }.start();
    }


    private void startReceiveThread() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        DatagramPacket packet = new DatagramPacket(new byte[LENGTH], LENGTH);
                        socket.receive(packet);
                        receiveQueue.put(packet);


                    } catch (IOException e) {
                        Log.e(TAG, "Receive thread:", e);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Receive thread:", e);
                    }
                }
            }
        }.start();
    }

    private void startHandlerThread() {

        new Thread() {
            @Override
            public void run() {
                super.run();

                while (true) {
                    try {
                        DatagramPacket packet = receiveQueue.take();
                        final TransportPayload payload = TransportPayload.getInstance(packet.getData(), packet.getLength());

                        Log.d(TAG, "handling a message:"+new Gson().toJson(payload));
                        switch (payload.getCommand()) {
                            case TransportPayload.Type.IM_SERVER_FORWARD:

                                ChatHistoryEntity historyEntity = new ChatHistoryEntity(payload.getContent(), false, payload.getTime(), payload.getFrom());
                                historyEntity.setState(ChatHistoryEntity.Type.confirmed);
                                chatDetailDao.insert(historyEntity);
                                int nonReadingCount = chatListDao.getNonReadingCount(payload.getTo(),payload.getFrom());
                                FriendEntity entity = new FriendEntity(payload.getFrom(),payload.getContent(),payload.getTime(),nonReadingCount+1);
                                chatListDao.updateRecentChat(entity);
                                break;
                            case TransportPayload.Type.IM_SERVER_CONFIRM:
                                Log.d(TAG, "hashSet size before remove:"+packetExpectToConfirm.size());
                                packetExpectToConfirm.remove(payload);
                                Log.d(TAG, "hashSet size after remove:"+packetExpectToConfirm.size());

                                chatDetailDao.updateChatState(
                                        ChatHistoryEntity.Type.confirmed,
                                        BaseApplication.getLoginUser(),
                                        payload.getTo(),
                                        payload.getTime());
                                Log.d(TAG, "Receiving a confirm packet");


                                break;

                            default:
                        }
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Handler thread:", e);
                    }

                }

            }
        }.start();
    }


    public static void sendPayLoad(final TransportPayload payload) {
        Log.d(TAG, "sendPayLoad: "+new Gson().toJson(payload));
        try {
            byte[] bytes = payload.getBytes();
            DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length, InetAddress.getByName(BaseApplication.HOST), BaseApplication.getPort());
            sendQueue.put(packet);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void sendPayLoadWithConfirm(final TransportPayload payload) {
        sendPayLoad(payload);
        packetExpectToConfirm.add(payload);

        new Thread(){
            boolean isConfirmed = false;
            @Override
            public void run() {
                super.run();
                try{
                    for (int i = 0; i < RETRY_TIME; i++) {
                        Thread.sleep(RETRY_INTERVAL);
                        if(!isConfirmed) {
                            if (packetExpectToConfirm.contains(payload)) {
                                //重发
                                Log.d(TAG, "Send retrying.");
                                NetService.sendPayLoad(payload);
                            } else {
                                isConfirmed = true;
                                return;
                            }
                        }
                    }

                    //todo 修改为未送达
                    chatDetailDao.updateChatState(
                            ChatHistoryEntity.Type.failed,
                            BaseApplication.getLoginUser(),
                            payload.getTo(),
                            payload.getTime());
                    Log.e(TAG, "Send failed after retry");

                } catch (InterruptedException e) {
                    Log.e(TAG, "sending confirm error", e);
                }

            }
        }.start();



    }
}
