package com.loviagin.rollic.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.loviagin.rollic.R;

public class AboutAppActivity extends AppCompatActivity {

    private ImageButton buttonBack;
    private TextView textViewLinkIlia, textViewLinkCorp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        findViewById(R.id.bMessage).setVisibility(View.INVISIBLE);
        buttonBack = findViewById(R.id.bNotifications);
        textViewLinkCorp = findViewById(R.id.tvLinkCorpAbout);
        textViewLinkIlia = findViewById(R.id.tvLinkIliaAbout);

        buttonBack.setImageDrawable(getResources().getDrawable(R.drawable.fi_rr_back));
        buttonBack.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        textViewLinkCorp.setOnClickListener(v -> {
            openWebPage("https://loviagin.com/");
        });
    }

    private void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Log.e("TAG", "u1" + webpage);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        Log.e("TAG", "u2" + intent);
        Log.e("TAG", "u3");
        startActivity(intent);
    }
}