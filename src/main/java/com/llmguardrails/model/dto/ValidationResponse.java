package com.llmguardrails.model.dto;

import com.llmguardrails.model.Decision;

import java.util.List;

public class ValidationResponse {
    private Decision decision;
    private Double riskScore;
    private List<String> reasons;
    private String redactedPrompt;

    public ValidationResponse() {}

    public ValidationResponse(Decision decision, Double riskScore, List<String> reasons, String redactedPrompt) {
        this.decision = decision;
        this.riskScore = riskScore;
        this.reasons = reasons;
        this.redactedPrompt = redactedPrompt;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }

    public String getRedactedPrompt() {
        return redactedPrompt;
    }

    public void setRedactedPrompt(String redactedPrompt) {
        this.redactedPrompt = redactedPrompt;
    }
}

