package com.loviagin.rollic.workers;

import android.util.Log;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenAIApi {

    private static final String API_URL = "https://api.openai.com/v1/engines/davinci/completions";
    private static final String API_KEY = "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";

    public static String getResponse(String prompt) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"prompt\":\"" + prompt + "\",\"max_tokens\":150}");
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        JSONObject jsonResponse = null;
        try {
            Response response = client.newCall(request).execute();
            jsonResponse = new JSONObject(response.body().string());
            return jsonResponse.getJSONObject("choices").getJSONArray("text").getString(0);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("OpenAI Response", String.valueOf(jsonResponse));
            return "Ошибка: " + e.getMessage();
        }
    }
}
