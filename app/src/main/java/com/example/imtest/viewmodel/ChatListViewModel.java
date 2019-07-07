package com.example.imtest.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.imtest.BaseApplication;
import com.example.imtest.entity.ChatHistoryEntity;
import com.example.imtest.entity.FriendEntity;
import com.example.imtest.database.ChatListDao;
import com.example.imtest.net.IHttpRequest;
import com.example.imtest.net.RetrofitFactory;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ChatListViewModel extends ViewModel {

    private static final String TAG = "ChatListViewModel";

    private LiveData<List<FriendEntity>> chatList;

    private ChatListDao chatListDao = BaseApplication.getDatabase().chatListDao();

    public LiveData<List<FriendEntity>> getChatList() {
        if(chatList == null){
            chatList = chatListDao.getAllFriends(BaseApplication.getLoginUser());
            getChatListFromNet();

        }

        return chatList;
    }

    private void getChatListFromNet(){
        Retrofit retrofit = RetrofitFactory.getInstance();
        IHttpRequest request = retrofit.create(IHttpRequest.class);
        Disposable d =
                request.getFriendList(BaseApplication.getLoginUser())

                        .flatMap(new Function<List<FriendEntity>, ObservableSource<FriendEntity>>() {
                            @Override
                            public ObservableSource<FriendEntity> apply(List<FriendEntity> friendEntities) throws Exception {
                                return Observable.fromIterable(friendEntities);
                            }
                        })
//                        .map(new Function<FriendEntity, FriendEntity>() {
//                            @Override
//                            public FriendEntity apply(FriendEntity friendEntity) throws Exception {
//                                Log.d(TAG, "init friend list: "+new Gson().toJson(friendEntity));
//
//                                List<ChatHistoryEntity> entities = BaseApplication.getDatabase().chatDetailDao()
//                                        .getNewestChat(friendEntity.getCurrentName(),friendEntity.getTheOther());
//
//                                if(entities!=null&&entities.size()>0){
//                                    friendEntity.setRecentChat(entities.get(0).getText());
//                                    friendEntity.setRecentTime(entities.get(0).getTime());
//                                }
//
//                                return friendEntity;
//                            }
//                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<FriendEntity>() {
                            @Override
                            public void accept(FriendEntity friendEntity) throws Exception {
                                Log.d(TAG, "init friend list: " + new Gson().toJson(friendEntity));

                                chatListDao.insert(friendEntity);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "accept: ", throwable);
                            }
                        });
    }



}
