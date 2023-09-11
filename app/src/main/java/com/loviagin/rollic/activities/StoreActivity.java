package com.loviagin.rollic.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.loviagin.rollic.R;

public class StoreActivity extends AppCompatActivity {

    private ImageButton buttonHome, buttonSearch, buttonVideo, buttonAccount, buttonMessages, buttonNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        buttonHome = findViewById(R.id.bHome);
        buttonSearch = findViewById(R.id.bSearch);
        buttonVideo = findViewById(R.id.bVideo);
        buttonAccount = findViewById(R.id.bAccount);
        buttonMessages = findViewById(R.id.bMessage);
        buttonNotifications = findViewById(R.id.bNotifications);

        buttonHome.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        buttonSearch.setOnClickListener(view -> startActivity(new Intent(this, SearchActivity.class)));
        buttonVideo.setOnClickListener(view -> startActivity(new Intent(this, VideoActivity.class)));
        buttonAccount.setOnClickListener(view -> startActivity(new Intent(this, AccountActivity.class)));
        buttonMessages.setOnClickListener(view -> startActivity(new Intent(this, ChatActivity.class)));
        buttonNotifications.setOnClickListener(view -> startActivity(new Intent(this, NotificationActivity.class)));
    }
}