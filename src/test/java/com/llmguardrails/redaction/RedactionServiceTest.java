package com.llmguardrails.redaction;

import com.llmguardrails.config.GuardrailsConfig;
import com.llmguardrails.model.RuleMatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RedactionServiceTest {

    private RedactionService redactionService;
    private GuardrailsConfig guardrailsConfig;

    @BeforeEach
    void setUp() {
        guardrailsConfig = new GuardrailsConfig();
        GuardrailsConfig.RedactionConfig redactionConfig = new GuardrailsConfig.RedactionConfig();
        redactionConfig.setEnabled(true);
        redactionConfig.setReplacementText("[REDACTED]");
        guardrailsConfig.setRedaction(redactionConfig);
        
        redactionService = new RedactionService(guardrailsConfig);
    }

    @Test
    void testRedactPrompt_WithEmail_ShouldRedact() {
        String prompt = "Contact me at user@example.com";
        RuleMatch match = new RuleMatch(
                "Email Pattern",
                "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b",
                0.5,
                "Detects email addresses",
                "user@example.com"
        );
        
        String redacted = redactionService.redactPrompt(prompt, List.of(match));
        
        assertTrue(redacted.contains("[REDACTED]"));
        assertFalse(redacted.contains("user@example.com"));
    }

    @Test
    void testRedactPrompt_WithMultipleMatches_ShouldRedactAll() {
        String prompt = "Email: user@example.com and phone: 555-123-4567";
        RuleMatch emailMatch = new RuleMatch(
                "Email Pattern",
                "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b",
                0.5,
                "Detects email",
                "user@example.com"
        );
        RuleMatch phoneMatch = new RuleMatch(
                "Phone Pattern",
                "\\b(\\+?1[-.]?)?\\(?[0-9]{3}\\)?[-.]?[0-9]{3}[-.]?[0-9]{4}\\b",
                0.4,
                "Detects phone",
                "555-123-4567"
        );
        
        String redacted = redactionService.redactPrompt(prompt, List.of(emailMatch, phoneMatch));
        
        assertTrue(redacted.contains("[REDACTED]"));
        assertFalse(redacted.contains("user@example.com"));
    }

    @Test
    void testRedactPrompt_WithNoMatches_ShouldReturnOriginal() {
        String prompt = "Hello, world!";
        String redacted = redactionService.redactPrompt(prompt, List.of());
        
        assertEquals(prompt, redacted);
    }

    @Test
    void testRedactPrompt_WithNullMatches_ShouldReturnOriginal() {
        String prompt = "Hello, world!";
        String redacted = redactionService.redactPrompt(prompt, null);
        
        assertEquals(prompt, redacted);
    }

    @Test
    void testRedactPrompt_WithRedactionDisabled_ShouldReturnOriginal() {
        guardrailsConfig.getRedaction().setEnabled(false);
        redactionService = new RedactionService(guardrailsConfig);
        
        String prompt = "Contact me at user@example.com";
        RuleMatch match = new RuleMatch(
                "Email Pattern",
                "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b",
                0.5,
                "Detects email",
                "user@example.com"
        );
        
        String redacted = redactionService.redactPrompt(prompt, List.of(match));
        
        assertEquals(prompt, redacted);
    }

    @Test
    void testRedactPrompt_WithInvalidPattern_ShouldHandleGracefully() {
        String prompt = "Test prompt";
        RuleMatch invalidMatch = new RuleMatch(
                "Invalid Pattern",
                "[invalid regex (",
                0.5,
                "Invalid",
                "test"
        );
        
        // Should not throw exception
        assertDoesNotThrow(() -> {
            String redacted = redactionService.redactPrompt(prompt, List.of(invalidMatch));
            assertNotNull(redacted);
        });
    }
}

