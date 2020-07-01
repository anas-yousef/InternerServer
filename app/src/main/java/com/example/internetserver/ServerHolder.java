package com.example.internetserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerHolder {
    final static String baseUrl = "https://hujipostpc2019.pythonanywhere.com";
    private static ServerHolder instance = null;

    synchronized static ServerHolder getInstance(){
        if (instance == null) {
            instance = new ServerHolder();
        }
        return instance;
    }

    public final MyServer server;

    private ServerHolder(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(new OkHttpClient())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        this.server = retrofit.create(MyServer.class);
    }
}