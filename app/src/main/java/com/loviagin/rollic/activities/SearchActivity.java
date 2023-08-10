package com.loviagin.rollic.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loviagin.rollic.R;

public class SearchActivity extends AppCompatActivity {

    private ImageButton buttonHome, buttonSearch, buttonVideo, buttonAccount, buttonMessages, buttonNotifications;
    private FloatingActionButton buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        buttonHome = findViewById(R.id.bHome);
        buttonSearch = findViewById(R.id.bSearch);
        buttonVideo = findViewById(R.id.bVideo);
        buttonAccount = findViewById(R.id.bAccount);
        buttonMessages = findViewById(R.id.bMessage);
        buttonNotifications = findViewById(R.id.bNotifications);
        buttonAdd = findViewById(R.id.bAdd);

        buttonHome.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        buttonSearch.setOnClickListener(view -> startActivity(new Intent(this, SearchActivity.class)));
        buttonVideo.setOnClickListener(view -> startActivity(new Intent(this, VideoActivity.class)));
        buttonAccount.setOnClickListener(view -> startActivity(new Intent(this, AccountActivity.class)));
        buttonMessages.setOnClickListener(view -> startActivity(new Intent(this, MessagesActivity.class)));
        buttonNotifications.setOnClickListener(view -> startActivity(new Intent(this, NotificationActivity.class)));
        buttonSearch.setColorFilter(R.color.black);
        buttonAdd.setColorFilter(R.color.white);
        buttonAdd.setOnClickListener(v -> startActivity(new Intent(this, AddActivity.class)));
    }
}