package com.loviagin.rollic.activities;

import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.username;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loviagin.rollic.R;
import com.loviagin.rollic.UserData;
import com.loviagin.rollic.models.User;

public class AccountActivity extends AppCompatActivity {

    private ImageButton buttonHome, buttonAccount;
    private FloatingActionButton buttonAdd;
    private TextView textViewUsername, textViewName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        buttonHome = findViewById(R.id.bHome);
        buttonAccount = findViewById(R.id.bAccount);
        buttonAdd = findViewById(R.id.bAdd);
        textViewUsername = findViewById(R.id.tvUsernameAccount);
        textViewName = findViewById(R.id.tvNameAccount);

        buttonAccount.setColorFilter(R.color.black);
        buttonHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        buttonAdd.setColorFilter(R.color.white);
        textViewUsername.setText(username);
        textViewName.setText(name);
    }
}