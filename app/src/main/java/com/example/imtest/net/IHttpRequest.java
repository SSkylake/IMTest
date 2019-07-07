package com.example.imtest.net;

import com.example.imtest.entity.ChatHistoryEntity;
import com.example.imtest.entity.FriendEntity;
import com.example.imtest.entity.UserEntity;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IHttpRequest {


    @GET("register_or_log")
    Observable<StandardResponse> registerOrLog(@Query("username")String username,@Query("password")String password);

    @GET("get_friend_list")
    Observable<List<FriendEntity>> getFriendList(@Query("username")String username);

    @GET("add_friend")
    Observable<StandardResponse> addFriend(@Query("host")String host,@Query("friend")String friend);

    @GET("get_chat_history")
    Observable<List<ChatHistoryEntity>> getChatHistory(@Query("host")String host,@Query("the_other")String theOther);

    @GET("reset_non_reading_count")
    Observable<StandardResponse> resetNonReadingCount(@Query("one")String host,@Query("the_other")String friend);



}
