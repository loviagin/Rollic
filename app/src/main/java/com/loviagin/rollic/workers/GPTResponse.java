package com.loviagin.rollic.workers;

import java.util.List;

public class GPTResponse {
    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }

    public class Choice {
        private String text;

        public String getText() {
            return text;
        }
    }
}