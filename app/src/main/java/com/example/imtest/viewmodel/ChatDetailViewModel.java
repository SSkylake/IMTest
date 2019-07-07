package com.example.imtest.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.imtest.BaseApplication;
import com.example.imtest.database.ChatListDao;
import com.example.imtest.entity.ChatHistoryEntity;
import com.example.imtest.database.ChatDetailDao;
import com.example.imtest.entity.TransportPayload;
import com.example.imtest.net.IHttpRequest;
import com.example.imtest.net.NetService;
import com.example.imtest.net.RetrofitFactory;
import com.example.imtest.net.StandardResponse;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ChatDetailViewModel extends ViewModel {
    private static final String TAG = "ChatDetailViewModel";

    private LiveData<List<ChatHistoryEntity>> historyList;

    private ChatDetailDao chatDetailDao = BaseApplication.getDatabase().chatDetailDao();
    private ChatListDao chatListDao = BaseApplication.getDatabase().chatListDao();

    public LiveData<List<ChatHistoryEntity>> getHistoryList(String who) {

        if (historyList == null) {
            historyList = chatDetailDao.getAll(BaseApplication.getLoginUser(), who);
            getHistoryListFromNet(who);
        }

        return historyList;
    }

    private void getHistoryListFromNet(String who) {
        Retrofit retrofit = RetrofitFactory.getInstance();
        IHttpRequest httpRequest = retrofit.create(IHttpRequest.class);
        Disposable d =
                httpRequest.getChatHistory(BaseApplication.getLoginUser(), who)
                        .subscribeOn(Schedulers.io())
                        .flatMap(new Function<List<ChatHistoryEntity>, ObservableSource<ChatHistoryEntity>>() {
                            @Override
                            public ObservableSource<ChatHistoryEntity> apply(List<ChatHistoryEntity> chatHistoryEntities) throws Exception {
                                return Observable.fromIterable(chatHistoryEntities);
                            }
                        })
                        .subscribe(new Consumer<ChatHistoryEntity>() {
                            @Override
                            public void accept(ChatHistoryEntity chatHistoryEntity) throws Exception {
                                chatHistoryEntity.setState(ChatHistoryEntity.Type.confirmed);
                                chatDetailDao.insert(chatHistoryEntity);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "accept: ", throwable);
                            }
                        });

    }

    public void sendMessage(final String who, final String content) {

        new Thread() {
            @Override
            public void run() {
                super.run();

                long time = System.currentTimeMillis();
                final ChatHistoryEntity entity = new ChatHistoryEntity(content, true, time, who);
                TransportPayload payload = new TransportPayload(1, BaseApplication.getLoginUser(), who, content);
                NetService.sendPayLoadWithConfirm(payload);
                chatDetailDao.insert(entity);
            }
        }.start();


    }

    public void notifyReadingDone(final String theOther) {


        Retrofit retrofit = RetrofitFactory.getInstance();
        IHttpRequest httpRequest = retrofit.create(IHttpRequest.class);
        Disposable dis = httpRequest.resetNonReadingCount(BaseApplication.getLoginUser(), theOther)
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<StandardResponse>() {
                    @Override
                    public void accept(StandardResponse standardResponse) throws Exception {
                        try {
                            chatListDao.setNonReadingNumZero(BaseApplication.getLoginUser(), theOther);
                        } catch (IllegalStateException e) {
                            Log.e(TAG, "notifyReadingDone: ", e);
                        }


                        Log.d(TAG, "notifyReadingDone: " + new Gson().toJson(standardResponse));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "notifyReadingDone: ", throwable);
                    }
                });


    }
}
