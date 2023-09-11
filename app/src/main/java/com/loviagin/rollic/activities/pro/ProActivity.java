package com.loviagin.rollic.activities.pro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.loviagin.rollic.R;

public class ProActivity extends AppCompatActivity {

    private ImageButton buttonClose;
    private Button buttonJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro);

        buttonClose = findViewById(R.id.ibClosePro);
        buttonJoin = findViewById(R.id.bJoinPro);

        buttonClose.setOnClickListener(view -> finish());
        buttonJoin.setOnClickListener(view -> {});

//        AlfaPay alfaPay = new AlfaPay(this, "YOUR_API_KEY");
    }
}