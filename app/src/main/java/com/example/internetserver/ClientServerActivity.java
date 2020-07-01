package com.example.internetserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.os.Bundle;

import java.util.UUID;

public class ClientServerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_server);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build();

        OneTimeWorkRequest getRequest = new OneTimeWorkRequest.Builder(GetApiWorker.class)
                .setConstraints(constraints).build();

        WorkManager workManager = WorkManager.getInstance(this);

        workManager.enqueue(getRequest);

        UUID workId = getRequest.getId();

        workManager.getWorkInfoByIdLiveData(workId).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if(workInfo == null)
                {
                    return;
                }

                if(workInfo.getState() == WorkInfo.State.FAILED)
                {
                    int x = 0;
                    //Update UI if FAILED or server
                }
                else if(workInfo.getState() == WorkInfo.State.SUCCEEDED){
                    //Update UI if SUCCEEDED or server
                    int y = 0;
                }
                else{
                    //Update UI to display "loading"
                    int w = 0;
                }
            }
        });


    }
}
