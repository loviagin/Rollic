package com.loviagin.rollic.workers;

public class GPTPrompt {
    private String prompt;
    private int max_tokens;

    public GPTPrompt(String prompt, int max_tokens) {
        this.prompt = prompt;
        this.max_tokens = max_tokens;
    }

    public String getPrompt() {
        return prompt;
    }

    public int getMaxTokens() {
        return max_tokens;
    }
}