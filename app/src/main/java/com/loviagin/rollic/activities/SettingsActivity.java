package com.loviagin.rollic.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.loviagin.rollic.R;
import com.loviagin.rollic.workers.LanguageUtils;

public class SettingsActivity extends AppCompatActivity {

    private CardView cardViewLanguage, cardViewDelete, cardViewAbout;
    private PopupWindow popupWindow;
    private Spinner spinner;
    private String selectedLanguage;
    private ImageButton buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        cardViewAbout = findViewById(R.id.cvAboutSettings);
        cardViewDelete = findViewById(R.id.cvDeleteSettings);
        cardViewLanguage = findViewById(R.id.cvLanguageSettings);
        findViewById(R.id.bMessage).setVisibility(View.INVISIBLE);
        buttonBack = findViewById(R.id.bNotifications);

        buttonBack.setImageDrawable(getResources().getDrawable(R.drawable.fi_rr_back));
        buttonBack.setOnClickListener(v -> startActivity(new Intent(this, AccountActivity.class)));

        cardViewAbout.setOnClickListener(v -> startActivity(new Intent(this, AboutAppActivity.class)));
        cardViewLanguage.setOnClickListener(v -> {
            showPopup();
        });

        cardViewDelete.setOnClickListener(v -> openWebPage("https://rollic.loviagin.com/delete-account/"));
    }

    private void showPopup() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_window, null);

        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        spinner = (Spinner) popupView.findViewById(R.id.spinner);
        Button applyButton = (Button) popupView.findViewById(R.id.apply_button);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguage = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (selectedLanguage){
                    case "English":
                        LanguageUtils.setAppLocale(getResources(), "en");
                        break;
                    case "Русский":
                        LanguageUtils.setAppLocale(getResources(), "ru");
                        break;
                }
                popupWindow.dismiss();
                startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            }
        });

        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(cardViewLanguage, Gravity.CENTER, 0, 0);
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
