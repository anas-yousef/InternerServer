package com.example.internetserver;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class GetApiWorker extends Worker {


    public GetApiWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Call<TokenResponse> callToGetSlash = ServerHolder.getInstance().server.callGetSlash();
        Response<TokenResponse> response = null;
        try {
           response = callToGetSlash.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert response != null;
        Log.d("GetSlash Worker", "got response: " + response.code());
        if(response.code() != 200 || !response.isSuccessful())
        {
            return Result.failure();
        }
        TokenResponse result = response.body();
        if(result == null)
        {
            return Result.failure();
        }
        Log.d("GetSlash Worker", "got response: " + result.data);

        return Result.success(new Data.Builder().putString("data", result.data).build());

    }
}
