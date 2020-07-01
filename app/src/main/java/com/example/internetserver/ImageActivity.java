package com.example.internetserver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        this.imageView = findViewById(R.id.imageView);

        String imageUrl = getIntent().getStringExtra(MainActivity.IMAGE_URL);
        String baseImageUrl = ServerHolder.baseUrl + imageUrl;
        Glide.with(this).load(baseImageUrl).into(this.imageView);

    }
}
