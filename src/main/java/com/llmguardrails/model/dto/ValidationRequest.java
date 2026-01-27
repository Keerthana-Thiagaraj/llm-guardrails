package com.llmguardrails.model.dto;

import jakarta.validation.constraints.NotBlank;

public class ValidationRequest {
    @NotBlank(message = "Prompt cannot be blank")
    private String prompt;

    public ValidationRequest() {}

    public ValidationRequest(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}

