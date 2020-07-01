package com.example.internetserver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class GetUserApiWorker extends Worker {
    public GetUserApiWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public Result doWork() {
        String token = getInputData().getString(MainActivity.TOKEN);
        String auth = "token ";
        Call<UserResponse> callToGetUser = ServerHolder.getInstance().server.callGetUser(auth + token);
        Response<UserResponse> response = null;
        try {
            response = callToGetUser.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert response != null;
        Log.d("GetSlash Worker", "got response: " + response.code());
        if(response.code() != 200 || !response.isSuccessful())
        {
            return Result.failure();
        }
        UserResponse result = response.body();
        if(result == null)
        {
            return Result.failure();
        }
        Log.d("GetSlash Worker", "got response: " + result.data);
        Gson gson = new Gson();
        String json  = gson.toJson(result);

        return Result.success(new Data.Builder().putString(MainActivity.DATA, json).build());
    }
}
