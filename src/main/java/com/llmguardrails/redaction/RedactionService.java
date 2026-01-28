package com.llmguardrails.redaction;

import com.llmguardrails.config.GuardrailsConfig;
import com.llmguardrails.model.RuleMatch;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RedactionService {

    private static final Logger log = LoggerFactory.getLogger(RedactionService.class);

    private final GuardrailsConfig guardrailsConfig;

    public RedactionService(GuardrailsConfig guardrailsConfig) {
        this.guardrailsConfig = guardrailsConfig;
    }
    
    public String redactPrompt(String prompt, List<RuleMatch> redactMatches) {
        if (!guardrailsConfig.getRedaction().getEnabled()) {
            return prompt;
        }
        
        if (redactMatches == null || redactMatches.isEmpty()) {
            return prompt;
        }
        
        String redactedPrompt = prompt;
        String replacement = guardrailsConfig.getRedaction().getReplacementText();
        
        for (RuleMatch match : redactMatches) {
            try {
                Pattern pattern = Pattern.compile(match.getPattern());
                Matcher matcher = pattern.matcher(redactedPrompt);
                redactedPrompt = matcher.replaceAll(replacement);
            } catch (Exception e) {
                log.warn("Failed to redact pattern: {}", match.getPattern(), e);
            }
        }
        
        log.debug("Redacted prompt: {} matches replaced", redactMatches.size());
        return redactedPrompt;
    }
}

