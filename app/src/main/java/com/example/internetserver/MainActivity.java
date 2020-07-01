package com.example.internetserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.app.admin.DelegatedAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    EditText editTextPrettyName;
    TextView prettyName;
    TextView userName;
    Button button;
    Button buttonUpdatePrettyName;
    Button buttonImage;
    TextView textView;

    String stringImage = "";
    String stringPrettyName = "";
    String userTokenPath = "";
    String userToken = "";
    final static String USERNAME = "userName";
    final static String SP = "server";
    final static String TOKEN = "token";
    final static String PRETTY_NAME = "pretty_name";
    final static String DATA = "data";
    final static String IMAGE_URL = "image_url";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.buttonImage = findViewById(R.id.buttonImage);
        this.button = findViewById(R.id.button);
        this.editText = findViewById(R.id.editText);
        this.userName = findViewById(R.id.userName);
        this.prettyName = findViewById(R.id.prettyName);
        this.textView = findViewById(R.id.textView);
        this.buttonUpdatePrettyName = findViewById(R.id.buttonPrettyName);
        this.editTextPrettyName = findViewById(R.id.editTextPrettyName);
        sharedPreferences = getSharedPreferences(SP, MODE_PRIVATE);
        checkSP();

        getPathAndToken();
        updatePrettyName();
        sendToShowImage();

    }

    private void sendToShowImage()
    {
        this.buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                intent.putExtra(IMAGE_URL, MainActivity.this.stringImage);
                startActivity(intent);

            }
        });
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) this.getSystemService(
                        INPUT_METHOD_SERVICE);

        if (inputMethodManager != null && inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(
                    this.getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void checkSP()
    {
        String token = this.sharedPreferences.getString(TOKEN, "");
        if((!token.equals("")))
        {
            this.textView.setText(token);
            this.userToken = token;
            enqueueUser();
        }

    }

    private void updatePrettyName()
    {
        this.buttonUpdatePrettyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                MainActivity.this.stringPrettyName = MainActivity.this.editTextPrettyName.getText().toString();
                //MainActivity.this.editTextPrettyName.setText("");
                enqueuePostPrettyName();
            }
        });
    }

    private void getPathAndToken()
    {
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                MainActivity.this.userTokenPath = MainActivity.this.editText.getText().toString();
                //MainActivity.this.editText.setText("");
                enqueueUserToken();

            }
        });

    }

    private void enqueueSlash()
    {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build();

        OneTimeWorkRequest getRequest = new OneTimeWorkRequest.Builder(GetApiWorker.class)
                .setConstraints(constraints).build();

        WorkManager workManager = WorkManager.getInstance(this);

        workManager.enqueue(getRequest);

        UUID getRequestId = getRequest.getId();

        SlashStatus(workManager, getRequestId);
    }

    private void enqueueUserToken()
    {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build();


        Data.Builder data = new Data.Builder(); //Send data to WorkManager
        data.putString(USERNAME, this.userTokenPath);

        OneTimeWorkRequest getUserTokenRequest = new OneTimeWorkRequest.Builder(GetUserTokenApiWorker.class)
                .setConstraints(constraints).setInputData(data.build()).build();


        WorkManager workManager = WorkManager.getInstance(this);

        workManager.enqueue(getUserTokenRequest);


        UUID getUserTokenId = getUserTokenRequest.getId();

        UserTokenStatus(workManager, getUserTokenId);
    }

    private void enqueueUser()
    {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build();


        Data.Builder data = new Data.Builder(); //Send data to WorkManager
        data.putString(TOKEN, this.userToken);

        OneTimeWorkRequest getUserRequest = new OneTimeWorkRequest.Builder(GetUserApiWorker.class)
                .setConstraints(constraints).setInputData(data.build()).build();


        WorkManager workManager = WorkManager.getInstance(this);

        workManager.enqueue(getUserRequest);


        UUID getUserTokenId = getUserRequest.getId();

        UserStatus(workManager, getUserTokenId);
    }

    private void enqueuePostPrettyName()
    {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build();

        Data.Builder data = new Data.Builder(); //Send data to WorkManager
        data.putString(PRETTY_NAME, this.stringPrettyName);
        data.putString(TOKEN, this.userToken);

        OneTimeWorkRequest postPrettyNameRequest = new OneTimeWorkRequest.Builder(PostPrettyNameWorker.class)
                .setConstraints(constraints).setInputData(data.build()).build();

        WorkManager workManager = WorkManager.getInstance(this);

        workManager.enqueue(postPrettyNameRequest);

        UUID postRequestId = postPrettyNameRequest.getId();

        PostPrettyNameStatus(workManager, postRequestId);

    }

    private void PostPrettyNameStatus(WorkManager workManager, UUID id)
    {
        workManager.getWorkInfoByIdLiveData(id).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if(workInfo == null)
                {
                    return;
                }

                if(workInfo.getState() == WorkInfo.State.FAILED)
                {
                    MainActivity.this.prettyName.setText("Error 404");
                    //Update UI if FAILED or server
                }
                else if(workInfo.getState() == WorkInfo.State.SUCCEEDED){
                    Data data = workInfo.getOutputData();
                    Gson gson = new Gson();
                    UserResponse userResponse = gson.fromJson(data.getKeyValueMap().get(MainActivity.DATA).toString(), UserResponse.class);
                    String userName = userResponse.data.username;
                    String prettyName = userResponse.data.pretty_name;
                    //String imageURL = userResponse.data.image_url;
                    MainActivity.this.prettyName.setText(prettyName);
                }
                else{
                    //Update UI to display "loading"
                    MainActivity.this.prettyName.setText("Loading for server...");
                }
            }
        });

    }

    private void UserTokenStatus(WorkManager workManager, UUID id)
    {
        workManager.getWorkInfoByIdLiveData(id).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if(workInfo == null)
                {
                    return;
                }

                if(workInfo.getState() == WorkInfo.State.FAILED)
                {
                    MainActivity.this.textView.setText("Error 404");
                    //Update UI if FAILED or server
                }
                else if(workInfo.getState() == WorkInfo.State.SUCCEEDED){
                    Data data = workInfo.getOutputData();
                    MainActivity.this.sharedPreferences.edit()
                            .putString(TOKEN, data.getKeyValueMap().get(DATA).toString()).apply();

                    MainActivity.this.userToken = data.getKeyValueMap().get(DATA).toString();

                    MainActivity.this.textView.setText(data.getKeyValueMap().get("data").toString());

                    enqueueUser();
                }
                else{
                    //Update UI to display "loading"
                    MainActivity.this.textView.setText("Loading for server...");
                }
            }
        });

    }

    private void UserStatus(WorkManager workManager, UUID id)
    {
        workManager.getWorkInfoByIdLiveData(id).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if(workInfo == null)
                {
                    return;
                }

                if(workInfo.getState() == WorkInfo.State.FAILED)
                {
                    MainActivity.this.userName.setText("Error 404");
                    //Update UI if FAILED or server
                }
                else if(workInfo.getState() == WorkInfo.State.SUCCEEDED){
                    Data data = workInfo.getOutputData();
                    Gson gson = new Gson();
                    UserResponse userResponse = gson.fromJson(data.getKeyValueMap().get(MainActivity.DATA).toString(), UserResponse.class);
                    String userName = userResponse.data.username;
                    String prettyName = userResponse.data.pretty_name;
                    MainActivity.this.stringImage = userResponse.data.image_url;
                    //String imageURL = userResponse.data.image_url;
//                    if(prettyName.equals(""))
//                    {
//                        MainActivity.this.userName.setText("Welcome peasant: " + userName);
//                        MainActivity.this.prettyName.setText("");
//                    }
//                    else{
//                        MainActivity.this.userName.setText("");
//                        MainActivity.this.prettyName.setText(prettyName);
//                    }

                    MainActivity.this.userName.setText("Welcome peasant: " + userName);
                    MainActivity.this.prettyName.setText(prettyName);

                }
                else{
                    //Update UI to display "loading"
                    MainActivity.this.userName.setText("Loading for server...");
                    MainActivity.this.prettyName.setText("Loading for server...");
                }
            }
        });

    }

    private void SlashStatus(WorkManager workManager, UUID id)
    {

        workManager.getWorkInfoByIdLiveData(id).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if(workInfo == null)
                {
                    return;
                }

                if(workInfo.getState() == WorkInfo.State.FAILED)
                {
                    //MainActivity.this.textView.setText("Error 404");
                    //Update UI if FAILED or server
                }
                else if(workInfo.getState() == WorkInfo.State.SUCCEEDED){
                    //MainActivity.this.textView.setText(workInfo.getOutputData().getKeyValueMap().get("data").toString());

                }
                else{
                    //Update UI to display "loading"
                    //MainActivity.this.textView.setText("Loading for server...");
                }
            }
        });

    }
}
