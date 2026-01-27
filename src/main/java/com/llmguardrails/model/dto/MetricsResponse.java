package com.llmguardrails.model.dto;

public class MetricsResponse {
    private Long totalRequests;
    private Long allowed;
    private Long blocked;
    private Long redacted;

    public MetricsResponse() {}

    public MetricsResponse(Long totalRequests, Long allowed, Long blocked, Long redacted) {
        this.totalRequests = totalRequests;
        this.allowed = allowed;
        this.blocked = blocked;
        this.redacted = redacted;
    }

    public Long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Long getAllowed() {
        return allowed;
    }

    public void setAllowed(Long allowed) {
        this.allowed = allowed;
    }

    public Long getBlocked() {
        return blocked;
    }

    public void setBlocked(Long blocked) {
        this.blocked = blocked;
    }

    public Long getRedacted() {
        return redacted;
    }

    public void setRedacted(Long redacted) {
        this.redacted = redacted;
    }
}

