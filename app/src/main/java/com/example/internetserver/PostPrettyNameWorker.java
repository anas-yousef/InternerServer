package com.example.internetserver;

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

public class PostPrettyNameWorker extends Worker {
    public PostPrettyNameWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SetUserPrettyNameRequest request = new SetUserPrettyNameRequest(getInputData().getString(MainActivity.PRETTY_NAME));
        String token = "token " + getInputData().getString(MainActivity.TOKEN);
        Call<UserResponse> callToPostPrettyName = ServerHolder.getInstance().server.callUpdatePrettyName(request, token);
        Response<UserResponse> response = null;
        try {
            response = callToPostPrettyName.execute();
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
