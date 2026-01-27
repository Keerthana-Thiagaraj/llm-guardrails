package com.llmguardrails.model;

public class RuleMatch {
    private String ruleName;
    private String pattern;
    private Double severity;
    private String description;
    private String matchedText;

    public RuleMatch() {}

    public RuleMatch(String ruleName, String pattern, Double severity, String description, String matchedText) {
        this.ruleName = ruleName;
        this.pattern = pattern;
        this.severity = severity;
        this.description = description;
        this.matchedText = matchedText;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Double getSeverity() {
        return severity;
    }

    public void setSeverity(Double severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMatchedText() {
        return matchedText;
    }

    public void setMatchedText(String matchedText) {
        this.matchedText = matchedText;
    }
}

