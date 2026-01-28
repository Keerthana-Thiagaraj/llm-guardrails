package com.llmguardrails.llm;

public interface LLMService {
    /**
     * Analyzes a prompt and returns a risk score between 0.0 (safe) and 1.0 (dangerous).
     * 
     * @param prompt The prompt to analyze
     * @return Risk score between 0.0 and 1.0
     */
    Double analyzeRisk(String prompt);
    
    /**
     * Checks if the LLM service is enabled and available.
     * 
     * @return true if the service is available
     */
    boolean isEnabled();
}

