package com.llmguardrails.service;

import com.llmguardrails.analyzer.PromptAnalyzer;
import com.llmguardrails.decision.DecisionEngine;
import com.llmguardrails.model.AnalysisResult;
import com.llmguardrails.model.Decision;
import com.llmguardrails.model.dto.ValidationRequest;
import com.llmguardrails.model.dto.ValidationResponse;
import com.llmguardrails.redaction.RedactionService;
import com.llmguardrails.repository.IMetricsRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuardService {

    private static final Logger log = LoggerFactory.getLogger(GuardService.class);

    private final PromptAnalyzer promptAnalyzer;
    private final DecisionEngine decisionEngine;
    private final RedactionService redactionService;
    private final IMetricsRepository metricsRepository;

    public GuardService(
            PromptAnalyzer promptAnalyzer,
            DecisionEngine decisionEngine,
            RedactionService redactionService,
            IMetricsRepository metricsRepository
    ) {
        this.promptAnalyzer = promptAnalyzer;
        this.decisionEngine = decisionEngine;
        this.redactionService = redactionService;
        this.metricsRepository = metricsRepository;
    }
    
    public ValidationResponse validate(ValidationRequest request) {
        String prompt = request.getPrompt();
        log.info("Validating prompt (length: {})", prompt.length());
        
        // Analyze the prompt
        AnalysisResult analysisResult = promptAnalyzer.analyze(prompt);
        
        // Make decision
        Decision decision = decisionEngine.makeDecision(analysisResult);
        
        // Redact if needed
        String redactedPrompt = null;
        if (decision == Decision.REDACT) {
            List<com.llmguardrails.model.RuleMatch> redactMatches = analysisResult.getRuleMatches().stream()
                    .filter(m -> m.getSeverity() < 0.7) // Only redact rules, not block rules
                    .collect(Collectors.toList());
            redactedPrompt = redactionService.redactPrompt(prompt, redactMatches);
        }
        
        // Record metrics
        metricsRepository.recordRequest(decision);
        
        // Build response
        ValidationResponse response = new ValidationResponse(
                decision,
                analysisResult.getCombinedScore(),
                analysisResult.getReasons(),
                redactedPrompt
        );
        
        log.info("Validation complete: decision={}, riskScore={}", decision, analysisResult.getCombinedScore());
        return response;
    }
}

