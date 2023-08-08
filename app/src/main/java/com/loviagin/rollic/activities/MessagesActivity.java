package com.loviagin.rollic.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.loviagin.rollic.R;

public class MessagesActivity extends AppCompatActivity {

    private ImageButton buttonMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        findViewById(R.id.bMessage).setVisibility(View.INVISIBLE);
        buttonMessages = findViewById(R.id.bNotifications);

        buttonMessages.setImageDrawable(getResources().getDrawable(R.drawable.fi_rr_back));
        buttonMessages.setOnClickListener(view -> finish());
    }
}