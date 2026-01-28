package com.llmguardrails.llm;

import com.llmguardrails.config.GuardrailsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class LLMServiceConfig {
    
    private static final Logger log = LoggerFactory.getLogger(LLMServiceConfig.class);

    private final GuardrailsConfig guardrailsConfig;

    public LLMServiceConfig(GuardrailsConfig guardrailsConfig) {
        this.guardrailsConfig = guardrailsConfig;
    }
    
    @Bean
    @Primary
    public LLMService llmService(OpenAIService openAIService, MockLLMService mockLLMService) {
        if (guardrailsConfig.getAi().getOpenai().getEnabled() 
                && guardrailsConfig.getAi().getOpenai().getApiKey() != null 
                && !guardrailsConfig.getAi().getOpenai().getApiKey().isBlank()) {
            log.info("Using OpenAI service for LLM risk analysis");
            return openAIService;
        } else {
            log.info("Using Mock LLM service (OpenAI not configured)");
            return mockLLMService;
        }
    }
}

