package com.loviagin.rollic.activities;

import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.username;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loviagin.rollic.Constants;
import com.loviagin.rollic.R;
import com.loviagin.rollic.UserData;

public class RegisterScreenActivity extends AppCompatActivity {

    private Button buttonUpload, buttonSkip, buttonSave;
    private EditText editTextName, editTextNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redister_screen);

        findViewById(R.id.bNotifications).setVisibility(View.GONE);
        findViewById(R.id.bMessage).setVisibility(View.GONE);
        buttonSkip = findViewById(R.id.bSkipRegister);
        buttonSave = findViewById(R.id.bSaveRegister);
        editTextName = findViewById(R.id.etNameRegister);
        editTextNickname = findViewById(R.id.etNicknameRegister);
        editTextNickname.setText(username);
        editTextName.setText(name);

        buttonSkip.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(USERS_COLLECTION).document(uid).update("f_name", username).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    name = username;
                    startActivity(new Intent(RegisterScreenActivity.this, AccountActivity.class));
                }
            });
        });

        buttonSave.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String name0 = editTextName.getText() == null ? username : editTextName.getText().toString().trim();
            String nickname = editTextNickname.getText() == null ? username : editTextNickname.getText().toString().trim();
            db.collection(USERS_COLLECTION).document(uid).update("f_name", name0, "username", nickname).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    name = name0;
                    username = nickname;
                    startActivity(new Intent(RegisterScreenActivity.this, AccountActivity.class));
                }
            });
        });
    }
}