package com.loviagin.rollic.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loviagin.rollic.R;
import com.loviagin.rollic.workers.GPTPrompt;
import com.loviagin.rollic.workers.GPTResponse;
import com.loviagin.rollic.workers.OpenAIAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaydPostAddActivity extends AppCompatActivity {

    private Button button;
    private EditText editText;
    private TextView textView;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payd_post_add);
        button = findViewById(R.id.submitButton);
        editText = findViewById(R.id.topicEditText);
        textView = findViewById(R.id.resultTextView);

        String apiKey = "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";

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = editText.getText().toString();
                promptGPT(inputText);
            }
        });
    }

    private void promptGPT(String prompt) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/v1/engines/davinci/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenAIAPI openAIAPI = retrofit.create(OpenAIAPI.class);

        Call<GPTResponse> call = openAIAPI.getCompletion("Bearer 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", new GPTPrompt(prompt, 150));

        call.enqueue(new Callback<GPTResponse>() {
            @Override
            public void onResponse(Call<GPTResponse> call, Response<GPTResponse> response) {
                if (!response.isSuccessful()) {
                    textView.setText("Code: " + response.code());
                    return;
                }

                final GPTResponse gptResponse = response.body();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String content = "";
                        content += "Output: " + gptResponse.getChoices().get(0).getText();

                        textView.setText(content);
                    }
                }, 10000);

                call.cancel();
            }

            @Override
            public void onFailure(Call<GPTResponse> call, Throwable t) {
                textView.setText(t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}