package com.llmguardrails.model;

import java.util.List;

public class AnalysisResult {
    private Double ruleBasedScore;
    private Double aiScore;
    private Double combinedScore;
    private List<RuleMatch> ruleMatches;
    private List<String> reasons;

    public AnalysisResult() {}

    public AnalysisResult(
            Double ruleBasedScore,
            Double aiScore,
            Double combinedScore,
            List<RuleMatch> ruleMatches,
            List<String> reasons
    ) {
        this.ruleBasedScore = ruleBasedScore;
        this.aiScore = aiScore;
        this.combinedScore = combinedScore;
        this.ruleMatches = ruleMatches;
        this.reasons = reasons;
    }

    public Double getRuleBasedScore() {
        return ruleBasedScore;
    }

    public void setRuleBasedScore(Double ruleBasedScore) {
        this.ruleBasedScore = ruleBasedScore;
    }

    public Double getAiScore() {
        return aiScore;
    }

    public void setAiScore(Double aiScore) {
        this.aiScore = aiScore;
    }

    public Double getCombinedScore() {
        return combinedScore;
    }

    public void setCombinedScore(Double combinedScore) {
        this.combinedScore = combinedScore;
    }

    public List<RuleMatch> getRuleMatches() {
        return ruleMatches;
    }

    public void setRuleMatches(List<RuleMatch> ruleMatches) {
        this.ruleMatches = ruleMatches;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }
}

