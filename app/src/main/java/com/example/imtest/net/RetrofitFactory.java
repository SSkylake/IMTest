package com.example.imtest.net;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private Retrofit sInstance;
    private static final String baseUrl = "http://ssdutlrz.club:8080/IMServer/";

    private RetrofitFactory(){
        sInstance = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static Retrofit getInstance(){
        return HOLDER.FACTORY.sInstance;
    }

    private static class HOLDER{
        private static final RetrofitFactory FACTORY = new RetrofitFactory();
    }
}
