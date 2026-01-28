package com.llmguardrails.llm;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MockLLMService implements LLMService {

    private static final Logger log = LoggerFactory.getLogger(MockLLMService.class);

    @Override
    public Double analyzeRisk(String prompt) {
        log.debug("Mock LLM service analyzing prompt (returning 0.0)");
        return 0.0;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}

