package com.llmguardrails.decision;

import com.llmguardrails.config.GuardrailsConfig;
import com.llmguardrails.model.AnalysisResult;
import com.llmguardrails.model.Decision;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DecisionEngine {
    
    private static final Logger log = LoggerFactory.getLogger(DecisionEngine.class);

    private final GuardrailsConfig guardrailsConfig;

    public DecisionEngine(GuardrailsConfig guardrailsConfig) {
        this.guardrailsConfig = guardrailsConfig;
    }
    
    public Decision makeDecision(AnalysisResult analysisResult) {
        Double riskScore = analysisResult.getCombinedScore();
        Double blockThreshold = guardrailsConfig.getDecision().getBlockThreshold();
        Double redactThreshold = guardrailsConfig.getDecision().getRedactThreshold();
        
        log.debug("Making decision for risk score: {}, block threshold: {}, redact threshold: {}", 
                riskScore, blockThreshold, redactThreshold);
        
        if (riskScore >= blockThreshold) {
            log.info("Decision: BLOCK (risk score: {})", riskScore);
            return Decision.BLOCK;
        } else if (riskScore >= redactThreshold) {
            log.info("Decision: REDACT (risk score: {})", riskScore);
            return Decision.REDACT;
        } else {
            log.info("Decision: ALLOW (risk score: {})", riskScore);
            return Decision.ALLOW;
        }
    }
}

