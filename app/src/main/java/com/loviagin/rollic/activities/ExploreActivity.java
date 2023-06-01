package com.loviagin.rollic.activities;

import static com.loviagin.rollic.models.Objects.mAuth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.loviagin.rollic.R;

public class ExploreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        mAuth.signOut();
        startActivity(new Intent(this, MainActivity.class));
    }
}