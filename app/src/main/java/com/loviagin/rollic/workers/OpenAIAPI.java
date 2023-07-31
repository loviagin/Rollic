package com.loviagin.rollic.workers;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface OpenAIAPI {
    @POST("completions")
    Call<GPTResponse> getCompletion(@Header("Authorization") String authHeader, @Body GPTPrompt prompt);
}